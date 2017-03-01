package com.lnngle.yeezhuan.soaple;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Page;
import com.lnngle.yeezhuan.Consts;
import com.lnngle.yeezhuan.KeyValue;
import com.lnngle.yeezhuan.model.SoapleInfo;
import com.lnngle.yeezhuan.model.SoapleInfoType;
import com.lnngle.yeezhuan.model.query.SoapleInfoQuery;
import com.lnngle.yeezhuan.model.query.SoapleInfoTypeQuery;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;

@RouterMapping(url = Consts.ROUTER_YZ_SP_ADMIN, viewPath = "/WEB-INF/yeezhuan/soaple")
@RouterNotAllowConvert
public class SoapleAdminController extends JBaseController {

	public void list() {
		String typeId = this.getPara("typeId");
		if (typeId == null || typeId.equals("")) {
			typeId = "1";//
		}

		List<SoapleInfoType> allType = getAllType();
		setAttr("allType", allType);

		List<KeyValue> list = getFieldLabel(typeId);
		StringBuffer select = new StringBuffer();
		List<String> fieldLabels = new ArrayList<String>();
		List<String> fieldNames = new ArrayList<String>();
		for (KeyValue fl : list) {
			select.append(fl.getKey()).append(",");
			fieldLabels.add((String) fl.getValue());
			fieldNames.add(fl.getKey());
		}
		setAttr("fieldLabels", fieldLabels);
		setAttr("fieldNames", fieldNames);
		Page<SoapleInfo> page = SoapleInfoQuery.me().searchSoapleInfo(getPageNumber(), this.getPageSize(),
				select.substring(0, select.length() - 1), Integer.parseInt(typeId));
		setAttr("page", page);
		render("sp_list.html");
		return;
	}

	private List<SoapleInfoType> getAllType() {
		return SoapleInfoTypeQuery.me().getAllTypeMap();
	}

	private List<KeyValue> getFieldLabel(String typeId) {
		List<SoapleInfoType> allType = getAllType();
		for (SoapleInfoType type : allType) {
			if (type.getId().intValue() == Integer.parseInt(typeId)) {
				String typeMap = type.getTypeMap();
				List<KeyValue> list = JSON.parseArray(typeMap, KeyValue.class);

				return list;
			}
		}

		return new ArrayList<KeyValue>();
	}

}
