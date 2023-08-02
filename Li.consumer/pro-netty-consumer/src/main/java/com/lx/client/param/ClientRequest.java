package com.lx.client.param;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {
	
	private final long id;
	private Object content;
	private final AtomicLong aid = new AtomicLong(1);//原子类
	
	private String command;
	
	
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ClientRequest() {
		id = aid.incrementAndGet();
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}
	
	
}
