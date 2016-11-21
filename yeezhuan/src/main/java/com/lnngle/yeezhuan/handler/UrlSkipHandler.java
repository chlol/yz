package com.lnngle.yeezhuan.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class UrlSkipHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (target.endsWith(".html") && target.startsWith("/static")) {
			return ;
		} else {
			next.handle(target, request, response, isHandled);
		}
		
	}

}
