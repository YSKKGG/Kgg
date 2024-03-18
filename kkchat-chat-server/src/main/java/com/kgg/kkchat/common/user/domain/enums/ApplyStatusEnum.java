package com.kgg.kkchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:申请状态枚举
 * Author: Kgg
 * Date: 2024/3/10
 */

@Getter
@AllArgsConstructor
public enum ApplyStatusEnum {
    WAIT_APPROVAL(1, "待审批"),

    AGREE(2, "同意");

    private final Integer code;

    private final String desc;
}
