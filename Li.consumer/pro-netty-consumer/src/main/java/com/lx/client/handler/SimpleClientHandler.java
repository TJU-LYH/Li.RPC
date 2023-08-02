package com.lx.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.lx.client.core.DefaultFuture;
import com.lx.client.param.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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
		DefaultFuture.recive(response);
		System.out.println(JSONObject.toJSONString(response));
		//ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	}

	
	
	
	
}
