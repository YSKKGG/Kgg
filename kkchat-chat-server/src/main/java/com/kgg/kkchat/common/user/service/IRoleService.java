package com.kgg.kkchat.common.user.service;

import com.kgg.kkchat.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kgg.kkchat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author kgg
 * @since 2024-03-09
 */
public interface IRoleService{
    //权限判断
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
