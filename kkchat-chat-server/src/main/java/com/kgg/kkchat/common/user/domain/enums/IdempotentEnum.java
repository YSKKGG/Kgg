package com.kgg.kkchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/8
 */
@AllArgsConstructor
@Getter
public enum IdempotentEnum {
    UID(1,"uid"),
    MSG_ID(2,"消息id");

    private final Integer type;
    private final String desc;
}
