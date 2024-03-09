package com.kgg.kkchat.common.user.service;

import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.domain.vo.req.BlackReq;
import com.kgg.kkchat.common.user.domain.vo.resp.BadgeResp;
import com.kgg.kkchat.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author kgg
 * @since 2023-12-20
 */
public interface UserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, Long itemId);

    void black(BlackReq req);
}
