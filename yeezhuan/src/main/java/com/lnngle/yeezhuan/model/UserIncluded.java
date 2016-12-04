package com.lnngle.yeezhuan.model;

import java.math.BigInteger;

import com.jfinal.plugin.activerecord.IBean;

import io.jpress.model.core.JModel;
import io.jpress.model.core.Table;

@Table(tableName = "user_included", primaryKey = "id")
public class UserIncluded extends  JModel<UserIncluded> implements IBean {
	private static final long serialVersionUID = -3968455721063535454L;
	
	public void setId(java.math.BigInteger id) {
		set("id", id);
	}
	
	public void setUserId(java.math.BigInteger userId) {
		set("user_id", userId);
	}
	
	public void setContentId(java.math.BigInteger contentId) {
		set("content_id", contentId);
	}
	
	public java.math.BigInteger getId() {
		Object id = get("id");
		if (id == null)
			return null;

		return id instanceof BigInteger ? (BigInteger)id : new BigInteger(id.toString());
	} 
	
	public java.math.BigInteger getUserId() {
		Object id = get("user_id");
		if (id == null)
			return null;

		return id instanceof BigInteger ? (BigInteger)id : new BigInteger(id.toString());
	} 
	
	public java.math.BigInteger getContentId() {
		Object id = get("content_id");
		if (id == null)
			return null;

		return id instanceof BigInteger ? (BigInteger)id : new BigInteger(id.toString());
	} 
}
