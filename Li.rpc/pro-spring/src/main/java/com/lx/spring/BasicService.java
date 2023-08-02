package com.lx.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lx.user.model.User;
import com.lx.user.remote.UserRemote;


@Configuration
@Service
public class BasicService {
	
	
	//@RemoteInvoke
	private UserRemote userRemote;
	
	public BasicService() {
		
	}
	
	public void test1() {
		System.out.println("3");
	}
	
	public void testSaveUser() {
		System.out.println("2");
		User u = new User();
		u.setId(1);
		u.setName("李四");
		
		Object response = userRemote.saveUser(u);
		System.out.println(JSONObject.toJSONString(response));
	}
}
