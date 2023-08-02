package com.lx.netty.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lx.netty.util.Response;

public class DefaultFuture {
	
	public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<Long,DefaultFuture>();

	final Lock lock = new ReentrantLock();
	public Condition condtion = lock.newCondition();
	
	private Response response; 
	
	public Response getResponse() {
		return response;
	}


	public void setResponse(Response response) {
		this.response = response;
	}


	public DefaultFuture(ClientRequest request) {
		allDefaultFuture.put(request.getId(), this);
	}

	
	//主线程获取结果，首先要获取结果
	public Response get() {
		lock.lock();
		
		try {
			while (!done()) {
				condtion.await();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return this.response;
	}
	
	public static void recive(Response response) {
		DefaultFuture df = allDefaultFuture.get(response.getId());
		if(df != null) {
			Lock lock = df.lock;
			lock.lock();
			try {	
				df.setResponse(response);
				df.condtion.signal();
				allDefaultFuture.remove(response.getId());//接收到这个请求之后，把这个请求删除掉
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				lock.unlock();
			}
		}
	}


	private boolean done() {
		
		if (this.response != null) {
			return true;
		}
		
		return false;
	}

}
