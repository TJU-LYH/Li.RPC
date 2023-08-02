package com.lx.spring;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@Configuration
@ComponentScan(basePackages = "com.lx") //扫描这个包下的所有注解
public class BasicController {

	public static void main(String[] args) throws InterruptedException{
		ApplicationContext context = 
				new AnnotationConfigApplicationContext(BasicController.class);
		
		BasicService basicService = context.getBean(BasicService.class);  //拿不到这个对象？？？
		for(String str : context.getBeanDefinitionNames()){
            System.out.println("str = " + str);
        }
		if (basicService==null) {
			System.out.println("2");
		}
		System.out.println("1");
		
		
		basicService.test1();
		basicService.testSaveUser();
//		
	}
	
}
