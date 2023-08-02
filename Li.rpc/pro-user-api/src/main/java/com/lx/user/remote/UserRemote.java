package com.lx.user.remote;

import java.util.List;

import com.lx.user.model.User;




//相当于是一个接口，客户端直接调用接口里的方法就好，就可以访问到服务器端对应的方法
public interface UserRemote {
	
	public Object saveUser(User user);
	public Object saveUsers(List<User> users);
}

