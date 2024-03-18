package com.kgg.kkchat.common.common.event.listener;

import com.kgg.kkchat.common.common.event.UserApplyEvent;
import com.kgg.kkchat.common.user.dao.UserApplyDao;
import com.kgg.kkchat.common.user.domain.entity.UserApply;
import com.kgg.kkchat.common.websocket.domain.vo.resp.WSFriendApply;
import com.kgg.kkchat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Description:好友申请监听器
 * Author: Kgg
 * Date: 2024/3/10
 */
@Slf4j
@Component
public class UserApplyListener {
    @Autowired
    private UserApplyDao userApplyDao;
    @Autowired
    private WebSocketService webSocketService;

//    @Autowired
//    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
//        UserApply userApply = event.getUserApply();
//        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
//        pushService.sendPushMsg(WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId());
    }

}
