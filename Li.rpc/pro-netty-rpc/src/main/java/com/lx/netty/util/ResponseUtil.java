package com.lx.netty.util;

public class ResponseUtil {
	
	public static Response createSuccessResult() {
		return new Response();
	}
	
	public static Response createFailresult(String code, String msg) {
		
		Response response = new Response();
		response.setCode(code);
		response.setMsg(msg);
		
		return response;
	}
	
	public static Response createSuccessResult(Object content) {
		Response response = new Response();
		response.setResult(content);
		return response;
	}
}
