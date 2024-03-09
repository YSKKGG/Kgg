package com.kgg.kkchat.common.common.event;

import com.kgg.kkchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/9
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private User user;

    public UserOnlineEvent(Object source,User user) {
        super(source);
        this.user=user;
    }
}
