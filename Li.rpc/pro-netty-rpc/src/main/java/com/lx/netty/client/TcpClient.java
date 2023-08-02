package com.lx.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.lx.netty.handler.SimpleClientHandler;
import com.lx.netty.util.Response;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TcpClient {
	
	
	static final Bootstrap b = new Bootstrap();
	static ChannelFuture f = null;
	static {
		EventLoopGroup workergroup = new NioEventLoopGroup();
		b.group(workergroup)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
						new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new SimpleClientHandler());
				ch.pipeline().addLast(new StringEncoder());
			}

		});
		
		String host = "localhost";
		int port = 8080;
		
		try {
			f = b.connect(host, port).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	//注意：1、每个请求都是一个连接； 并发问题
	//解决办法：request ①唯一请求ID；②请求内容 ；   response①唯一响应ID ②响应结果
	//长连接发送数据,异步获得响应结果
	public static Response send(ClientRequest request) {
		
		f.channel().writeAndFlush(JSONObject.toJSONString(request));
		f.channel().writeAndFlush("\r\n");
		
		DefaultFuture df = new DefaultFuture(request);
		return df.get();
	}
	

}
