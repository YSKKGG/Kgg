package com.kgg.kkchat.common.user.service.impl;

import com.kgg.kkchat.common.user.domain.enums.RoleEnum;
import com.kgg.kkchat.common.user.service.IRoleService;
import com.kgg.kkchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/9
 */
@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains((roleEnum.getId()));
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}
