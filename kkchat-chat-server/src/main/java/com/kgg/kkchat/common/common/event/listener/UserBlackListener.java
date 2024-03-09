package com.kgg.kkchat.common.common.event.listener;

import com.kgg.kkchat.common.common.event.UserBlackEvent;
import com.kgg.kkchat.common.common.event.UserOnlineEvent;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.domain.enums.UserActiveStatusEnum;
import com.kgg.kkchat.common.user.service.IUserBackpackService;
import com.kgg.kkchat.common.user.service.IpService;
import com.kgg.kkchat.common.user.service.cache.UserCache;
import com.kgg.kkchat.common.websocket.service.WebSocketService;
import com.kgg.kkchat.common.websocket.service.adapter.WebSocketAdapter;
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
public class UserBlackListener {
    @Autowired
    private IUserBackpackService userBackpackService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserCache userCache;


    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent event) {
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStatus(UserBlackEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(UserBlackEvent event) {
        userCache.evictBlackMap();
    }
}
