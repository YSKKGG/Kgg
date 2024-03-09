package com.kgg.kkchat.common.common.event.listener;

import com.kgg.kkchat.common.common.event.UserOnlineEvent;
import com.kgg.kkchat.common.common.event.UserRegisterEvent;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.domain.enums.IdempotentEnum;
import com.kgg.kkchat.common.user.domain.enums.ItemEnum;
import com.kgg.kkchat.common.user.domain.enums.UserActiveStatusEnum;
import com.kgg.kkchat.common.user.service.IUserBackpackService;
import com.kgg.kkchat.common.user.service.IpService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserOnlineListener {

    @Autowired
    private IpService ipService;
    @Autowired
    private UserDao userDao;

    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution=true)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getType());
        userDao.updateById(update);
        //用户ip详情的解析
        ipService.refreshIpDetailAsync(user.getId());
    }
}
