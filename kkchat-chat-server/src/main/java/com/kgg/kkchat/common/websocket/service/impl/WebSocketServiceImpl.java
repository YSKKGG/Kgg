package com.kgg.kkchat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kgg.kkchat.common.common.event.UserOnlineEvent;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.IpInfo;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.domain.enums.RoleEnum;
import com.kgg.kkchat.common.user.service.IRoleService;
import com.kgg.kkchat.common.user.service.LoginService;
import com.kgg.kkchat.common.websocket.NettyUtil;
import com.kgg.kkchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.kgg.kkchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.kgg.kkchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.kgg.kkchat.common.websocket.domain.vo.resp.WSLoginUrl;
import com.kgg.kkchat.common.websocket.service.WebSocketService;
import com.kgg.kkchat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.representer.BaseRepresenter;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 专门管理websocket的逻辑
 * Author: Kgg
 * Date: 2023/12/21
 */

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UserDao userDao;
    @Autowired
    @Lazy
    private WxMpService wxMpService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    /**
     * 管理登录用户/游客的连接
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 临时保存code和channel的映射关系
     */
    public static final Duration DURATION = Duration.ofHours(1);
    public static final int MAXIMUM_SIZE = 1000;
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机码
        Integer code = generateLoginCode(channel);
        //微信生成带参数二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        //把二维码推送到前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void offline(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        //todo 用户下线
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        //确认链接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(uid);
        //移除code
        WAIT_LOGIN_MAP.invalidate(code);
        //调用登录模块获取token
        String token = loginService.login(uid);
        //用户登录
        loginSuccess(channel, user, token);
    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
        } else {
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {
        ONLINE_WS_MAP.forEach((channel,ext)-> {
            threadPoolTaskExecutor.execute(()-> sendMsg(channel,msg));
        });
    }

    private void loginSuccess(Channel channel, User user, String token) {
        //保存channel的对应uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        //推送登陆成功消息
        sendMsg(channel, WebSocketAdapter.buildResp(user, token,roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        //用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel,NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this,user));
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }


    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
