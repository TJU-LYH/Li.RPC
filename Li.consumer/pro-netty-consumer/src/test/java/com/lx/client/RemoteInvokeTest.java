package com.lx.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.lx.client.annotation.RemoteInvoke;
import com.lx.client.param.Response;
import com.lx.user.bean.User;
import com.lx.user.remote.UserRemote;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokeTest.class)
@ComponentScan("com.lx")
public class RemoteInvokeTest {
	
	@RemoteInvoke
	private UserRemote userRemote;
	
	@Test
	public void testSaveUser() {
		User u = new User();
		u.setId(1);
		u.setName("李四");
		
		Response response = userRemote.saveUser(u);
		System.out.println(JSONObject.toJSONString(response));
	}
	
	@Test
	public void testSaveUsers() {
		
		List<User> users = new ArrayList<>();
		User u = new User();
		u.setId(1);
		u.setName("李四");
		users.add(u);
		userRemote.saveUsers(users);
	}
}
