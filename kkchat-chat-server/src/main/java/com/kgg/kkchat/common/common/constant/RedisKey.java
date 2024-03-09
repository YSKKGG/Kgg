package com.kgg.kkchat.common.common.constant;

import java.util.Objects;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/24
 */
public class RedisKey {
    public static final String BASE_KEY="kkchat:chat";
    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING="userToken:uid_%d";
    public static String getKey(String key,Object... o){
        return BASE_KEY + String.format(key,o);
    }
}
