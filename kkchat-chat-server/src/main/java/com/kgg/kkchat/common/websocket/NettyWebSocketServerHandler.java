package com.kgg.kkchat.common.websocket;

import cn.hutool.json.JSONUtil;
import com.kgg.kkchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.kgg.kkchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.kgg.kkchat.common.websocket.domain.vo.req.WSBaseReq;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            System.out.println("握手完成");
        }else if (evt instanceof IdleStateEvent){
            IdleStateEvent event= (IdleStateEvent)evt;
            if(event.state()==IdleState.READER_IDLE){
                System.out.println("读空闲");
                //todo 用户下线
                ctx.channel().close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        WSBaseReq bean = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(bean.getType())) {
            case AUTHORIZE:
                break;
            case LOGIN:
                System.out.println("请求二维码");
                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("登录成功"));
            case HEARTBEAT:
                break;
        }
//        System.out.println(text);
    }
}
