package com.lnngle.yeezhuan.controller;

import com.baidu.ueditor.ActionEnter;
import com.jfinal.aop.Before;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;

@RouterMapping(url = "/static/ueditor")
@Before(UserInterceptor.class)
public class UEditorController extends JBaseController {

	public void config() {
		String rootPath = this.getRequest().getServletContext().getRealPath( "/" );
		String message = new ActionEnter( this.getRequest(), rootPath ).exec();
		renderAjaxResultForSuccess(message);
		
		return ;
	}
}
