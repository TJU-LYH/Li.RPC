package com.lx.netty.init;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.lx.netty.constant.Constants;
import com.lx.netty.factory.ZookeeperFactory;
import com.lx.netty.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;


@Component  //整个容器启动之后，初始化下面的方法
public class Nettyinital implements ApplicationListener<ContextRefreshedEvent>{//ContextRefreshedEvent为初始化完毕事件,监听到一个Application初始化完成，就会执行onApplicationEvent
	
	public void start() {
		
		//创建两个线程组
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		try {
			//创建服务端的启动对象
			ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.group(parentGroup, childGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, false)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 初始化channel时，添加处理器
							ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
							//ch.pipeline().addLast("HttpServerCodec",new HttpServerCodec());
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new IdleStateHandler(60, 45, 10, TimeUnit.SECONDS));//心跳机制
							ch.pipeline().addLast(new ServerHandler());
							ch.pipeline().addLast(new StringEncoder());
						}
					});

			int port = 8080;
			int weight = 2;
			ChannelFuture f = bootstrap.bind(8080).sync();
			
			
			//获取服务器的IP地址
			InetAddress netAddress = InetAddress.getLocalHost();
			//将nett服务器注册到Zookeeper中，
			CuratorFramework client = ZookeeperFactory.create();
			client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constants.SERVER_PATH+"/"+netAddress.getHostAddress()+"#"+port+"#"+weight+"#");//采用临时节点
			
			f.channel().closeFuture().sync(); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//关闭释放资源
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		//ContextRefreshedEvent为初始化完毕事件,监听到一个Application初始化完成，就会执行onApplicationEvent
		for(String str : event.getApplicationContext().getBeanDefinitionNames()){
            System.out.println("str = " + str);
        }
		Nettyinital u = event.getApplicationContext().getBean(Nettyinital.class);
		System.out.println(u.toString());
		this.start();  
	}
	
	

}
