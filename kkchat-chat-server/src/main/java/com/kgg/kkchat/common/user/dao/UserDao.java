package com.kgg.kkchat.common.user.dao;

import com.kgg.kkchat.common.common.domain.enums.YesOrNo;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author kgg
 * @since 2023-12-20
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User>{

    public User getByOpenId(String fromUser) {
        return lambdaQuery()
                .eq(User::getOpenId, fromUser)
                .one();
    }

    public User getByName(String name) {
        return lambdaQuery().eq(User::getName, name).one();
    }

    public void modifyName(Long uid, String name) {
        lambdaUpdate().eq(User::getId,uid)
                .set(User::getName,name).update();
    }

    public void wearingBadge(Long uid, Long itemId) {
        lambdaUpdate().eq(User::getId,uid)
                .set(User::getItemId,itemId)
                .update();
    }

    public void invalidUid(Long id) {
        lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, YesOrNo.YES.getStatus())
                .update();
    }

    public List<User> getFriendList(List<Long> uids) {
        return lambdaQuery()
                .in(User::getId, uids)
                .select(User::getId, User::getActiveStatus, User::getName, User::getAvatar)
                .list();


    }
}
