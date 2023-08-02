package com.lx.client.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.lx.client.annotation.RemoteInvoke;
import com.lx.client.core.TcpClient;
import com.lx.client.param.ClientRequest;
import com.lx.client.param.Response;
import com.lx.user.bean.User;


//客户端动态代理处理业务
@Component
public class InvokeProxy implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		
		Field[] fields = bean.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			if(field.isAnnotationPresent(RemoteInvoke.class)) { //判断这个属性上面是否有这个注解
				field.setAccessible(true);//设为可修改的
			
				final Map<Method, Class> methodClassMap = new HashMap<Method, Class>();
				putMethodClassMap(methodClassMap, field);
				
				Enhancer enhancer = new Enhancer();//创建了一个动态代理
				enhancer.setInterfaces(new Class[] {field.getType()});//gettype获取到这个属性的类型
				enhancer.setCallback(new MethodInterceptor() {
					//拦截 testSaveUser方法
					@Override
					public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
						//需要采用netty客户端去调用服务器
						ClientRequest request = new ClientRequest();
						
						User u = new User();
						//User i = (User) args[0];
						request.setCommand(methodClassMap.get(method).getName() + "." + method.getName());
						request.setContent(args[0]);
						
						Response response = TcpClient.send(request);
						return response;
					}
				});
				
				
				
				try {
					field.set(bean, enhancer.create());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}

	
	/**
	 * 对属性的所有方法和属性接口类型放入到一个map中
	 * @param methodClassMap
	 * @param field
	 */
	private void putMethodClassMap(Map<Method, Class> methodClassMap, Field field) {

		Method[] methods = field.getType().getDeclaredMethods();
		for(Method m : methods) {
			methodClassMap.put(m, field.getType());
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
