package com.kgg.kkchat.common.user.dao;

import com.kgg.kkchat.common.user.domain.entity.Black;
import com.kgg.kkchat.common.user.mapper.BlackMapper;
import com.kgg.kkchat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author kgg
 * @since 2024-03-09
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> {

}
