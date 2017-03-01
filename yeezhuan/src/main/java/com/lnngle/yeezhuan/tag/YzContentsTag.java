package com.lnngle.yeezhuan.tag;

import java.util.List;

import com.lnngle.yeezhuan.model.query.ContentQuery;

import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.utils.StringUtils;

public class YzContentsTag extends JTag {
	public static final String TAG_NAME = "jp.yzcontents";

	@Override
	public void onRender() {

		String orderBy = getParam("orderBy");
		String keyword = getParam("keyword");
		String module = getParam("module");
		String excludeId = getParam("excludeId");

		int pageNumber = getParamToInt("page", 1);
		int pageSize = getParamToInt("pageSize", 10);

		Integer count = getParamToInt("count");
		if (count != null && count > 0) {
			pageSize = count;
		}
		
		int cId = 0;
		if (StringUtils.isNotEmpty(excludeId)) {
			cId = Integer.parseInt(excludeId);
		}

		List<Content> data = ContentQuery.me().searchContents(pageNumber, pageSize, module, keyword,
				orderBy,cId);

		if (data == null || data.isEmpty()) {
			renderText("");
			return;
		}

		setVariable("contents", data);
		renderBody();
	}
}
