package com.lnngle.yeezhuan.model.query;

import com.jfinal.plugin.activerecord.Page;
import com.lnngle.yeezhuan.model.SoapleInfo;

import io.jpress.model.query.JBaseQuery;

public class SoapleInfoQuery extends JBaseQuery {
	private static final SoapleInfoQuery QUERY = new SoapleInfoQuery();
	protected static final SoapleInfo DAO = new SoapleInfo();
	public static SoapleInfoQuery me() {
		return QUERY;
	}
	
	public Page<SoapleInfo> searchSoapleInfo(int pageNumber, int pageSize,String selectColumn, int typeId) {
		return DAO.paginate(pageNumber, pageSize, "select " + selectColumn + ",status,create_date", " from " + DAO.getTableName() + " where type_id=?",typeId);
	}
}
