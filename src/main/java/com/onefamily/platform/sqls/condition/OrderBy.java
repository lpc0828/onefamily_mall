package com.onefamily.platform.sqls.condition;

import com.onefamily.platform.utils.StringTool;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 排序
 *
 * @Author 杨健/YangJian
 * @Date 2015年7月28日 下午3:41:20
 */
public class OrderBy {

	private OrderByType order;
	private final StringBuilder sql;
	private final List<String> columns = new ArrayList<>();

	public OrderBy() {
		this.sql = new StringBuilder();
		this.order = OrderByType.ASC;
	}

	public OrderBy(String... columns) {
		this.sql = new StringBuilder();
		this.columns.addAll(Arrays.asList(columns));
	}

	public OrderBy(OrderByType order, String... columns) {
		this(columns);
		this.order = order;
	}

	public OrderBy column(String column) {
		return column(column, OrderByType.ASC);
	}

	public OrderBy columns(String... columns) {
		this.columns.addAll(Arrays.asList(columns));
		this.order = OrderByType.ASC;
		return this;
	}

	public OrderBy columns(OrderByType order, String... columns) {
		columns(columns);
		this.order = order;
		return this;
	}

	public OrderBy column(String column, OrderByType order) {
		if (order == null) {
			return column(column);
		}
		columns.add(column);
		this.order = order;
		return this;
	}

	public String toString() {
		sql.append(" ");
		sql.append(StringUtils.join(columns, ", "));
		sql.append(" ");
		if (!StringTool.isEmpty(order)) {
			sql.append(order.name());
			sql.append(" ");
		}
		return sql.toString();
	}
}