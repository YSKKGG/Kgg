package com.kgg.kkchat.common.user.dao;

import com.kgg.kkchat.common.user.domain.entity.UserRole;
import com.kgg.kkchat.common.user.mapper.UserRoleMapper;
import com.kgg.kkchat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author kgg
 * @since 2024-03-09
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {

    public List<UserRole> listByUid(Long uid) {
        return lambdaQuery()
                .eq(UserRole::getUid, uid)
                .list();
    }
}
