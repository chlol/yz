package com.lnngle.yeezhuan.controller;

import static io.jpress.Consts.COOKIE_LOGINED_USER;

import java.util.Date;

import com.jfinal.aop.Before;
import com.lnngle.yeezhuan.Consts;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.BaseFrontController;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_YZ_USER)
public class UserController extends BaseFrontController {
	public static final String ERR_MSG11 = "手机号码不能为空!";
	public static final String ERR_MSG12 = "手机号不存在，请先注册!";
	public static final String ERR_MSG13 = "手机号码已经存在!";
	public static final String ERR_MSG21 = "密码不能为空!";
	public static final String ERR_MSG22 = "密码错误!";

	public void toLogin() {
		render("user_login.html");
		return;
	}

	public void login() {
		keepPara();

		String mobile = getPara("mobile");
		String password = getPara("password");

		if (!StringUtils.isNotBlank(mobile)) {
			renderForLogin(ERR_MSG11);
			return;
		}

		if (!StringUtils.isNotBlank(password)) {
			renderForLogin(ERR_MSG21);
			return;
		}

		User user = UserQuery.me().findUserByUsername(mobile);
		if (null == user) {
			renderForLogin(ERR_MSG12);
			return;
		}

		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, COOKIE_LOGINED_USER, user.getId());
			String gotoUrl = getPara("goto");
			if (StringUtils.isNotEmpty(gotoUrl)) {
				gotoUrl = StringUtils.urlDecode(gotoUrl);
				gotoUrl = StringUtils.urlRedirect(gotoUrl);
				redirect(gotoUrl);
			}
		} else {
			renderForLogin(ERR_MSG22);
		}
	}

	@Before(UserInterceptor.class)
	public void logout() {
		CookieUtils.remove(this, COOKIE_LOGINED_USER);
		redirect("/");
	}

	public void toRegist() {
		render("user_register.html");
		return;
	}

	public void regist() {
		keepPara();

		String mobile = getPara("mobile");
		String password = getPara("password");

		if (!StringUtils.isNotBlank(mobile)) {
			renderForRegister(ERR_MSG11);
			return;
		}

		if (!StringUtils.isNotBlank(password)) {
			renderForRegister(ERR_MSG21);
			return;
		}

		if (null != mobile && UserQuery.me().findUserByMobile(mobile) != null) {
			renderForRegister(ERR_MSG13);
			return;
		}

		User user = new User();
		user.setMobile(mobile);
		user.setUsername(mobile);

		String salt = EncryptUtils.salt();
		password = EncryptUtils.encryptPassword(password, salt);
		user.setPassword(password);
		user.setSalt(salt);
		user.setCreateSource("register");
		user.setCreated(new Date());

		if (user.save()) {
			CookieUtils.put(this, COOKIE_LOGINED_USER, user.getId());
			MessageKit.sendMessage(Actions.USER_CREATED, user);

			if (isAjaxRequest()) {
				renderAjaxResultForSuccess();
			} else {
				String gotoUrl = getPara("goto");
				gotoUrl = StringUtils.urlDecode(gotoUrl);
				gotoUrl = StringUtils.urlRedirect(gotoUrl);
				redirect(gotoUrl);
			}
		} else {
			renderAjaxResultForError();
		}

	}

	private void renderForRegister(String message) {
		setAttr(Consts.ERR_MSG_KEY, message);
		render("user_register.html");
	}

	private void renderForLogin(String message) {
		setAttr(Consts.ERR_MSG_KEY, message);
		render("user_login.html");
	}

	@Before(UserInterceptor.class)
	public void manage() {
		render("admin_index.html");
		return;
	}
}
