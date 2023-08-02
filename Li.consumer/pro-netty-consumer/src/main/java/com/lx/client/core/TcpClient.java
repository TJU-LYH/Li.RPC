package com.lx.client.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import com.alibaba.fastjson.JSONObject;
import com.lx.client.constant.Constants;
import com.lx.client.handler.SimpleClientHandler;
import com.lx.client.param.ClientRequest;
import com.lx.client.param.Response;
import com.lx.client.zk.ZookeeperFactory;

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
	
	//static Set<String> realServerPath = new HashSet<String>();//去重，有重复节点
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
		
		
		CuratorFramework client = ZookeeperFactory.create();
		String host = "localhost";
		int port = 8080;
		try {
			List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);
			
			
			CuratorWatcher watcher = new ServerWatcher();//监听路径下服务器的变化
			//加上zk监听服务器变化
			client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);
			
			
			//Set<String> realServerPath = new HashSet<String>();//去重，有重复节点
			for(String serverPath : serverPaths) {
				String[] str = serverPath.split("#");
				int weight = Integer.valueOf(str[2]);
				if(weight > 0) {  //相当于有两个和SpringServer的连接
					for(int w=0; w<=weight; w++) {
						ChannelManager.realServerPath.add(str[0]+"#"+str[1]);	
						ChannelFuture channelfuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
						ChannelManager.add(channelfuture);
					}
				}
				
				
				
//				ChannelManager.realServerPath.add(str[0]+"#"+str[1]);	//不仅保存了host，还保存了port
//				//一开始，不需要清空，直接追加进来
//				ChannelFuture channelfuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
//				ChannelManager.add(channelfuture);
			}
			
			
			
			//服务器启动的时候进行一个管理
			if(ChannelManager.realServerPath.size()>0) {
				String[] hostAndPort = ChannelManager.realServerPath.toArray()[0].toString().split("#");
				host = hostAndPort[0];
				port = Integer.valueOf(hostAndPort[1]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		try {
//			f = b.connect(host, port).sync();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	
	
	//注意：1、每个请求都是一个连接； 并发问题
	//解决办法：request ①唯一请求ID；②请求内容 ；   response①唯一响应ID ②响应结果
	//长连接发送数据,异步获得响应结果
	public static Response send(ClientRequest request) {
		f = ChannelManager.get(ChannelManager.position);
		f.channel().writeAndFlush(JSONObject.toJSONString(request));
		f.channel().writeAndFlush("\r\n");
		
		DefaultFuture df = new DefaultFuture(request);
		return df.get();
	}
	

}
