package com.lx.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.lx.netty.handler.param.ServerRequest;
import com.lx.netty.util.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		//System.out.println(msg.toString());
		//ctx.channel().writeAndFlush(msg.toString()+"is ok\r\n");
		//ctx.channel().close();
		//msg里包括客户端request的id和content，JSONObject.parseObject将指定的JSON字符串转换成自己的实体类的对象
		ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);
		
		Response response = new Response();
		response.setId(request.getId());//设置成和客户端请求ID同样的
		response.setResult("is ok");
		System.out.println(msg.toString());
		ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
		ctx.channel().writeAndFlush("\r\n");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent)evt;
			
			//心跳机制
			if(event.state().equals(IdleState.READER_IDLE)) {
				System.out.println("读空闲");
				ctx.channel().close();
			} else if(event.state().equals(IdleState.WRITER_IDLE)) {
				System.out.println("写空闲");
			} else if(event.state().equals(IdleState.ALL_IDLE)) {
				System.out.println("读写空闲");
				ctx.channel().writeAndFlush("ping\r\n");
			}
		}
	}
	
	
	
	
	
	
}
