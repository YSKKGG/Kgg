package com.kgg.kkchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/9
 */
@AllArgsConstructor
@Getter
public enum UserActiveStatusEnum {
    ONLINE(1,"在线"),
    OFFLINE(2,"离线");

    private final Integer type;
    private final String desc;
}
