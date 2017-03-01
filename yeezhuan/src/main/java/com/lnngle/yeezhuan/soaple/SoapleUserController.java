package com.lnngle.yeezhuan.soaple;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.lnngle.yeezhuan.Consts;
import com.lnngle.yeezhuan.KeyValue;
import com.lnngle.yeezhuan.model.SoapleInfo;
import com.lnngle.yeezhuan.model.SoapleInfoType;
import com.lnngle.yeezhuan.model.query.SoapleInfoTypeQuery;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_YZ_SP_USER, viewPath = "/WEB-INF/yeezhuan/soaple")
public class SoapleUserController extends JBaseController {
	public void toSave() {
		String typeId = this.getPara("typeId");
		if (typeId == null || typeId.equals("")) {
			typeId = "1";//
		}

		List<KeyValue> fieldLabel = getFieldLabel(typeId);
		setAttr("title", this.getTitle(typeId));
		setAttr("fields", fieldLabel);
		setAttr("typeId", typeId);
		render("sp_info.html");
		return;
	}

	public void save() {
		String typeId = this.getPara("typeId");
		SoapleInfo si = new SoapleInfo();
		si.setTypeId(Integer.parseInt(typeId));

		if (StringUtils.isNotEmpty(getPara("field1"))) {
			si.setField1(getPara("field1"));
		}
		if (StringUtils.isNotEmpty(getPara("field2"))) {
			si.setField2(getPara("field2"));
		}
		if (StringUtils.isNotEmpty(getPara("field3"))) {
			si.setField3(getPara("field3"));
		}
		if (StringUtils.isNotEmpty(getPara("field4"))) {
			si.setField4(getPara("field4"));
		}
		if (StringUtils.isNotEmpty(getPara("field5"))) {
			si.setField5(getPara("field5"));
		}
		if (StringUtils.isNotEmpty(getPara("field6"))) {
			si.setField6(getPara("field6"));
		}
		if (StringUtils.isNotEmpty(getPara("field7"))) {
			si.setField7(getPara("field7"));
		}
		if (StringUtils.isNotEmpty(getPara("field8"))) {
			si.setField8(getPara("field8"));
		}
		if (StringUtils.isNotEmpty(getPara("field9"))) {
			si.setField9(getPara("field9"));
		}
		if (StringUtils.isNotEmpty(getPara("field10"))) {
			si.setField10(getPara("field10"));
		}
		if (StringUtils.isNotEmpty(getPara("field11"))) {
			si.setField11(getPara("field11"));
		}
		if (StringUtils.isNotEmpty(getPara("field12"))) {
			si.setField12(getPara("field12"));
		} else {
			// do nothing
		}
		
		si.setStatus(1);
		si.setCreateDate(new Date());

		if (!si.save()) {
			setAttr(Consts.ERR_MSG_KEY, "门店申请失败");
		}
		render("sp_result.html");
		return;
	}

	private String getTitle(String typeId) {
		if ("1".equals(typeId)) {
			return "门店申请录入";
		} else {
			return "信息录入";
		}
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

	private List<SoapleInfoType> getAllType() {
		return SoapleInfoTypeQuery.me().getAllTypeMap();
	}
}
