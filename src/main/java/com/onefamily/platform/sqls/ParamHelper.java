package com.onefamily.platform.sqls;

import com.onefamily.platform.exception.GenericBusinessException;

import java.io.Serializable;
import java.util.List;


public class ParamHelper {

	public static String arrayToString(Object[] objs) {
		StringBuilder sb = new StringBuilder("");
		if (objs != null && objs.length > 0){
			for (Object obj : objs){
				sb.append(obj != null ? obj.toString() : "null");
				sb.append(";");
			}
		}
		return sb.toString();
	}
	
	public static String arrayToString(List<Serializable> objs) {
		StringBuilder sb = new StringBuilder("");
		if (objs != null && objs.size() > 0){
			for (Object obj : objs){
				sb.append(obj != null ? obj.toString() : "null");
				sb.append(";");
			}
		}
		return sb.toString();
	}
	
	public static void in(List<Serializable> params, StringBuilder sql, String columnName, String paramIds) throws GenericBusinessException {
		String[] ids = paramIds.split(",");
		if (ids.length > 0){
			sql.append(" and " + columnName + " in (");
			for (String id : ids){
				sql.append("?,");
				params.add(Long.parseLong(id));
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}else{
			throw new GenericBusinessException("传入id有误");
		}
	}
	
	public static void in(List<Serializable> params, StringBuilder sql, String columnName){
		if (params.size() > 0){
			sql.append(" and " + columnName + " in (");
			for (Serializable id : params){
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}
	}
	
	public static void in(List<Serializable> params, StringBuilder sql, String columnName, String paramIds, String symbol, int type) throws GenericBusinessException{
		String[] ids = paramIds.split(",");
		if (ids.length > 0){
			sql.append(" " + symbol + " " + columnName + " in (");
			for (String id : ids){
				sql.append("?,");
				if (type == 1){
					params.add(Long.parseLong(id));
				}
				if (type == 2){
					params.add(id);
				}
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}else{
			throw new GenericBusinessException("传入id有误");
		}
	}
}
