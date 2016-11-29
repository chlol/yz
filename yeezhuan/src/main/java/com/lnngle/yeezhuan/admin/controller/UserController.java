package com.lnngle.yeezhuan.admin.controller;

import java.sql.SQLException;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.lnngle.yeezhuan.Consts;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.JBaseController;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/yz/a/u", viewPath = "/WEB-INF/yeezhuan/user")
@RouterNotAllowConvert
@Before(UserInterceptor.class)
public class UserController extends JBaseController {
	public void get() {
		User user = getLoginedUser();
		if (user != null) {
			user = UserQuery.me().findById(user.getId());
		}
		setAttr("user", user);
		render("edit.html");
		return;
	}

	public void edit() {
		final User user = getModel(User.class);
		String password = user.getPassword();

		if (StringUtils.isNotBlank(user.getEmail())) {
			User dbUser = UserQuery.me().findUserByEmail(user.getEmail());
			if (dbUser != null && user.getId().compareTo(dbUser.getId()) != 0) {
				renderForMessage("邮件地址已经存在，不能修改为该邮箱。");
				return;
			}
		}

		// 用户修改了密码
		if (StringUtils.isNotEmpty(password)) {
			User dbUser = UserQuery.me().findById(user.getId());
			user.setSalt(dbUser.getSalt());
			password = EncryptUtils.encryptPassword(password, dbUser.getSalt());
			user.setPassword(password);
		} else {
			// 清除password，防止密码被置空
			user.remove("password");
		}
		
		boolean saved = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				if (!user.saveOrUpdate()) {
					return false;
				}

				return true;
			}
		});
		setAttr("user", user);
		if (saved) {
			renderForMessage("账号信息保存成功");
		} else {
			renderForMessage("账号信息保存失败");
		}
	}
	
	private void renderForMessage(String message) {
		setAttr(Consts.ERR_MSG_KEY, message);
		render("edit.html");
	}

}
