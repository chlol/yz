package com.lnngle.yeezhuan.controller;

import io.jpress.core.BaseFrontController;
import io.jpress.router.RouterMapping;

import com.lnngle.yeezhuan.Consts;

@RouterMapping(url = Consts.ROUTER_YZ_USER)
public class UserRegistController extends BaseFrontController {
	public void toRegist() {
		render("user_register.html");
		return;
	}
	
	public void regist() {

	}
}
