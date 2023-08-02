package com.lx.netty.medium;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.lx.netty.annotation.Remote;

//netty服务器接收到这个请求之后，把数据交给这个中介。然后交给userController来处理

@Component
public class InitialMedium implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		//判断这个类上面是否有这个Controller注解
		if(bean.getClass().isAnnotationPresent(Remote.class)) {
			//拿到这个类里面的所有发方法
			Method[] methods = bean.getClass().getDeclaredMethods();
			for(Method m : methods) {
				String key = bean.getClass().getInterfaces()[0].getName() + "." + m.getName();
				Map<String,BeanMethod> beanMap = Media.beanMap;
				BeanMethod beanMethod = new BeanMethod();
				beanMethod.setBean(bean);
				beanMethod.setMethod(m);
				beanMap.put(key, beanMethod );
			}
		}
		return bean;
	}
	
	
}
