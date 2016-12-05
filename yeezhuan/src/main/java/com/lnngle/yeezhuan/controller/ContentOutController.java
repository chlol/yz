package com.lnngle.yeezhuan.controller;

import java.math.BigInteger;

import com.lnngle.yeezhuan.Consts;

import io.jpress.core.cache.ActionCache;
import io.jpress.front.controller.ContentController;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_CONTENT)
public class ContentOutController extends ContentController {
	
	@ActionCache
	public void index() {
		this.setUserAd();
		super.index();
	}
	
	private void setUserAd() {
		String userId = this.getPara("uid");
		if (StringUtils.isNotEmpty(userId)) {
			UserQuery uq = new UserQuery();
			User user = uq.findById(new BigInteger(userId));
			String signature = user.getSignature();
			this.setAttr("signature", signature);
		}
	}
}
