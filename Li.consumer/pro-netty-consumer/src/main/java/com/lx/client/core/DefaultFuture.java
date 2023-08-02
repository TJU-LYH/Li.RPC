package com.lx.client.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lx.client.param.ClientRequest;
import com.lx.client.param.Response;

public class DefaultFuture {
	
	public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<Long,DefaultFuture>();

	final Lock lock = new ReentrantLock();
	public Condition condtion = lock.newCondition();
	
	private Response response; 
	
	private long timeout = 2*60*1000; //2min请求还没有返回的话，默认超时
	private long startTime = System.currentTimeMillis();
	
	public Response getResponse() {
		return response;
	}


	public long getTimeout() {
		return timeout;
	}


	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}


	public long getStartTime() {
		return startTime;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public void setResponse(Response response) {
		this.response = response;
	}


	public DefaultFuture(ClientRequest request) {
		allDefaultFuture.put(request.getId(), this);
	}

	
	//主线程获取数据，首先要获取结果
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
	
	public Response get(long time) {
		lock.lock();
		
		try {
			while (!done()) {
				condtion.await(time, TimeUnit.SECONDS);
				if((System.currentTimeMillis() - startTime) > time) {  //请求超时了
					System.out.println("请求超时了！");
					break;
				}
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
	
	static class FutureThread extends Thread {

		@Override
		public void run() {
			Set<Long> ids = allDefaultFuture.keySet();//key的集合
			for(Long id : ids) {
				DefaultFuture df = allDefaultFuture.get(id);
				if(df == null) {
					allDefaultFuture.remove(id);
				}else {
					//加入这个链路超时了
					if(df.getTimeout() < (System.currentTimeMillis() - df.getStartTime())) {
						Response resp = new Response();
						resp.setId(id);
						resp.setCode("333333");
						resp.setMsg("链路请求超时");
						recive(resp);
					}
				}
			}
		
		}
		
	}
	
	static {
		FutureThread futureThread = new FutureThread();
		futureThread.setDaemon(true);//设置它为后台守护线程，一直在那扫描
	}

}
