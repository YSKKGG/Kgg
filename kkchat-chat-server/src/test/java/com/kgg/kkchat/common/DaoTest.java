package com.kgg.kkchat.common;

import com.kgg.kkchat.common.common.thread.MyUncaughtExceptionHandler;
import com.kgg.kkchat.common.common.utils.RedisUtils;
import com.kgg.kkchat.common.user.dao.UserDao;
import com.kgg.kkchat.common.user.domain.entity.User;
import com.kgg.kkchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 * Author: Kgg
 * Date:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private LoginService loginService;
    @Test
    public void redis() {
        String s="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExMDAzLCJjcmVhdGVUaW1lIjoxNzAzNDEwNjQxfQ.P6dw79tk7dQ8ku3RcjACZeQEFVmu2rtQNAobVurK-0E";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);
    }

    @Test
    public void jwt(){
        String login = loginService.login(20014L);
        System.out.println(login);
    }

    @Test
    public void a1() {
        redisTemplate.opsForValue().set("name","卷心菜");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name); //卷心菜
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 1000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }

    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Test
    public void thread() throws InterruptedException {
        threadPoolExecutor.execute(()->{
            if(1==1){
                log.error("123");
                throw new RuntimeException("1234");
            }
        });

        Thread.sleep(200);
    }
}