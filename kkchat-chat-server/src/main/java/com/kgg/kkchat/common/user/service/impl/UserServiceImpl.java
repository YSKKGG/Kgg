package com.kgg.kkchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kgg.kkchat.common.common.annotation.RedissonLock;
import com.kgg.kkchat.common.common.event.UserBlackEvent;
import com.kgg.kkchat.common.common.event.UserRegisterEvent;
import com.kgg.kkchat.common.common.utils.AssertUtil;
import com.kgg.kkchat.common.user.dao.BlackDao;
import com.kgg.kkchat.common.user.dao.ItemConfigDao;
import com.kgg.kkchat.common.user.dao.UserBackpackDao;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.dto.ItemInfoDTO;
import com.kgg.kkchat.common.user.domain.dto.SummeryInfoDTO;
import com.kgg.kkchat.common.user.domain.entity.*;
import com.kgg.kkchat.common.user.domain.enums.BlackTypeEnum;
import com.kgg.kkchat.common.user.domain.enums.ItemEnum;
import com.kgg.kkchat.common.user.domain.enums.ItemTypeEnum;
import com.kgg.kkchat.common.user.domain.vo.req.BlackReq;
import com.kgg.kkchat.common.user.domain.vo.req.ItemInfoReq;
import com.kgg.kkchat.common.user.domain.vo.req.SummeryInfoReq;
import com.kgg.kkchat.common.user.domain.vo.resp.BadgeResp;
import com.kgg.kkchat.common.user.domain.vo.resp.UserInfoResp;
import com.kgg.kkchat.common.user.service.UserService;
import com.kgg.kkchat.common.user.service.adapter.UserAdapter;
import com.kgg.kkchat.common.user.service.cache.ItemCache;
import com.kgg.kkchat.common.user.service.cache.UserCache;
import com.kgg.kkchat.common.user.service.cache.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/23
 */
@Service
@Slf4j
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

    @Autowired
    private UserCache userCache;

    @Autowired
    private UserSummaryCache userSummaryCache;

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

    @Override
    public List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req) {
        //需要前端同步的uid
        List<Long> uidList = getNeedSyncUidList(req.getReqList());
        //加载用户信息
        Map<Long, SummeryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getReqList()
                .stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummeryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {//简单做，更新时间可判断被修改
        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.getById(a.getItemId());
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return ItemInfoDTO.skip(a.getItemId());
            }
            ItemInfoDTO dto = new ItemInfoDTO();
            dto.setItemId(itemConfig.getId());
            dto.setImg(itemConfig.getImg());
            dto.setDescribe(itemConfig.getDescribe());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > infoReq.getLastModifyTime())) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }

    public void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
