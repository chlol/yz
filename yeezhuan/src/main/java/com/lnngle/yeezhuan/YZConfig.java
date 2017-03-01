package com.lnngle.yeezhuan;

import com.jfinal.config.Handlers;
import com.lnngle.yeezhuan.handler.UrlSkipHandler;
import com.lnngle.yeezhuan.tag.YzContentsTag;

import io.jpress.Config;
import io.jpress.core.Jpress;

public class YZConfig extends Config {

	public void configHandler(Handlers handlers) {
		handlers.add(new UrlSkipHandler());
		super.configHandler(handlers);
	}
	
	@Override
	public void onJfinalStartAfter() {
		Jpress.addTag(YzContentsTag.TAG_NAME, new YzContentsTag());
		super.onJfinalStartAfter();
	}
}
