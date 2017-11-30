package com.onefamily.platform.utils;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PaginationQC implements Serializable {

	@Override
	public String toString() {
		return "PaginationQC [page=" + page + ", start=" + start + ", limit=" + limit + "]";
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		if (start <= 0) {
			start = 0;
		}
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	private Integer page;
	private Integer start;
	private Integer limit;
	private String sort;
	private String dir;

}
