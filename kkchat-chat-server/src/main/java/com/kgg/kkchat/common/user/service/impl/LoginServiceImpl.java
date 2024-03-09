package com.kgg.kkchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kgg.kkchat.common.common.constant.RedisKey;
import com.kgg.kkchat.common.common.utils.JwtUtils;
import com.kgg.kkchat.common.common.utils.RedisUtils;
import com.kgg.kkchat.common.user.service.LoginService;
import jodd.util.StringUtil;
import org.dom4j.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/24
 */
@Service
public class LoginServiceImpl implements LoginService {
    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEWAL_DAYS = 1;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if(expireDays==-2){
            return;
        }
        if (expireDays< TOKEN_RENEWAL_DAYS){
            RedisUtils.expire(getUserTokenKey(uid), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uidOrNull = jwtUtils.getUidOrNull(token);
        if(Objects.isNull(uidOrNull)){
            return null;
        }
        String oldToken = RedisUtils.getStr(getUserTokenKey(uidOrNull));
        return Objects.equals(token,oldToken)?uidOrNull:null;
    }

    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}
