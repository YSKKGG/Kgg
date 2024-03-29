package com.kgg.kkchat.common.user.service.cache;

import com.kgg.kkchat.common.common.constant.RedisKey;
import com.kgg.kkchat.common.common.utils.RedisUtils;
import com.kgg.kkchat.common.user.dao.BlackDao;
import com.kgg.kkchat.common.user.dao.ItemConfigDao;
import com.kgg.kkchat.common.user.dao.UserRoleDao;
import com.kgg.kkchat.common.user.domain.entity.Black;
import com.kgg.kkchat.common.user.domain.entity.ItemConfig;
import com.kgg.kkchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@Component
public class UserCache {
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private BlackDao blackDao;

    @Cacheable(cacheNames = "user", key = "'roles:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        collect.forEach((type, list) -> {
            result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    public List<Long> getUserModifyTime(List<Long> collect) {
        List<String> keys = collect.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }
}
