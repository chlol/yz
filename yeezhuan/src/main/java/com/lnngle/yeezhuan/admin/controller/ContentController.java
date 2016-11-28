package com.lnngle.yeezhuan.admin.controller;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;

import io.jpress.core.JBaseController;
import io.jpress.core.render.AjaxResult;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.User;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.MappingQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.TemplateManager;
import io.jpress.template.TplMetadata;
import io.jpress.template.TplModule;
import io.jpress.template.TplTaxonomyType;
import io.jpress.utils.JsoupUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/yz/a/c", viewPath = "/WEB-INF/yeezhuan/content")
@RouterNotAllowConvert
@Before(UserInterceptor.class)
public class ContentController extends JBaseController {

	public void edit() {
		String moduleName = getModuleName();
		BigInteger contentId = getParaToBigInteger("id");
		
		TplModule module = TemplateManager.me().currentTemplateModule(moduleName);
		setAttr("module", module);
		
		if (contentId != null) {
			Content content = ContentQuery.me().findById(contentId);
			if (content != null) {
				setAttr("content", content);
				moduleName = content.getModule();
				List<TplMetadata> metadatas = module.getMetadatas();
				if (metadatas != null) {
					for (TplMetadata tplMetadata : metadatas) {
						setAttr(tplMetadata.getName(), content.metadata(tplMetadata.getName()));
					}
				}	
			}

		}
		
		setAttr("m", moduleName);
	}

	public void save() {
		final Map<String, String> metas = new HashMap<>();
		String originalUrl = this.getPara("originalUrl");
		if (!StringUtils.isBlank(originalUrl)) {
			metas.put("originalUrl", originalUrl);
		}
		
		final Content content = getContent();

		if (StringUtils.isBlank(content.getTitle())) {
			renderAjaxResultForError("文章标题不能为空！");
			return;
		}

		boolean isAddAction = content.getId() == null;

		// String slug = StringUtils.isBlank(content.getSlug()) ?
		// content.getTitle() : content.getSlug();
		// content.setSlug(slug);

		Content dbContent = ContentQuery.me().findBySlug(content.getSlug());
		if (dbContent != null && content.getId() != null && dbContent.getId().compareTo(content.getId()) != 0) {
			renderAjaxResultForError();
			return;
		}

		boolean saved = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				Content oldContent = null;
				if (content.getId() != null) {
					oldContent = ContentQuery.me().findById(content.getId());
				}

				if (!content.saveOrUpdate()) {
					return false;
				}

				content.updateCommentCount();

				List<BigInteger> ids = getOrCreateTaxonomyIds(content.getModule());
				if (ids == null || ids.size() == 0) {
					MappingQuery.me().deleteByContentId(content.getId());
				} else {
					if (!MappingQuery.me().doBatchUpdate(content.getId(), ids.toArray(new BigInteger[0]))) {
						return false;
					}
				}

				if (metas != null) {
					for (Map.Entry<String, String> entry : metas.entrySet()) {
						content.saveOrUpdateMetadta(entry.getKey(), entry.getValue());
					}
				}

				MessageKit.sendMessage(Actions.CONTENT_COUNT_UPDATE, ids.toArray(new BigInteger[] {}));

				if (oldContent != null && oldContent.getTaxonomys() != null) {
					List<Taxonomy> taxonomys = oldContent.getTaxonomys();
					BigInteger[] taxonomyIds = new BigInteger[taxonomys.size()];
					for (int i = 0; i < taxonomys.size(); i++) {
						taxonomyIds[i] = taxonomys.get(i).getId();
					}
					MessageKit.sendMessage(Actions.CONTENT_COUNT_UPDATE, taxonomyIds);
				}

				return true;
			}
		});

		if (!saved) {
			renderAjaxResultForError();
			return;
		}

		if (isAddAction) {
			MessageKit.sendMessage(Actions.CONTENT_ADD, content);
		} else {
			MessageKit.sendMessage(Actions.CONTENT_UPDATE, content);
		}

		AjaxResult ar = new AjaxResult();
		ar.setErrorCode(0);
		ar.setData(content.getId());
		renderAjaxResult("save ok", 0, content.getId());
	}

	private String getModuleName() {
		return getPara("m");
	}

	private Content getContent() {
		Content content = getModel(Content.class);

		content.setText(JsoupUtils.getBodyHtml(content.getText()));

		if (content.getCreated() == null) {
			content.setCreated(new Date());
		}
		content.setModified(new Date());

		User user = getLoginedUser();
		content.setUserId(user.getId());

		return content;
	}

	public List<BigInteger> getOrCreateTaxonomyIds(String moduleName) {
		TplModule module = TemplateManager.me().currentTemplateModule(moduleName);
		List<TplTaxonomyType> types = module.getTaxonomyTypes();
		List<BigInteger> tIds = new ArrayList<BigInteger>();
		for (TplTaxonomyType type : types) {
			if (type.isInputType()) {
				String slugsData = getPara("_" + type.getName());
				if (StringUtils.isBlank(slugsData)) {
					continue;
				}

				List<Taxonomy> taxonomyList = TaxonomyQuery.me().findListByModuleAndType(moduleName, type.getName());

				String[] slugs = slugsData.split(",");
				for (String slug : slugs) {
					BigInteger id = getTaxonomyIdFromListBySlug(slug, taxonomyList);
					if (id == null) {
						Taxonomy taxonomy = new Taxonomy();
						taxonomy.setTitle(slug);
						taxonomy.setSlug(slug);
						taxonomy.setContentModule(moduleName);
						taxonomy.setType(type.getName());
						if (taxonomy.save()) {
							id = taxonomy.getId();
						}
					}
					tIds.add(id);
				}
			} else if (type.isSelectType()) {
				BigInteger[] ids = getParaValuesToBigInteger("_" + type.getName());
				if (ids != null && ids.length > 0)
					tIds.addAll(Arrays.asList(ids));
			}
		}
		return tIds;
	}

	private BigInteger getTaxonomyIdFromListBySlug(String slug, List<Taxonomy> list) {
		for (Taxonomy taxonomy : list) {
			if (slug.equals(taxonomy.getSlug()))
				return taxonomy.getId();
		}
		return null;
	}
	
	public void list() {
		String keyword = getPara("k", "").trim();
		BigInteger[] tids = null;
		Page<Content> page = null;
		if (StringUtils.isNotBlank(getStatus())) {
			page = ContentQuery.me().paginateBySearch(getPageNumber(), 2, getModuleName(), keyword,
					getStatus(), tids, null);
		} else {
			page = ContentQuery.me().paginateByModuleNotInDelete(getPageNumber(), 2, getModuleName(),
					keyword, tids, null);
		}
		
		setAttr("k", keyword);
		setAttr("page", page);
	}
	
	private String getStatus() {
		return getPara("s");
	}
}
