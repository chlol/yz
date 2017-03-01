package com.lnngle.yeezhuan.model;

import java.math.BigInteger;
import java.util.Date;

import com.jfinal.plugin.activerecord.IBean;

import io.jpress.model.core.JModel;
import io.jpress.model.core.Table;

@Table(tableName = "soaple_info", primaryKey = "id")
public class SoapleInfo extends JModel<SoapleInfo> implements IBean {
	private static final long serialVersionUID = -3542448661677263486L;

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}

	public java.math.BigInteger getId() {
		Object id = get("id");
		if (id == null)
			return null;

		return id instanceof BigInteger ? (BigInteger) id : new BigInteger(id.toString());
	}

	public int getTypeId() {
		return get("type_id");
	}

	public void setTypeId(int typeId) {
		set("type_id", typeId);
	}

	public String getField1() {
		return get("field1");
	}

	public void setField1(String field1) {
		set("field1", field1);
	}

	public String getField2() {
		return get("field2");
	}

	public void setField2(String field2) {
		set("field2", field2);
	}

	public String getField3() {
		return get("field3");
	}

	public void setField3(String field3) {
		set("field3", field3);
	}

	public String getField4() {
		return get("field4");
	}

	public void setField4(String field4) {
		set("field4", field4);
	}

	public String getField5() {
		return get("field5");
	}

	public void setField5(String field5) {
		set("field5", field5);
	}

	public String getField6() {
		return get("field6");
	}

	public void setField6(String field6) {
		set("field6", field6);
	}

	public String getField7() {
		return get("field7");
	}

	public void setField7(String field7) {
		set("field7", field7);
	}

	public String getField8() {
		return get("field8");
	}

	public void setField8(String field8) {
		set("field8", field8);
	}

	public String getField9() {
		return get("field9");
	}

	public void setField9(String field9) {
		set("field9", field9);
	}

	public String getField10() {
		return get("field10");
	}

	public void setField10(String field10) {
		set("field10", field10);
	}

	public String getField11() {
		return get("field11");
	}

	public void setField11(String field11) {
		set("field11", field11);
	}

	public String getField12() {
		return get("field12");
	}

	public void setField12(String field12) {
		set("field12", field12);
	}
	
	public int getStatus() {
		return get("status");
	}

	public void setStatus(int status) {
		set("status", status);
	}
	
	public Date getCreateDate() {
		return get("create_date");
	}

	public void setCreateDate(Date createDate) {
		set("create_date", createDate);
	}

}
