package com.kgg.kkchat.common.websocket.service;

import com.kgg.kkchat.common.websocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;

/**
 * Description:
 * Author: Kgg
 * Date: 2023/12/21
 */
public interface WebSocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void offline(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String token);

    void sendMsgToAll(WSBaseResp<?> msg);
}
