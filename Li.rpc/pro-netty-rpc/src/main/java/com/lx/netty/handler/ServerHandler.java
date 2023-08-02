package com.lx.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.lx.netty.handler.param.ServerRequest;
import com.lx.netty.medium.Media;
import com.lx.netty.util.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);
		
		
		Media media = Media.newInstance();
		Response result = media.process(request);  //去中介者Media中处理业务
		
		
//		Response response = new Response();
//		response.setId(request.getId());//设置成和客户端请求ID同样的
//		response.setResult("is oK");
		ctx.channel().writeAndFlush(JSONObject.toJSONString(result));
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
