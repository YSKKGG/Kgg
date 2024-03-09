package com.kgg.kkchat.common.common.event.listener;

import com.kgg.kkchat.common.common.event.UserRegisterEvent;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.domain.enums.IdempotentEnum;
import com.kgg.kkchat.common.user.domain.enums.ItemEnum;
import com.kgg.kkchat.common.user.service.IUserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/8
 */
@Component
public class UserRegisterListener {

    @Autowired
    private IUserBackpackService userBackpackService;

    @Autowired
    private UserDao userDao;

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        //前100名的注册徽章
        User user = event.getUser();
        int registeredCount = userDao.count();
        if (registeredCount < 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (registeredCount < 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }

    }
}
