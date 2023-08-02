//package com.lx.pro_netty_rpc;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.lx.netty.client.ClientRequest;
//import com.lx.netty.client.TcpClient;
//import com.lx.netty.util.Response;
//import com.lx.user.bean.User;
//
//public class TestTcp {
//	@Test
//	public void testGetResponse() {
//		ClientRequest request = new ClientRequest();
//		request.setContent("测试TCP长连接");
//		Response response = TcpClient.send(request);
//		System.out.println(response.getResult());
//	}
//	
//	@Test
//	public void testSaveUser() {
//		ClientRequest request = new ClientRequest();
//		
//		User u = new User();
//		u.setId(1);
//		u.setName("李四");
//		  
//		request.setCommand("com.lx.user.controller.UserController.saveUser");
//		request.setContent(u);
//		Response response = TcpClient.send(request);
//		System.out.println(response.getResult());
//	}
//	
//	@Test
//	public void testSaveUsers() {
//		ClientRequest request = new ClientRequest();
//		
//		List<User> users = new ArrayList<>();
//		User u = new User();
//		u.setId(1);
//		u.setName("李四");
//		users.add(u);
//		  
//		request.setCommand("com.lx.user.controller.UserController.saveUsers");
//		request.setContent(users);
//		Response response = TcpClient.send(request);
//		System.out.println(response.getResult());
//	}
//	
//}
