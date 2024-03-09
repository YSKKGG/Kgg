package com.kgg.kkchat.common.user.dao;

import com.kgg.kkchat.common.user.domain.entity.ItemConfig;
import com.kgg.kkchat.common.user.mapper.ItemConfigMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author kgg
 * @since 2024-03-06
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig>{

    public List<ItemConfig> getByType(Integer itemType) {
        return lambdaQuery().eq(ItemConfig::getType, itemType)
                .list();
    }
}
