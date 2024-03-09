package com.kgg.kkchat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.kgg.kkchat.common.common.domain.enums.YesOrNo;
import com.kgg.kkchat.common.user.domain.entity.ItemConfig;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.domain.entity.UserBackpack;
import com.kgg.kkchat.common.user.domain.vo.resp.BadgeResp;
import com.kgg.kkchat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/23
 */
public class UserAdapter {

    public static User buildUserSave(String fromUser) {
        return User.builder().openId(fromUser).build();
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        return user;
    }

    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(user,userInfoResp);
        userInfoResp.setId(user.getId());
        userInfoResp.setModifyNameChance(modifyNameCount);
        return userInfoResp;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> byType, List<UserBackpack> backpacks, User user) {
        Set<Long> collect = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return byType.stream().map(a->{
            BadgeResp resp = new BadgeResp();
            BeanUtil.copyProperties(a,resp);
            resp.setObtain(collect.contains(a.getId())? YesOrNo.YES.getStatus():YesOrNo.NO.getStatus());
            resp.setWearing(Objects.equals(a.getId(),user.getItemId())? YesOrNo.YES.getStatus():YesOrNo.NO.getStatus());
            return resp;
        }).sorted(Comparator.comparing(BadgeResp::getWearing,Comparator.reverseOrder())
                .thenComparing(BadgeResp::getObtain))
                .collect(Collectors.toList());
    }
}
