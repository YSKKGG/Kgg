package com.kgg.kkchat.common.user.dao;

import com.kgg.kkchat.common.user.domain.entity.Role;
import com.kgg.kkchat.common.user.mapper.RoleMapper;
import com.kgg.kkchat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author kgg
 * @since 2024-03-09
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role>{

}
