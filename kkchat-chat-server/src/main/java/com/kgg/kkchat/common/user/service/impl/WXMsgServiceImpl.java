package com.kgg.kkchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.service.UserService;
import com.kgg.kkchat.common.user.service.WXMsgService;
import com.kgg.kkchat.common.user.service.adapter.TextBuilder;
import com.kgg.kkchat.common.user.service.adapter.UserAdapter;
import com.kgg.kkchat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/23
 */

@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {
    @Autowired
    private WebSocketService webSocketService;
    /**
     * openId和用户登录code的关系map
     */
    private static final ConcurrentHashMap<String,Integer> WAIT_AUTHORIZE_MAP=new ConcurrentHashMap<>();
    @Value("${wx.mp.callback}")
    private String callback;
    public static final String URL="https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)) {
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        //用户已经注册并授权
        if (registered && authorized) {
            //走登录成功逻辑 通过code找到给channel推送消息
            webSocketService.scanLoginSuccess(code, user.getId());
            return null;
        }
        //用户未注册，就先注册
        if (!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        //推送链接让用户授权
        WAIT_AUTHORIZE_MAP.put(openId, code);
        webSocketService.waitAuthorize(code);
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        System.out.println(authorizeUrl);
        return TextBuilder.build("请点击登录：<a href=\"" + authorizeUrl + "\">登录</a>", wxMpXmlMessage);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        //更新用户信息
        if(StrUtil.isBlank(user.getAvatar())){
            fillUserInfo(user.getId(),userInfo);
        }
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        webSocketService.scanLoginSuccess(code,user.getId());
    }

    private void fillUserInfo(Long id, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(id, userInfo);
//        try {
//            userDao.updateById(user);
//        }catch (DuplicateKeyException ignored){
//            user.setName(generateRandomName());
//            userDao.updateById(user);
//        }
        userDao.updateById(user);
    }
    private String generateRandomName() {
        // 实现生成随机名称的逻辑，可以使用 UUID 或其他方式
        return "随机名称" + UUID.randomUUID().toString();
    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("getEventKey error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }

    }
}
