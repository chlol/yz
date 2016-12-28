package com.lnngle.yeezhuan.admin.controller;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.lnngle.yeezhuan.interceptor.UserInterceptor;
import com.lnngle.yeezhuan.model.UserIncluded;
import com.lnngle.yeezhuan.model.query.ContentQuery;

import io.jpress.core.JBaseController;
import io.jpress.core.render.AjaxResult;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.User;
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
		String originalName = this.getPara("originalName");
		if (!StringUtils.isBlank(originalName)) {
			metas.put("originalName", originalName);
		}
		
		String originalUrl = this.getPara("originalUrl");
		if (!StringUtils.isBlank(originalUrl)) {
			metas.put("originalUrl", originalUrl);
		}
		
		final Content content = getContent();

		if (StringUtils.isBlank(content.getTitle())) {
			renderAjaxResultForError("文章标题不能为空！");
			return;
		}

		final boolean isAddAction = content.getId() == null;

		boolean saved = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (StringUtils.isBlank(content.getSlug())) {
					content.setSlug(UUID.randomUUID().toString().replace("-", ""));
				}
				
				Content oldContent = null;
				if (!isAddAction) {
					oldContent = ContentQuery.me().findById(content.getId());
					oldContent.setText(content.getText());
					oldContent.setModified(content.getModified());
					oldContent.setUserId(content.getUserId());
					oldContent.setModule(content.getModule());
					oldContent.setStatus(content.getStatus());
					oldContent.setCreated(content.getCreated());
					oldContent.setSlug(content.getSlug());
					if (!oldContent.saveOrUpdate()) {
						return false;
					}
				} else {
					if (!content.saveOrUpdate()) {
						return false;
					}
				}

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
	
	public void mList() {
		String keyword = getPara("k", "").trim();
		BigInteger[] tids = null;
		Page<Content> page = null;
		page = ContentQuery.me().paginateForPublic(getPageNumber(), this.getPageSize(), getModuleName(),
				keyword, tids, null,this.getLoginedUser().getId(),false);
		
		setAttr("k", keyword);
		setAttr("page", page);
		setAttr("m", getModuleName());
	}
	public void pList() {
		list();
	}
	private void list() {
		String keyword = getPara("k", "").trim();
		setAttr("tids", getPara("tids"));
		BigInteger[] tids = null;
		String[] tidStrings = getPara("tids", "").split(",");

		List<BigInteger> tidList = new ArrayList<BigInteger>();
		for (String stringid : tidStrings) {
			if (StringUtils.isNotBlank(stringid)) {
				tidList.add(new BigInteger(stringid));
			}
		}
		tids = tidList.toArray(new BigInteger[] {});
		Page<Content> page = ContentQuery.me().paginateForPublic(getPageNumber(), this.getPageSize(), getModuleName(),
					keyword, tids, null,this.getLoginedUser().getId(),true);

		filterUI(tids);
		setAttr("k", keyword);
		setAttr("page", page);
		setAttr("m", getModuleName());
	}
	
	private void filterUI(BigInteger[] tids) {
		TplModule module = TemplateManager.me().currentTemplateModule(getModuleName());

		if (module == null) {
			return;
		}

		List<TplTaxonomyType> types = module.getTaxonomyTypes();
		if (types != null && !types.isEmpty()) {
			HashMap<String, List<Taxonomy>> _taxonomyMap = new HashMap<String, List<Taxonomy>>();

			for (TplTaxonomyType type : types) {
				// 排除标签类的分类删选
				if (TplTaxonomyType.TYPE_SELECT.equals(type.getFormType())) {
					List<Taxonomy> taxonomys = TaxonomyQuery.me().findListByModuleAndTypeAsSort(getModuleName(),
							type.getName());
					processSelected(tids, taxonomys);
					_taxonomyMap.put(type.getTitle(), taxonomys);
				}
			}

			setAttr("_taxonomyMap", _taxonomyMap);
		}
	}

	private static final String Select_Key = "_selected";
	private void processSelected(BigInteger[] tids, List<Taxonomy> taxonomys) {
		if (taxonomys == null || taxonomys.isEmpty()) {
			clearSelect(taxonomys);
			return;
		}
		if (tids == null || tids.length == 0) {
			clearSelect(taxonomys);
			return;
		}
		for (Taxonomy t : taxonomys) {
			for (BigInteger id : tids) {
				if (t.getId().compareTo(id) == 0) {
					t.put(Select_Key, "selected=\"selected\"");
				}
			}
		}
	}
	
	private void clearSelect(List<Taxonomy> taxonomys) {
		for (Taxonomy t : taxonomys) {
			t.remove(Select_Key);
		}
	}
	
	public void included() {
		String contentId = this.getPara("cid");
		UserIncluded ui = new UserIncluded();
		ui.setContentId(new BigInteger(contentId));
		ui.setUserId(this.getLoginedUser().getId());
		if (ui.save()) {
			this.renderAjaxResultForSuccess("文章收录成功！");
		} else {
			this.renderAjaxResultForError("文章收录失败！");
		}
	}
	
	public void unIncluded() {
		String includedId = this.getPara("iid");
		UserIncluded ui = new UserIncluded();
		ui.setId(new BigInteger(includedId));
		if (ui.delete()) {
			this.renderAjaxResultForSuccess("删除文章收录成功！");
		} else {
			this.renderAjaxResultForError("删除文章收录失败！");
		}
	}
}
