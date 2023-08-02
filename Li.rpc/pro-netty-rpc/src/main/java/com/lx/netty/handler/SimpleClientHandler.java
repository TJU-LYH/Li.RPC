package com.lx.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.lx.netty.client.DefaultFuture;
import com.lx.netty.util.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//
		if("ping".equals(msg.toString())) {
			ctx.channel().writeAndFlush("ping\r\n");
			return;
		}
		
		//System.out.println(msg.toString());
		//ctx.channel().attr(AttributeKey.valueOf("ssss")).set(msg);
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		System.out.println("接收服务器返回数据：" + JSONObject.toJSONString(response));
		DefaultFuture.recive(response);
		//ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	}

	
	
	
	
}
