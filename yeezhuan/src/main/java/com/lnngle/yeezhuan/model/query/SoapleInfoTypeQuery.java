package com.lnngle.yeezhuan.model.query;

import java.util.List;

import com.lnngle.yeezhuan.model.SoapleInfoType;

import io.jpress.model.query.JBaseQuery;

public class SoapleInfoTypeQuery extends JBaseQuery {
	private static final SoapleInfoTypeQuery QUERY = new SoapleInfoTypeQuery();
	protected static final SoapleInfoType DAO = new SoapleInfoType();
	public static SoapleInfoTypeQuery me() {
		return QUERY;
	}
	
	public List<SoapleInfoType> getAllTypeMap() {
		return DAO.doFindByCache("wechat_dkf", "sp_all_SoapleInfoType");
	}

}
