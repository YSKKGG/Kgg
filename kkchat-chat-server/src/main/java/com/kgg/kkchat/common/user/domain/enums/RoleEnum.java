package com.kgg.kkchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 权限枚举
 * Author: Kgg
 * Date: 2024-03-06
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1l, "超级管理员"),
    CHAT_MANAGER(2l, "群聊管理员"),
    ;

    private final Long id;
    private final String desc;

    private static Map<Long, RoleEnum> cache;

    static {
        cache = Arrays.stream(RoleEnum.values()).collect(Collectors.toMap(RoleEnum::getId, Function.identity()));
    }

    public static RoleEnum of(Integer id) {
        return cache.get(id);
    }
}
