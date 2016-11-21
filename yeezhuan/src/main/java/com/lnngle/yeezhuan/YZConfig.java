package com.lnngle.yeezhuan;

import com.jfinal.config.Handlers;
import com.lnngle.yeezhuan.handler.UrlSkipHandler;

import io.jpress.Config;

public class YZConfig extends Config {

	public void configHandler(Handlers handlers) {
		handlers.add(new UrlSkipHandler());
		super.configHandler(handlers);
	}
}
