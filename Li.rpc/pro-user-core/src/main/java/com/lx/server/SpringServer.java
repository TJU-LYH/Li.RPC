package com.lx.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.lx.user.service.UserService;


@Configuration
@ComponentScan("com.lx") //扫描这个包下的所有注解
public class SpringServer {
	
	public static void main(String[] args) throws InterruptedException{
		
		ApplicationContext Context = new AnnotationConfigApplicationContext(SpringServer.class);
		
	}
}
