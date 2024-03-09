package com.kgg.kkchat.common.user.service;

import com.kgg.kkchat.common.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kgg.kkchat.common.user.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author kgg
 * @since 2024-03-06
 */
public interface IUserBackpackService{
    /**
     * 给用户发放物品
     * @param uid           用户id
     * @param itemId        物品id
     * @param idempotentEnum 幂等类型
     * @param businessId    幂等唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
