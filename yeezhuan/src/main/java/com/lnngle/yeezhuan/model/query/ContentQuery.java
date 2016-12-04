package com.lnngle.yeezhuan.model.query;

import java.math.BigInteger;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.Content;
import io.jpress.utils.StringUtils;

public class ContentQuery extends io.jpress.model.query.ContentQuery {
	private static final ContentQuery QUERY = new ContentQuery();
	public static ContentQuery me() {
		return QUERY;
	}
	
	public Page<Content> paginateForPublic(int page, int pagesize, String module, String keyword,
			BigInteger[] taxonomyIds, String month,BigInteger userId,boolean isPublic) {
		String select = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username,u.nickname,u.avatar";

		StringBuilder fromBuilder = new StringBuilder(" from content c");
		fromBuilder.append(" left join mapping m on c.id = m.`content_id`");
		fromBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		fromBuilder.append(" left join user u on c.user_id = u.id");
		fromBuilder.append(" where c.status <> ?");
		
		if (isPublic) {
			fromBuilder.append(" and c.id not in (select content_id from userincluded WHERE user_id=" + userId +  ")");
		} else {
			fromBuilder.append(" and (c.user_id = " + userId + " or c.id in (select content_id from userincluded WHERE user_id=" + userId +  "))");
		}

		LinkedList<Object> params = new LinkedList<Object>();
		params.add(Content.STATUS_DELETE);

		boolean needWhere = false;
		needWhere = appendIfNotEmpty(fromBuilder, "c.module", module, params, needWhere);

		if (StringUtils.isNotBlank(keyword)) {
			fromBuilder.append(" AND c.title like ? ");
			params.add("%" + keyword + "%");
		}

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			fromBuilder.append(" AND t.id in " + toString(taxonomyIds));
		}

		if (StringUtils.isNotBlank(month)) {
			fromBuilder.append(" DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		fromBuilder.append(" group by c.id");
		fromBuilder.append(" ORDER BY c.created DESC");

		if (params.isEmpty()) {
			return DAO.paginate(page, pagesize, true, select, fromBuilder.toString());
		}

		return DAO.paginate(page, pagesize, true, select, fromBuilder.toString(), params.toArray());
	}
}
