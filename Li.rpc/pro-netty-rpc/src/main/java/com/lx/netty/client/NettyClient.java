package com.lx.netty.client;

import com.lx.netty.handler.SimpleClientHandler;

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
import io.netty.util.AttributeKey;

public class NettyClient {
	
	public static void main(String[] args) throws InterruptedException {
		
		
		String host = "localhost";
		int port = 8080;
		//创建事件循环组
		EventLoopGroup workergroup = new NioEventLoopGroup();
		
		try {
			//创建客户端启动对象
			Bootstrap b = new Bootstrap();
			b.group(workergroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
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
			//连接远程服务器地址，阻塞直到练=连接完成
			ChannelFuture f = b.connect(host, port).sync();
			
			f.channel().writeAndFlush("hello server");
			f.channel().writeAndFlush("\r\n");
			
			//阻塞直到channel关闭
			f.channel().closeFuture().sync();
			
			//主线程获取到数据
			Object result = f.channel().attr(AttributeKey.valueOf("ssss")).get();
			System.out.println("获取到服务器返回的数据==" + result);
			
			//长连接
			//1、要从handler中获得数据，2、保证连接相响应结果数据安全（如果多线程并发）
			
		} finally {
			workergroup.shutdownGracefully();
		} 
		
		
	}

}
