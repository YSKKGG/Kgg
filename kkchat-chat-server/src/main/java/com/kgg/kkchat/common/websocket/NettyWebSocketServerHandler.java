package com.kgg.kkchat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.kgg.kkchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.kgg.kkchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.kgg.kkchat.common.websocket.domain.vo.req.WSBaseReq;
import com.kgg.kkchat.common.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
         webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if(StrUtil.isNotBlank(token)){
                webSocketService.authorize(ctx.channel(),token);
            }
            System.out.println("握手完成");
        }else if (evt instanceof IdleStateEvent){
            IdleStateEvent event= (IdleStateEvent)evt;
            if(event.state()==IdleState.READER_IDLE){
                System.out.println("读空闲");
                //todo 用户下线
                userOffLine(ctx.channel());
                ctx.channel().close();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx.channel());
    }

    /**
     *用户下线统一处理
     */
    private void userOffLine(Channel channel){
        webSocketService.offline(channel);
        channel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught",cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        WSBaseReq bean = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(bean.getType())) {
            case AUTHORIZE:
                webSocketService.authorize(channelHandlerContext.channel(),bean.getData());
                break;
            case LOGIN:
//                System.out.println("请求二维码");
                webSocketService.handleLoginReq(channelHandlerContext.channel());
//                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("登录成功"));
            case HEARTBEAT:
                break;
        }
//        System.out.println(text);
    }
}
