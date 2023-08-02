package com.lx.client.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelFuture;

public class ChannelManager {
	
	
	static AtomicInteger position = new AtomicInteger(0);
	static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<String>();
	
	//管理ChannelFuture,即可以读也可以写
	public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();
	
	
	public static void removeChannel(ChannelFuture channel) {
		channelFutures.remove(channel);
	}
	
	public static void add(ChannelFuture channel) {
		channelFutures.add(channel);
	}
	
	public static void clear() {
		channelFutures.clear();
	}

	public static ChannelFuture get(AtomicInteger i) {
		//轮询访问，对所有的Channel
		int size = channelFutures.size();
		ChannelFuture channel = null;
		if(i.get()>size) {
			channel = channelFutures.get(0);
			ChannelManager.position=new AtomicInteger(1);
		}else {
			channel = channelFutures.get(i.getAndIncrement());
		}
		return channel;
	}
}
