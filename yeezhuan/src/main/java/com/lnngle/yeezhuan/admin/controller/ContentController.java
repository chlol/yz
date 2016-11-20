package com.lnngle.yeezhuan.admin.controller;

import com.jfinal.aop.Before;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;

@RouterMapping(url = "/yz/a/c", viewPath = "/WEB-INF/yeezhuan/content")
@RouterNotAllowConvert
@Before(UserInterceptor.class)
public class ContentController extends JBaseController {
	
	public void edit() {
		render("edit.html");
		return;
	}
}
