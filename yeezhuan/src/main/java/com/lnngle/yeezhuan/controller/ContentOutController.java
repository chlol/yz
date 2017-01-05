package com.lnngle.yeezhuan.controller;

import java.math.BigInteger;
import java.util.List;

import com.lnngle.yeezhuan.Consts;

import io.jpress.core.cache.ActionCache;
import io.jpress.front.controller.ContentController;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.template.TemplateManager;
import io.jpress.template.TplMetadata;
import io.jpress.template.TplModule;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_CONTENT)
public class ContentOutController extends ContentController {
	
	@ActionCache
	public void index() {
		this.setUserAd();
		super.index();
		setMeta();
		
	}
	
	private void setUserAd() {
		String userId = this.getPara("uid");
		if (StringUtils.isNotEmpty(userId)) {
			User user = UserQuery.me().findById(new BigInteger(userId));
			String signature = user.getSignature();
			this.setAttr("signature", signature);
		} else {
			String value = OptionQuery.me().findValue("default_ad");
			if (StringUtils.isNotEmpty(value)) {
				this.setAttr("signature", value);
			}
			
		}
	}
	/**
	 * 获取tpl_config.xml里元数据的名称及其对应的值
	 */
	public void setMeta() {
		Content content = this.getAttr("content");
		TplModule module = TemplateManager.me().currentTemplateModule(content.getModule());
		List<TplMetadata> metadatas = module.getMetadatas();
		if (metadatas != null) {
			for (TplMetadata tplMetadata : metadatas) {
				setAttr(tplMetadata.getName(), content.metadata(tplMetadata.getName()));
			}
		}	
	}
}
