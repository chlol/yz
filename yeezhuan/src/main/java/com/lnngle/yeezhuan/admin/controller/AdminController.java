package com.lnngle.yeezhuan.admin.controller;

import com.jfinal.aop.Before;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;

@RouterMapping(url = "/yz/admin", viewPath = "/WEB-INF/yeezhuan")
@RouterNotAllowConvert
public class AdminController extends JBaseController {
	@Before(UserInterceptor.class)
	public void index() {
		render("index.html");
		return;
	}
}
