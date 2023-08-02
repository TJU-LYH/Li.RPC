package com.lx.client.core;

import java.util.HashSet;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import com.lx.client.zk.ZookeeperFactory;

import io.netty.channel.ChannelFuture;

public class ServerWatcher implements CuratorWatcher {

	
	//加入，挂掉了才去通知
	@Override
	public void process(WatchedEvent event) throws Exception{
		
		CuratorFramework client = ZookeeperFactory.create();
		
		String path = event.getPath();
		client.getChildren().usingWatcher(this).forPath(path);
		List<String> serverPaths = client.getChildren().forPath(path);
		
		
		ChannelManager.realServerPath.clear();//清空
		
		for(String serverPath : serverPaths) {
			String[] str = serverPath.split("#");
			int weight = Integer.valueOf(str[2]);
			if(weight > 0) {  //相当于有两个和SpringServer的连接
				for(int w=0; w<=weight; w++) {
					ChannelManager.realServerPath.add(str[0]+"#"+str[1]);	
				}
			}
			ChannelManager.realServerPath.add(str[0]+"#"+str[1]);	
		}
		
		ChannelManager.clear();
		
		
		
		
		//防止重复
		for(String realServer : ChannelManager.realServerPath) {
			String[] str = realServer.split("#");
			int weight = Integer.valueOf(str[2]);
			if(weight > 0) {  //相当于有两个和SpringServer的连接
				for(int w=0; w<=weight; w++) {
					ChannelFuture channelfuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));//去掉断连的之后然后重新连接    
					ChannelManager.add(channelfuture);
				}
			}
//			//去掉断连的之后然后重新连接
//			ChannelFuture channelfuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));//去掉断连的之后然后重新连接    
//			ChannelManager.add(channelfuture);
		}
		
	}

}
