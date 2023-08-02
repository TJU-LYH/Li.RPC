package com.lx.user.remote;

import java.util.List;

import javax.annotation.Resource;

import com.lx.netty.annotation.Remote;
import com.lx.netty.util.ResponseUtil;
import com.lx.user.model.User;
import com.lx.user.service.UserService;

@Remote
public class UserremoteImpl implements UserRemote{
	@Resource
	private UserService userService;
	
	public Object saveUser(User user) {
		
		userService.save(user);
	
		return ResponseUtil.createSuccessResult(user);
	}
	
	public Object saveUsers(List<User> users) {
		
		userService.saveList(users);
	
		return ResponseUtil.createSuccessResult(users);
	}
}
