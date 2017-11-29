package com.onefamily.platform.sqls;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.onefamily.platform.sqls.mapping.MappingDb;
import com.onefamily.platform.utils.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static com.onefamily.platform.utils.StringTool.isEmpty;
import static com.onefamily.platform.utils.StringTool.isNotEmpty;

/**
 * SQL UPDATE 更新
 *
 * @Author 杨健/YangJian
 * @Date 2015年7月21日 上午10:15:32
 */
public class Update {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	private boolean terminated = false;

	private String table;
	private MappingDb mappingDb;

	private Collection<String> conditions;

	private final List<String> columns;

	private final List<Object> parameters;

	private final StringBuilder sql;

	public Update() {
		this.sql = new StringBuilder(" update ");
		this.columns = new LinkedList<>();
		this.parameters = new LinkedList<>();
		this.conditions = new LinkedList<>();
	}

	public Update(String table) {
		this();
		this.table = table;
	}

	public Update table(String table) {
		this.table = table;
		return this;
	}

	public Update(Class<?> clazz) {
		this();
		this.table = new MappingDb(clazz).getTableName();
	}

	public Update(Object obj) {
		this();
		this.mappingDb = new MappingDb(obj);
		this.table = mappingDb.getTableName();
	}

	public Update(Object obj, List<String> columns) {
		this();
		this.mappingDb = new MappingDb(obj);
		this.table = mappingDb.getTableName();
		this.columns.addAll(columns);
	}

	/**
	 * 更新字段
	 * 
	 *            字段集合，前提是new Update(Object obj)
	 * @return Update
	 * @Author 杨健/YangJian
	 * @Date 2015年11月26日 下午4:15:47
	 * @Version 1.0.0
	 */
	public Update sets(String... columns) {

		// 更新所有字段
		if (isEmpty(columns)) {
			List<String> columnList = this.mappingDb.getColumns();
			String idName = this.mappingDb.getIdName();
			for (String key : columnList) {
				if (key.equals(idName)) {
					eq(idName, this.mappingDb.getValue(key));
				} else {
					set(key, this.mappingDb.getValue(key));
				}
			}
		}
		// 更新指定字段
		else {
			for (String key : columns) {
				set(key, this.mappingDb.getValue(key));
			}
		}
		return this;
	}

	/**
	 * 更新字段
	 * 
	 * @param column
	 *            字段
	 * @param value
	 *            值
	 * @return Update
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:29:23
	 * @Version 1.0.0
	 */
	public Update set(String column, Object value) {
		columns.add(column);
		if (value != null && value instanceof Date) {
			Date date = (Date) value;
			value = new Timestamp(date.getTime());
		}
		parameters.add(value);
		return this;
	}

	/**
	 * 拼接SQL查询条件（and）
	 * 
	 *            保存参数值
	 * @param columnName
	 *            字段
	 * @param value
	 *            参数值
	 * @return Update
	 * @Author 杨健/YangJian
	 * @Date 2015年6月30日 上午11:46:48
	 * @Version 1.0.0
	 */
	public Update eq(String columnName, Object value) {

		if (isEmpty(value))
			return this;

		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" = ? ").toString());
		setParameters(value);
		return this;
	}

	private Update setParameters(Object value) {
		parameters.add(value);
		return this;
	}

	/** 不相等 */
	public Update notEq(String columnName, Object value) {
		if (isEmpty(value))
			return this;

		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" <> ? ").toString());
		setParameters(value);
		return this;
	}

	/**
	 * 
	 * in
	 * 
	 * @param columnName
	 * @param value
	 * @return Update
	 * @Author 杨健/YangJian
	 * @Date 2015年7月19日 下午2:52:52
	 * @Version 1.0.0
	 */
	public Update in(String columnName, Object value) {
		return in(columnName, value, " in ");
	}

	/**
	 * not in
	 * 
	 * @param columnName
	 *            属性名称
	 * @param value
	 *            值集合
	 * @return
	 */
	public Update notIn(String columnName, Object value) {
		if (isEmpty(value))
			return this;

		in(columnName, value, " not in ");
		return this;
	}

	private Update in(String columnName, Object value, String opt) {

		if (isEmpty(value) || "null".equals(value))
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

		conditions.add(
				new StringBuilder().append(" and ").append(columnName).append(opt).append("( ").append(Joiner.on(",").join(params)).append(" )").toString());
		return this;
	}

	/** 空 */
	public Update isNull(String columnName) {
		if (isEmpty(columnName))
			return this;

		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" is null ").toString());
		return this;
	}

	/** 非空 */
	public Update isNotNull(String columnName) {
		if (isEmpty(columnName))
			return this;

		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" is not null ").toString());
		return this;
	}

	public Update or(String columnName, Object value) {
		if (isEmpty(columnName))
			return this;
		if (isEmpty(value))
			return this;
		conditions.add(new StringBuilder().append(" or ").append(columnName).append(" = ? ").toString());
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
	public Update like(String columnName, String value) {
		if (isEmpty(value))
			return this;
		if (value.indexOf("%") < 0)
			value = StringUtils.join("%", value, "%");

		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" like ?").toString());
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
	public Update notLike(String columnName, String value) {
		if (isEmpty(value))
			return this;
		if (value.indexOf("%") < 0)
			value = StringUtils.join("%", value, "%");

		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" not like ?").toString());
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
	public Update between(String columnName, Object lo, Object go) {
		if (isEmpty(lo) && isEmpty(go)) {
			return this;
		}

		if (isNotEmpty(lo)) {
			if (lo instanceof Timestamp) {
				lo = ((Timestamp) lo).getTime();
			} else if (lo instanceof Date) {
				lo = new Timestamp(((Date) lo).getTime());
			}
		}

		if (isNotEmpty(go)) {
			if (go instanceof Timestamp) {
				go = ((Timestamp) go).getTime();
			} else if (go instanceof Date) {
				go = new Timestamp(((Date) go).getTime());
			}
		}

		if (isNotEmpty(lo) && isEmpty(go)) {
			conditions.add(new StringBuilder().append(" and ").append(columnName).append(" >= ? ").toString());
			setParameters(lo);
			return this;
		}

		if (isEmpty(lo) && isNotEmpty(go)) {
			conditions.add(new StringBuilder().append(" and ").append(columnName).append(" <= ? ").toString());
			setParameters(go);
			return this;
		}

		conditions.add(new StringBuilder().append(" between ? and ? ").toString());
		setParameters(lo);
		setParameters(go);
		return this;
	}

	public Update between(String columnName, Number lo, Number go) {
		if (isNotEmpty(lo))
			ge(columnName, lo);

		if (isNotEmpty(go))
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
	public Update le(String columnName, Number value) {
		if (isEmpty(value)) {
			return this;
		}
		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" <= ? ").toString());
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
	public Update lt(String columnName, Number value) {
		if (isEmpty(value)) {
			return this;
		}
		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" < ? ").toString());
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
	public Update ge(String columnName, Number value) {
		if (isEmpty(value)) {
			return this;
		}
		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" >= ? ").toString());
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
	public Update gt(String columnName, Number value) {
		if (isEmpty(value)) {
			return this;
		}
		conditions.add(new StringBuilder().append(" and ").append(columnName).append(" > ? ").toString());
		setParameters(value);
		return this;
	}

	/**
	 * 拼接SET及WHERE条件
	 * 
	 * @return void
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:27:39
	 * @Version 1.0.0
	 */
	private void terminate() {
		if (columns.isEmpty() || parameters.isEmpty())
			throw new IllegalArgumentException("Not contains SET statements!");

		if (!terminated) {
			sql.append(table).append(" set ");
			int size = columns.size();
			for (int i = 0; i < size; i++) {
				sql.append(columns.get(i)).append(" = ? ");
				if (i < size - 1) {
					sql.append(",");
				}
			}
			if (!conditions.isEmpty()) {
				sql.append(" where ");
				int i = 0;
				Iterator<String> conditionIter = conditions.iterator();
				while (conditionIter.hasNext()) {
					String condition = conditionIter.next();
					if (i == 0) {
						sql.append(StringUtils.replaceEach(condition, new String[] { "and", "or" }, new String[] { "", "" }));
					} else {
						sql.append(condition);
					}
					i++;
				}
			}

			terminated = true;
		}
	}

	/**
	 * sql
	 */
	@Override
	public String toString() {
		terminate();
		return sql.toString();
	}

	/**
	 * 更新操作
	 * 
	 * @param connection
	 *            数据库连接
	 * @throws SQLException
	 * @return int 影响条数
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:28:36
	 * @Version 1.0.0
	 */
	public int doUpdate(Connection connection) throws SQLException {
		log.debug("SQL={}", this.toString());
		log.debug("SQL param={}", parameters);
		int update = JdbcUtils.getRunner().update(connection, this.toString(), parameters.toArray());
		log.debug("SQL Value={}", update);
		return update;
	}
	
	public int doUpdate() throws SQLException {
		log.debug("SQL={}", this.toString());
		log.debug("SQL param={}", parameters);
		int update = JdbcUtils.getRunner().update(JdbcUtils.getConnection(), this.toString(), parameters.toArray());
		log.debug("SQL Value={}", update);
		return update;
	}

}
