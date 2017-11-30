package com.onefamily.platform.utils;


/**
 * PaginationHelper 负责构造一个传递给BO的PaginationSupport对象。<br>
 * 
 * @author <a href="mailto:senton1101@gmail.com">TianXiangdong</a> at 2010-9-1 下午05:20:14
 * @version 1.0
 * @see PaginationSupport
 */
public class PaginationHelper {

	/**
	 * 使用自定义分页参数获取分页类
	 * 
	 * @param <E>
	 *            返回数据中列表项中的实体对象类型
	 * @param nowPageStr
	 *            当前第几页
	 * @param doAction
	 *            当前操作(first:首页;pre:前一页;next:后一页;last:末页)
	 * @param totalPagesStr
	 *            所有页数
	 * @param pageSizeStr
	 *            每页记录数
	 * @return PaginationSupport
	 */
	public static <E extends Object> PaginationSupport<E> getPaginationSupport(String nowPageStr, String doAction, String totalPagesStr, String pageSizeStr) {
		int nowPage = 1;
		if (nowPageStr != null && !"".equals(nowPageStr)) {
			nowPage = new Integer(nowPageStr).intValue();
		}
		int totalPages = 1;
		if (totalPagesStr != null && !"".equals(totalPagesStr)) {
			totalPages = new Integer(totalPagesStr).intValue();
		}
		int pageSize = PaginationSupport.PAGESIZE;
		if (pageSizeStr != null && !"".equals(pageSizeStr)) {
			pageSize = new Integer(pageSizeStr).intValue();
		}
		if (doAction == null || "".equals(doAction)) {
			doAction = "first";
		}
		if ("first".equals(doAction)) {
			nowPage = 1;
		} else if ("pre".equals(doAction)) {
			if (nowPage > 1) {
				nowPage = nowPage - 1;
			}
		} else if ("next".equals(doAction)) {
			if (nowPage < totalPages) {
				nowPage = nowPage + 1;
			} else if (nowPage > totalPages) {
				nowPage = totalPages;
			}
		} else if ("last".equals(doAction)) {
			nowPage = totalPages;
		}
		return new PaginationSupport<E>(null, 0, pageSize, (nowPage - 1) * pageSize);
	}

	/**
	 * 用于从一个前端识别的分页vo,转换为后端分页识别的vo
	 * 
	 * @param pqc
	 * @return
	 */
	public static <E extends Object> PaginationSupport<E> getPageInfo(PaginationQC pqc) {
		PaginationSupport<E> pageSupport = new PaginationSupport<E>();
		Integer start = StringTool.isEmpty(pqc.getStart()) ? 0 : pqc.getStart();
		Integer limit = StringTool.isEmpty(pqc.getLimit()) ? 20 : pqc.getLimit();
		pageSupport.setStartIndex(start*limit);
		pageSupport.setPageSize(limit);
		return pageSupport;
	}
}
