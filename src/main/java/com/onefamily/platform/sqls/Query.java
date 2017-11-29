package com.onefamily.platform.sqls;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.onefamily.platform.dataformat.Jackson2Helper;
import com.onefamily.platform.sqls.condition.OrderBy;
import com.onefamily.platform.sqls.mapping.MappingDb;
import com.onefamily.platform.utils.JdbcUtils;
import com.onefamily.platform.utils.StringTool;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * SQL SELECT查询器
 *
 *
 * @Date 2015年7月16日 下午3:21:32
 */
public class Query {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	private int start;
	private int limit;
	private final StringBuilder sql;
	private final List<Object> parameters;
	private final List<OrderBy> orders;
	private boolean isDubeg = true;
	private Class<?> clazz;

	/**
	 * 是否打印SQL及参数
	 * 
	 * @param isDubeg
	 *            true 是
	 * @return void
	 *
	 * @Date 2015年8月6日 上午11:04:02
	 * @Version 1.0.0
	 */
	public void setDubeg(boolean isDubeg) {
		this.isDubeg = isDubeg;
	}

	/**
	 * 默认构造器
	 */
	public Query() {
		this.sql = new StringBuilder();
		this.orders = new ArrayList<OrderBy>();
		this.parameters = new ArrayList<Object>();
	}

	/**
	 * 转入需要封装的PO类
	 * 
	 * @param clazz
	 */
	public Query(Class<?> clazz) {
		this.clazz = clazz;
		MappingDb mappingDb = new MappingDb(clazz);
		String table = mappingDb.getTableName();
		this.sql = new StringBuilder(" select * from ").append(table).append(" where 1=1 ");
		this.orders = new ArrayList<OrderBy>();
		this.parameters = new ArrayList<Object>();
	}

	public Query(Class<?> clazz,String tableName) {
		this.clazz = clazz;
		this.sql = new StringBuilder(" select * from ").append(tableName).append(" where 1=1 ");
		this.orders = new ArrayList<OrderBy>();
		this.parameters = new ArrayList<Object>();
	}
	/**
	 * 直接传入SQL
	 * 
	 * @param sql
	 */
	public Query(String sql) {
		this.sql = new StringBuilder(sql);
		this.orders = new ArrayList<OrderBy>();
		this.parameters = new ArrayList<Object>();
	}

	/**
	 * 静态调用转入需要封装的PO类
	 * 
	 * @param clazz
	 */
	public static Query forClass(Class<?> clazz) {
		return new Query(clazz);
	}

	public Query mapping(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}

	/**
	 * 查询全字段数据库表
	 * 
	 * @param table
	 *            表名
	 * @return Query
	 *
	 * @Date 2015年8月6日 上午11:05:58
	 * @Version 1.0.0
	 */
	public static Query selectAll(String table) {
		return new Query(" select * from ").append(table).append(" where 1=1 ");
	}

	/**
	 * 查询部分字段
	 * 
	 * @param table
	 *            表名
	 * @param columnNames
	 *            字段数组
	 * @return Query
	 *
	 * @Date 2015年8月6日 上午11:06:44
	 * @Version 1.0.0
	 */
	public static Query select(String table, String... columnNames) {
		Query sql = new Query(" select ");
		sql.append(StringUtils.join(columnNames, ","));
		sql.append(" from ");
		sql.append(table);
		sql.append(" where 1=1 ");
		return sql;
	}

	/**
	 * 直接拼接sql片段
	 * 
	 * @param segment
	 *            sql片段
	 * @return
	 * @return Query
	 *
	 * @Date 2015年7月16日 上午10:52:11
	 * @Version 1.0.0
	 */
	public Query append(Object segment) {
		if (StringTool.isEmpty(segment))
			return this;

		sql.append(" ").append(segment).append(" ");
		return this;
	}

	/**
	 * 最终SQL
	 */
	@Override
	public String toString() {
		return sql.toString();
	}

	/**
	 * 参数集合
	 * 
	 * @return List<Object>
	 *
	 * @Date 2015年8月6日 上午11:09:00
	 * @Version 1.0.0
	 */
	public List<Object> getParameters() {
		return parameters;
	}

	/**
	 * 添加参数
	 * 
	 * @param parameter
	 * @return Query
	 *
	 * @Date 2015年8月6日 上午11:10:41
	 * @Version 1.0.0
	 */
	public Query setParameters(Object parameter) {
		if (StringTool.isEmpty(parameter))
			return this;

		if (parameter instanceof Collection<?>) {
			parameters.addAll((Collection<?>) parameter);
			return this;
		}

		parameters.add(parameter);
		return this;
	}

	/**
	 * 拼接SQL查询条件（and）
	 * 
	 * 保存参数值
	 * 
	 * @param columnName
	 *            字段
	 * @param value
	 *            参数值
	 * @return Query
	 *
	 * @Date 2015年6月30日 上午11:46:48
	 * @Version 1.0.0
	 */
	public Query eq(String columnName, Object value) {

		if (StringTool.isEmpty(value))
			return this;

		sql.append(" and ").append(columnName).append(" = ? ");
		setParameters(value);
		return this;
	}

	/** 不相等 */
	public Query notEq(String columnName, Object value) {
		if (StringTool.isEmpty(value))
			return this;

		sql.append(" and ").append(columnName).append(" != ? ");
		setParameters(value);
		return this;
	}

	/**
	 * in
	 * 
	 * @param columnName
	 * @param value
	 * @return Query
	 *
	 * @Date 2015年7月15日 下午5:22:49
	 * @Version 1.0.0
	 */
	public Query in(String columnName, Object value) {
		if (StringTool.isEmpty(value))
			return this;

		in(columnName, value, " in ");
		return this;
	}

	/**
	 * not in
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            值集合 是否需要引号
	 * @return
	 */
	public Query notIn(String columnName, Object value) {
		if (StringTool.isEmpty(value))
			return this;
		in(columnName, value, " not in ");
		return this;
	}

	private Query in(String columnName, Object value, String opt) {

		if (StringTool.isEmpty(value) || "null".equals(value))
			return this;

		List<String> params = Lists.newArrayList();
		if (value instanceof Collection<?>) {
			Collection<?> valueList = (Collection<?>) value;
			for (Object v : valueList) {
				setParameters(v);
				params.add("?");
			}
		} else {
			setParameters(value);
			params.add("?");
		}

		sql.append(" and ").append(columnName).append(opt).append("( ").append(Joiner.on(",").join(params)).append(" )");
		return this;
	}

	/** 空 */
	public Query isNull(String columnName) {
		if (StringTool.isEmpty(columnName))
			return this;

		sql.append(" and ").append(columnName).append(" is null ");
		return this;
	}

	/** 非空 */
	public Query isNotNull(String columnName) {
		if (StringTool.isEmpty(columnName))
			return this;

		sql.append(" and ").append(columnName).append(" is not null ");
		return this;
	}

	/** 或 */
	public Query or(String columnName, Object value) {
		if (StringTool.isEmpty(columnName))
			return this;
		if (StringTool.isEmpty(value))
			return this;
		sql.append(" or ").append(columnName).append(" = ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 模糊匹配
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query like(String columnName, String value) {
		if (StringTool.isEmpty(value))
			return this;
		if (value.indexOf("%") < 0)
			value = StringUtils.join("%", value, "%");

		sql.append(" and ").append(columnName).append(" like ?");
		setParameters(value);
		return this;
	}

	/**
	 * 模糊匹配
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query notLike(String columnName, String value) {
		if (StringTool.isEmpty(value))
			return this;
		if (value.indexOf("%") < 0)
			value = StringUtils.join("%", value, "%");

		sql.append(" and ").append(columnName).append(" not like ?");
		setParameters(value);
		return this;
	}

	/**
	 * 时间区间查询
	 * 
	 * @param columnName
	 *            属性名称
	 * @param lo
	 *            日期属性起始值
	 * @param go
	 *            日期属性结束值
	 * @return
	 */
	public Query between(String columnName, Object lo, Object go) {
		if (StringTool.isEmpty(lo) && StringTool.isEmpty(go)) {
			return this;
		}

		if (StringTool.isNotEmpty(lo)) {
			if (lo instanceof Timestamp) {
				lo = ((Timestamp) lo).getTime();
			} else if (lo instanceof Date) {
				lo = new Timestamp(((Date) lo).getTime());
			}
		}

		if (StringTool.isNotEmpty(go)) {
			if (go instanceof Timestamp) {
				go = ((Timestamp) go).getTime();
			} else if (go instanceof Date) {
				go = new Timestamp(((Date) go).getTime());
			}
		}

		if (StringTool.isNotEmpty(lo) && StringTool.isEmpty(go)) {
			sql.append(" and ").append(columnName).append(" >= ? ");
			setParameters(lo);
			return this;
		}

		if (StringTool.isEmpty(lo) && StringTool.isNotEmpty(go)) {
			sql.append(" and ").append(columnName).append(" <= ? ");
			setParameters(go);
			return this;
		}

		sql.append(" and ( ").append(columnName).append(" between ? and ? ) ");
		setParameters(lo);
		setParameters(go);
		return this;
	}

	/**
	 * 数字区间查询
	 * 
	 * @param columnName
	 * @param lo
	 * @param go
	 * @return Query
	 *
	 * @Date 2015年8月6日 上午11:11:30
	 * @Version 1.0.0
	 */
	public Query between(String columnName, Number lo, Number go) {
		if (StringTool.isNotEmpty(lo))
			ge(columnName, lo);

		if (StringTool.isNotEmpty(go))
			le(columnName, go);

		return this;
	}

	/**
	 * 小于等于
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query le(String columnName, Number value) {
		if (StringTool.isEmpty(value)) {
			return this;
		}
		sql.append(" and ").append(columnName).append(" <= ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 小于
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query lt(String columnName, Number value) {
		if (StringTool.isEmpty(value)) {
			return this;
		}
		sql.append(" and ").append(columnName).append(" < ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 大于等于
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query ge(String columnName, Number value) {
		if (StringTool.isEmpty(value)) {
			return this;
		}
		sql.append(" and ").append(columnName).append(" >= ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 大于等于
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query ge(String columnName, Timestamp value) {
		if (StringTool.isEmpty(value)) {
			return this;
		}
		sql.append(" and ").append(columnName).append(" >= ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 大于等于
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query ge(String columnName, java.sql.Date value) {
		if (StringTool.isEmpty(value)) {
			return this;
		}
		sql.append(" and ").append(columnName).append(" >= ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 大于
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public Query gt(String columnName, Number value) {
		if (StringTool.isEmpty(value)) {
			return this;
		}
		sql.append(" and ").append(columnName).append(" > ? ");
		setParameters(value);
		return this;
	}

	/**
	 * 排序
	 * 
	 * @param order
	 * @return Query
	 *
	 * @Date 2015年7月28日 下午3:41:43
	 * @Version 1.0.0
	 */
	public Query orderBy(OrderBy order) {
		if (StringTool.isEmpty(order)) {
			return this;
		}
		this.orders.add(order);
		return this;
	}

	/**
	 * 查询条数
	 * 
	 * @param start
	 *            起始条数
	 * @param limit
	 *            条数
	 * @return Query
	 *
	 * @Date 2015年8月6日 上午11:12:09
	 * @Version 1.0.0
	 */
	public Query limit(int start, int limit) {
		this.start = start;
		this.limit = limit;
		return this;
	}

	/**
	 * 
	 * @param limit
	 *            条数
	 * @return Query
	 *
	 * @Date 2015年8月7日 下午4:44:30
	 * @Version 1.0.0
	 */
	public Query limit(int limit) {
		this.start = 0;
		this.limit = limit;
		return this;
	}

	private void limit() {
		if (start < 0 || limit < 0) {
			throw new RuntimeException("LIMIT  or OFFSET must not be negative");
		}
		if (limit > 0) {
			sql.append(" limit ? ");
			parameters.add(limit);
		}
		if (start > 0) {
			sql.append(" offset ? ");
			parameters.add(start);
		}
	}

	/** 排序 */
	private void appendOrderBy() {
		if (StringTool.isEmpty(orders)) {
			limit();
			return;
		}
		sql.append(" order by ");

		int size = orders.size();

		for (int i = 0; i < size; i++) {
			sql.append(orders.get(i).toString());
			if (i < size - 1) {
				sql.append(",");
			}
		}
		limit();
	}

	/**
	 * 将查询结果封装到对象（clazz）
	 * 
	 * @param clazz
	 * @return BeanHandler<T>
	 *
	 * @Date 2015年8月6日 上午11:13:09
	 * @Version 1.0.0
	 */
	public static <T> BeanHandler<T> getBeanHandler(Class<T> clazz) {
		RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
		BeanHandler<T> bh = new BeanHandler<T>(clazz, rowProcessor);
		return bh;
	}

	/**
	 * 将查询结果封装到集合（List<clazz>）
	 * 
	 * @param clazz
	 * @return BeanListHandler<T>
	 *
	 * @Date 2015年8月6日 上午11:14:23
	 * @Version 1.0.0
	 */
	public static <T> BeanListHandler<T> getBeanListHandler(Class<T> clazz) {
		RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
		BeanListHandler<T> bh = new BeanListHandler<T>(clazz, rowProcessor);
		return bh;
	}

	/**
	 * 默认统计count(id)或count(*)
	 * 
	 * @throws SQLException
	 * @return Long
	 *
	 * @Date 2015年8月6日 上午11:14:55
	 * @Version 1.0.0
	 */
	public Long getCount() throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}
		String sqlStr = sql.toString();
		if (!sql.toString().toLowerCase().contains("select count")) {
			sqlStr = StringUtils.join("select count(*) from (", sql.toString(), ") as _t");
		}

		ScalarHandler<Long> handler = new ScalarHandler<Long>(1);
		Long count = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sqlStr, handler, parameters.toArray());
		debug(count, sqlStr);
		return count;
	}

	/**
	 * 统计
	 * 
	 * @param sh
	 *            指定字段
	 * @throws SQLException
	 * @return T
	 *
	 * @Date 2015年8月6日 上午11:16:27
	 * @Version 1.0.0
	 */
	public <T> T getCount(ScalarHandler<T> sh) throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}
		T count = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), sh, parameters.toArray());
		debug(count);
		return count;
	}

	/**
	 * 返回Map结果集
	 * 
	 * @throws SQLException
	 * @return Map<String,Object>
	 *
	 * @Date 2015年8月6日 上午11:17:50
	 * @Version 1.0.0
	 */
	public Map<String, Object> map() throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}
		appendOrderBy();
		Map<String, Object> map = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), new MapHandler(), parameters.toArray());
		debug(map);
		return map;
	}

	/**
	 * 返回List<Map>集合
	 * 
	 * @throws SQLException
	 * @return List<Map<String,Object>>
	 *
	 * @Date 2015年8月6日 上午11:18:15
	 * @Version 1.0.0
	 */
	public List<Map<String, Object>> listmap() throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}
		appendOrderBy();
		List<Map<String, Object>> map = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), new MapListHandler(), parameters.toArray());
		debug(map);
		return map;
	}

	/**
	 * 按指定字段返回List<String>集合
	 * 
	 * @param column
	 * @throws SQLException
	 * @return List<T>
	 *
	 * @Date 2016年9月9日 下午3:59:42
	 * @Version 1.0.0
	 */
	public <T> List<T> listColumn(String column) throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}

		appendOrderBy();

		ColumnListHandler<T> handler = new ColumnListHandler<T>(column);
		List<T> list = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), handler, parameters.toArray());
		debug(list);
		return list;
	}

	/**
	 * 返回单个对象
	 * 
	 * @throws SQLException
	 * @return T
	 *
	 * @Date 2015年8月6日 上午11:24:08
	 * @Version 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> T singleResult() throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}

		if (StringTool.isEmpty(clazz)) {
			throw new IllegalArgumentException("Not set clazz!");
		}
		appendOrderBy();
		BeanHandler<T> beanHandler = (BeanHandler<T>) getBeanHandler(clazz);
		T list = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), beanHandler, parameters.toArray());
		debug(list);
		return list;
	}

	/**
	 * 返回集合
	 * 
	 * @throws SQLException
	 * @return List<T>
	 *
	 * @Date 2015年8月6日 上午11:24:37
	 * @Version 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> list() throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}

		if (StringTool.isEmpty(clazz)) {
			throw new IllegalArgumentException("Not set clazz!");
		}

		appendOrderBy();

		BeanListHandler<T> beanListHandler = (BeanListHandler<T>) getBeanListHandler(clazz);
		List<T> list = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), beanListHandler, parameters.toArray());

		debug(list);
		return list;
	}

	/**
	 * 返回单个指定对象
	 * 
	 * @param clazz
	 *            指定封装对象
	 * @throws SQLException
	 * @return T
	 *
	 * @Date 2015年8月6日 上午11:25:13
	 * @Version 1.0.0
	 */
	public <T> T singleResult(Class<T> clazz) throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}
		BeanHandler<T> beanHandler = getBeanHandler(clazz);
		T list = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), beanHandler, parameters.toArray());
		debug(list);
		return list;
	}

	/**
	 * 返回指定对象集合
	 * 
	 * @param clazz
	 *            指定封装对象
	 * @throws SQLException
	 * @return List<T>
	 *
	 * @Date 2015年8月6日 上午11:25:48
	 * @Version 1.0.0
	 */
	public <T> List<T> list(Class<T> clazz) throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}

		appendOrderBy();

		BeanListHandler<T> beanListHandler = getBeanListHandler(clazz);
		List<T> list = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), beanListHandler, parameters.toArray());

		debug(list);
		return list;
	}

	/**
	 * 返回封装字段集合
	 * 
	 * @param columnListHandler
	 *            指定字段
	 * @throws SQLException
	 * @return List<T>
	 *
	 * @Date 2015年8月6日 上午11:26:26
	 * @Version 1.0.0
	 */
	public <T> List<T> list(ColumnListHandler<T> columnListHandler) throws SQLException {
		if (StringTool.isEmpty(sql)) {
			return null;
		}

		appendOrderBy();

		List<T> list = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sql.toString(), columnListHandler, parameters.toArray());
		debug(list);
		return list;
	}

	/**
	 * 删除
	 *
	 * @throws SQLException
	 * @return int
	 *
	 * @Date 2015年11月26日 下午7:05:38
	 * @Version 1.0.0
	 */
	public int delete() throws SQLException {

		if (StringTool.isEmpty(sql)) {
			return 0;
		}

		String querySql = sql.toString();
		int start = querySql.toLowerCase().indexOf("from");
		String deleteSql = StringUtils.join("delete ", querySql.substring(start, querySql.length()));
		int update = JdbcUtils.getRunner().update(JdbcUtils.getConnection(), deleteSql, parameters.toArray());
		debug(update);
		return update;
	}

	/**
	 * 打印日志
	 * 
	 * @param result
	 * @return void
	 *
	 * @Date 2015年8月6日 上午11:27:08
	 * @Version 1.0.0
	 */
	private void debug(Object result) {
		debug(result, null);
	}

	private void debug(Object result, String sqlStr) {

		if (!isDubeg) {
			return;
		}

		if (StringUtils.isNotBlank(sqlStr))
			log.debug("SQL={}", sqlStr);
		else
			log.debug("SQL={}", sql.toString());
		log.debug("SQL Parameters={}", Jackson2Helper.toJsonString(parameters));

	}
}
