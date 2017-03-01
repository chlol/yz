package com.lnngle.yeezhuan.model;

import java.math.BigInteger;

import com.jfinal.plugin.activerecord.IBean;

import io.jpress.model.core.JModel;
import io.jpress.model.core.Table;

@Table(tableName = "soaple_info_type", primaryKey = "id")
public class SoapleInfoType extends  JModel<SoapleInfoType> implements IBean {
	private static final long serialVersionUID = -3542448661677263486L;

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}
	
	public java.math.BigInteger getId() {
		Object id = get("id");
		if (id == null)
			return null;

		return id instanceof BigInteger ? (BigInteger)id : new BigInteger(id.toString());
	}
	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
	public String getTypeMap() {
		return get("type_map");
	}

	public void setTypeMap(String typeMap) {
		set("type_map", typeMap);
	}
}
