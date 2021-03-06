package com.lnngle.yeezhuan.admin.controller;

import com.jfinal.aop.Before;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;

@RouterMapping(url = "/yz/a", viewPath = "/WEB-INF/yeezhuan")
@RouterNotAllowConvert
@Before(UserInterceptor.class)
public class AdminController extends JBaseController {
	public void index() {
		render("index.html");
		return;
	}
}
