package com.kgg.kkchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@AllArgsConstructor
@Getter
public enum YesOrNo {
    NO(0,"否"),
    YES(1,"是"),
    ;
    private final Integer status;
    private final String desc;
}
