package com.lnngle.yeezhuan.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import io.jpress.interceptor.InterUtils;
import io.jpress.model.User;

public class UserInterceptor  implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		User user = InterUtils.tryToGetUser(inv);
		if (user != null) {
			inv.invoke();
		} else {
			inv.getController().redirect("/yz/user/login");
		}

	}

}
