package com.kgg.kkchat.common.user.service.impl;

import com.kgg.kkchat.common.common.annotation.RedissonLock;
import com.kgg.kkchat.common.common.event.UserBlackEvent;
import com.kgg.kkchat.common.common.event.UserRegisterEvent;
import com.kgg.kkchat.common.common.utils.AssertUtil;
import com.kgg.kkchat.common.user.dao.BlackDao;
import com.kgg.kkchat.common.user.dao.ItemConfigDao;
import com.kgg.kkchat.common.user.dao.UserBackpackDao;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.*;
import com.kgg.kkchat.common.user.domain.enums.BlackTypeEnum;
import com.kgg.kkchat.common.user.domain.enums.ItemEnum;
import com.kgg.kkchat.common.user.domain.enums.ItemTypeEnum;
import com.kgg.kkchat.common.user.domain.vo.req.BlackReq;
import com.kgg.kkchat.common.user.domain.vo.resp.BadgeResp;
import com.kgg.kkchat.common.user.domain.vo.resp.UserInfoResp;
import com.kgg.kkchat.common.user.service.UserService;
import com.kgg.kkchat.common.user.service.adapter.UserAdapter;
import com.kgg.kkchat.common.user.service.cache.ItemCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/23
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BlackDao blackDao;

    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        //发送物品
        //用户注册的事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer ModifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user,ModifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser,"昵称已被抢占，请换一个吧");
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem,"改名卡已用完");
        //改名字
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if(success){
            userDao.modifyName(uid,name);
        }
    }


    @Override
    public List<BadgeResp> badges(Long uid) {
        //徽章列表
        List<ItemConfig> byType = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //用户背包
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, byType.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //用户佩戴的徽章
        User user = userDao.getById(uid);

        return UserAdapter.buildBadgeResp(byType,backpacks,user);
    }

    @Override
    public void wearingBadge(Long uid, Long itemId) {
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem,"您还尚未获得这个徽章");
        ItemConfig byId = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(byId.getType(),ItemTypeEnum.BADGE.getType(),"只有徽章才能佩戴哦");
        userDao.wearingBadge(uid,itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(BlackReq req) {
        Long uid=req.getUid();
        Black user = new Black();
        user.setType(BlackTypeEnum.UID.getType());
        user.setTarget(uid.toString());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this,byId));
    }

    private void blackIp(String ip) {
        if(StringUtils.isBlank(ip)){
            return;
        }
        try {
            Black black = new Black();
            black.setType(BlackTypeEnum.IP.getType());
            black.setTarget(ip);
            blackDao.save(black);
        }catch (Exception e){

        }
    }
}
