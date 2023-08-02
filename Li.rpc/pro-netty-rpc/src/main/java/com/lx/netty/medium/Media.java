package com.lx.netty.medium;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.lx.netty.handler.param.ServerRequest;
import com.lx.netty.util.Response;

public class Media {
	
	public static Map<String, BeanMethod> beanMap;
	static {
		beanMap = new HashMap<String, BeanMethod>();
		
	}
	
	
	private static Media m = null;
	private Media() {
		
	}
	public static Media newInstance() {  //单例
		
		if(m == null) {
			m = new Media();
		}
		
		return m;
	}
	
	
	//反射处理业务代码
	public Response process(ServerRequest request) {

		Response result = null;
		try {
			String command = request.getCommand();//拿到方法问的方法
			BeanMethod beanthod = beanMap.get(command);
			
			if(beanthod == null) {
				return null;
			}
			
			Object bean = beanthod.getBean();
			Method m = beanthod.getMethod();
			Class paramType = m.getParameterTypes()[0];//获得参数类型
			Object content = request.getContent();
			
			//拿到方法参数
			Object args = JSONObject.parseObject(JSONObject.toJSONString(content), paramType);
			
			result =  (Response) m.invoke(bean, args);//反射调用
			result.setId(request.getId());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return result;
	}

	
}
