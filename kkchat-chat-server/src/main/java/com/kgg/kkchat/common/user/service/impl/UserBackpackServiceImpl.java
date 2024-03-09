package com.kgg.kkchat.common.user.service.impl;

import com.kgg.kkchat.common.common.annotation.RedissonLock;
import com.kgg.kkchat.common.common.domain.enums.YesOrNo;
import com.kgg.kkchat.common.common.service.LockService;
import com.kgg.kkchat.common.user.dao.UserBackpackDao;
import com.kgg.kkchat.common.user.domain.entity.UserBackpack;
import com.kgg.kkchat.common.user.domain.enums.IdempotentEnum;
import com.kgg.kkchat.common.user.service.IUserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/8
 */
@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNo.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
