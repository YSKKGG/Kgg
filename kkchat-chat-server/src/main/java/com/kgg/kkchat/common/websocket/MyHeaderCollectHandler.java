package com.kgg.kkchat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import com.sun.xml.internal.ws.util.StreamUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/6
 */
public class MyHeaderCollectHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request=(HttpRequest) msg;
            UrlBuilder urlBuilder=UrlBuilder.ofHttp(request.getUri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            //如果token存在
            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            //移除拼接的参数
            request.setUri(urlBuilder.getPath().toString());
            //获取ip
            String s = request.headers().get("X-Real-IP");
            if(StringUtils.isBlank(s)){
                InetSocketAddress address=(InetSocketAddress)ctx.channel().remoteAddress();
                s=address.getAddress().getHostAddress();
            }
            //保存ip
            NettyUtil.setAttr(ctx.channel(),NettyUtil.IP,s);
            //用一次就移除
            ctx.pipeline().remove(this);
        }
            ctx.fireChannelRead(msg);
    }
}
