package com.lnngle.yeezhuan.controller;

import static io.jpress.Consts.COOKIE_LOGINED_USER;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import com.lnngle.yeezhuan.Consts;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

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

	private static final Log log = Log.getLog(UserController.class);

	public void toLogin() {
		render("user_login.html");
		return;
	}
	
	private void setBackPara(String mobile,String password,String validateCode) {
		this.setAttr("mobile",mobile );
		this.setAttr("password", password);
		this.setAttr("validateCode", validateCode);
	}
	
	private void removeBackPara() {
		this.removeAttr("mobile");
		this.removeAttr("password");
		this.removeAttr("validateCode");
	}

	public void login() {
		keepPara();

		String mobile = getPara("mobile");
		String password = getPara("password");
		
		setBackPara(mobile,password,null);

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
		String validateCode = getPara("validateCode");
		
		setBackPara(mobile,password,validateCode);

		if (!StringUtils.isNotBlank(mobile)) {
			renderForRegister(ERR_MSG11);
			return;
		}

		if (!StringUtils.isNotBlank(password)) {
			renderForRegister(ERR_MSG21);
			return;
		}

		if (UserQuery.me().findUserByMobile(mobile) != null) {
			renderForRegister(ERR_MSG13);
			return;
		}
		String key = Code_Key_1 + mobile;
		Object myCode = this.getSession().getAttribute(key);
		if (myCode != null) {
			String tmpCode = (String) myCode;
			String[] split = tmpCode.split(";");
			if (!(validateCode.equals(split[0]) && ((System.currentTimeMillis() - Long.valueOf(split[1]).longValue())) < 10*60*1000)) {
				renderForRegister("无效的短信验证码！");
				return;
			}
		} else {
			renderForRegister("无效的短信验证码！");
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
			
			removeBackPara();
			renderForLogin("用户注册成功,请登录！");
		} else {
			renderForRegister("用户注册失败！");
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

	private void renderForRetrieve(String message) {
		setAttr(Consts.ERR_MSG_KEY, message);
		render("retrieve_password.html");
	}

	public void toRetrieve() {
		render("retrieve_password.html");
		return;
	}

	public void retrieve() {
		String mobile = getPara("mobile");
		String password = getPara("password");
		String validateCode = getPara("validateCode");
		
		setBackPara(mobile,password,validateCode);

		if (!StringUtils.isNotBlank(mobile)) {
			renderForRegister(ERR_MSG11);
			return;
		}

		if (!StringUtils.isNotBlank(password)) {
			renderForRegister(ERR_MSG21);
			return;
		}

		User user = UserQuery.me().findUserByMobile(mobile);
		if (user == null) {
			renderForRegister("要找回密码的手机号码不存在！");
			return;
		}
		
		String key = Code_Key_2 + mobile;
		Object myCode = this.getSession().getAttribute(key);
		if (myCode != null) {
			String tmpCode = (String) myCode;
			String[] split = tmpCode.split(";");
			if (!(validateCode.equals(split[0]) && ((System.currentTimeMillis() - Long.valueOf(split[1]).longValue())) < 10*60*1000)) {
				renderForRetrieve("无效的验证码！");
				return;
			}
		} else {
			renderForRetrieve("无效的验证码！");
			return;
		}

		String salt = EncryptUtils.salt();
		password = EncryptUtils.encryptPassword(password, salt);
		user.setPassword(password);
		user.setSalt(salt);
		user.setCreateSource("retrieve");
		user.setCreated(new Date());

		if (user.update()) {
			removeBackPara();
			renderForLogin("密码找回成功，请重新登录！");
		} else {
			renderForRetrieve("找回密码失败！");
		}

	}

	private static final String Code_Key_Pre = "CODE_KEY_";
	private static final String Code_Key_1 = "CODE_KEY_1";
	private static final String Code_Key_2 = "CODE_KEY_2";
	public void send() {
		String mobile = getPara("mobile");
		String code = createRandomCode();
		
		if (!StringUtils.isNotBlank(mobile)) {
			this.renderAjaxResultForSuccess("获取短信验证码前请先输入手机号码！");
			return;
		}
		
		if (sendSMS(mobile, code)) {
			String key = Code_Key_Pre + this.getPara("type") + mobile;
			this.getSession().setAttribute(key, code + ";" + System.currentTimeMillis());
			this.renderAjaxResultForSuccess("短信验证码发送成功,请输入验证码！");
		} else {
			this.renderAjaxResultForSuccess("短信验证码发送失败，请确认手机号输入是否正确！");
		}
	}

	private static boolean sendSMS(String phone, String code) {
		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23554031",
				"36c39219f51cbd44bb03fa96bcd00cab");
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType("normal");
		req.setSmsFreeSignName("易转");
		req.setRecNum(phone);
		req.setSmsTemplateCode("SMS_32495150");
		req.setSmsParamString("{\"code\":\"" + code + "\"}");
		try {
			AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
			return rsp.getResult() == null ? false : rsp.getResult().getSuccess();
		} catch (ApiException e) {
			log.error("发送短信验证码异常", e);
			return false;
		}
	}
	
	public static String createRandomCode(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
        	sb.append((int)(Math.random() * 9));
        }
        return sb.toString();
    }


	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			System.out.println(createRandomCode());
		}
	}
}
