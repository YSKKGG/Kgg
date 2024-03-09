package com.kgg.kkchat.common.user.service.cache;

import com.kgg.kkchat.common.user.dao.ItemConfigDao;
import com.kgg.kkchat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@Component
public class ItemCache {
    @Autowired
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item",key ="'itemsByType:'+#itemType")
    public List<ItemConfig> getByType(Integer itemType){
        return itemConfigDao.getByType(itemType);
    }

    @CacheEvict(cacheNames = "item",key ="'itemsByType:'+#itemType")
    public void evictByType(Integer itemType){

    }

}
