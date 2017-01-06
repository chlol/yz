package com.lnngle.yeezhuan.admin.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/yz/a/ccc")
public class CacheCleanController extends JBaseController {
	private static final Log logger = Log.getLog(CacheCleanController.class);
	public void flush() {
		String cacheName = this.getPara("cacheName");
		if (StringUtils.isNotEmpty(cacheName)) {
			logger.info("*********开始刷新缓存：" + cacheName);
			CacheKit.getCacheManager().getCache(cacheName).flush();
			renderAjaxResultForSuccess("缓存刷新完成");
		} else {
			renderAjaxResultForSuccess("缓存不存在");
		}
	}
	
	public void clean() {
		String cacheName = this.getPara("cacheName");
		if (StringUtils.isNotEmpty(cacheName)) {
			logger.info("*********开始清除缓存：" + cacheName);
			CacheKit.removeAll(cacheName);
			renderAjaxResultForSuccess("缓存清除完成");
		} else {
			renderAjaxResultForSuccess("缓存不存在");
		}
	}
}
