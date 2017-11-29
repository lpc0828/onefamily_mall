package com.onefamily.platform.sqls;

import com.google.common.collect.Lists;
import com.onefamily.platform.dataformat.Jackson2Helper;
import com.onefamily.platform.sqls.mapping.MappingDb;
import com.onefamily.platform.sqls.mapping.MappingField;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @Comment SQL INSERT
 * @Author 杨健/YangJian
 * @Date 2015年7月2日 下午2:32:08
 * @Version 1.0.0
 */
public class Insert {
	private String table;// 数据库表名
	private Class<?> clazz;// 对象
	private MappingDb mappingDb;
	private final List<MappingField> fields;// 对象属性集合
	private final List<String> columns;// 数据库字段集合
	private final List<Object[]> values;// 数据库字段对应值集合
	private boolean seqBefore = true;// PostgreSQL序列SEQ是否作为前缀
	private boolean terminated = false;
	private boolean isPrepareStatement = true;// 是否使用占位符
	private final StringBuilder sql;
	private final static ColumnListHandler<Long> rsh = new ColumnListHandler<Long>("id");
	private boolean isDubeg = true;
	private boolean useNewId = true;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Insert() {
		this.sql = new StringBuilder(" INSERT INTO ");
		this.fields = new LinkedList<MappingField>();
		this.columns = new LinkedList<String>();
		this.values = new LinkedList<Object[]>();
	}

	public Insert(String table) {
		this();
		this.table = table;
	}

	public Insert(Class<?> clazz) {
		this();
		this.clazz = clazz;
		this.mappingDb = new MappingDb(this.clazz);
		this.table = this.mappingDb.getTableName();
		this.fields.addAll(this.mappingDb.getFields());
		this.columns.addAll(this.mappingDb.getColumns());
	}

	/**
	 * @param clazz
	 *            没带值对象
	 * @param mapUnderscoreToCamelCase
	 *            是否将驼峰字段转换为下划线字段
	 */
	public Insert(Class<?> clazz, boolean mapUnderscoreToCamelCase) {
		this();
		this.clazz = clazz;
		this.mappingDb = new MappingDb(this.clazz);
		this.table = this.mappingDb.getTableName();
		this.fields.addAll(this.mappingDb.getFields());
		this.columns.addAll(this.mappingDb.getColumns());
	}

	/**
	 * 
	 * @param obj
	 *            已封装值对象
	 */
	public Insert(Object obj) {
		this();
		this.clazz = obj.getClass();
		this.mappingDb = new MappingDb(obj);
		this.table = this.mappingDb.getTableName();
		this.fields.addAll(this.mappingDb.getFields());
		this.columns.addAll(this.mappingDb.getColumns());
		this.values.add(this.mappingDb.getValues(useNewId));
	}

	/**
	 * 
	 * @param obj
	 *            已封装值对象
	 * @param mapUnderscoreToCamelCase
	 *            是否将驼峰字段转换为下划线字段
	 */
	public Insert(Object obj, boolean mapUnderscoreToCamelCase, boolean useNewId) {
		this();
		this.clazz = obj.getClass();
		this.mappingDb = new MappingDb(obj);
		this.table = this.mappingDb.getTableName();
		this.useNewId = useNewId;
		this.fields.addAll(this.mappingDb.getFields());
		this.columns.addAll(this.mappingDb.getColumns());
		this.values.add(this.mappingDb.getValues(useNewId));
	}

	public Insert insert(String table) {
		this.table = table;
		return this;
	}

	/**
	 * 设置序列seq前置还是后置，默认后置
	 * 
	 * @param seqBefore
	 * @return void
	 * @Author 杨健/YangJian
	 * @Date 2015年7月21日 上午11:38:17
	 * @Version 1.0.0
	 */
	public void setSeqBefore(boolean seqBefore) {
		this.seqBefore = seqBefore;
	}

	public String getSequenceName() {
		String sequenceName = StringUtils.join(this.table, "_id_seq");
		if (seqBefore) {
			sequenceName = StringUtils.join("seq_", this.table, "_id");
		}
		return sequenceName.toUpperCase();
	}

	/**
	 * 拼接sql片段
	 * 
	 * @param segment
	 * @return Insert
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:37:30
	 * @Version 1.0.0
	 */
	public Insert append(String segment) {
		sql.append(segment);
		return this;
	}

	public Insert table(String table) {
		this.table = table;
		return this;
	}

	/**
	 * 字段集合 需与values对应
	 * 
	 * @param columns
	 * @return Insert
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:38:05
	 * @Version 1.0.0
	 */
	public Insert columns(String... columns) {
		Collections.addAll(this.columns, columns);
		return this;
	}

	/**
	 * 值集合 需与columns对应
	 * 
	 * @param values
	 * @return Insert
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:38:44
	 * @Version 1.0.0
	 */
	public Insert values(Object... values) {
		this.values.add(values);
		return this;
	}

	/**
	 * 默认生成带占位符SQL
	 */
	public String toString() {
		toString(true);
		return sql.toString();
	}

	/**
	 * 生成SQL
	 * 
	 * @param isPrepareStatement
	 *            是否使用占位符
	 * @return String SQL
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:39:34
	 * @Version 1.0.0
	 */
	public String toString(boolean isPrepareStatement) {
		this.isPrepareStatement = isPrepareStatement;
		if (isPrepareStatement) {
			terminatePrepareStatement();
		} else {
			terminate();
		}
		return sql.toString();
	}

	/**
	 * 带占位符SQL
	 * 
	 * @return void
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:40:26
	 * @Version 1.0.0
	 */
	private void terminatePrepareStatement() {
		if (columns.isEmpty())
			throw new RuntimeException("No columns informed!");
		if (values.isEmpty())
			throw new RuntimeException("No values informed!");

		for (Object[] valueSet : values) {
			if (valueSet.length != columns.size()) {
				throw new RuntimeException("Value size different from column size!");
			}
		}

		if (!terminated) {
			this.append(table).append(" ( ").append(StringUtils.join(columns, ", ")).append(" )").append(" VALUES (");
			for (MappingField field : fields) {
				if (useNewId){
					if (field.isPrimaryKey() || "id".equalsIgnoreCase(field.getKeyName())) {
						this.append(StringUtils.join(" nextval ('",
								field.getSequenceName() == null ? this.getSequenceName() : field.getSequenceName(),
								"'), "));
					} else {
						this.append(StringUtils.join("?", ", "));
					}
				}else{
					this.append(StringUtils.join("?", ", "));
				}
			}
			sql.deleteCharAt(sql.length() - 2);
			sql.append(")");
		}
	}

	/**
	 * 带值SQL
	 * 
	 * @return void
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:40:39
	 * @Version 1.0.0
	 */
	private void terminate() {
		if (columns.isEmpty())
			throw new RuntimeException("No columns informed!");
		if (values.isEmpty())
			throw new RuntimeException("No values informed!");

		for (Object[] valueSet : values) {
			if (valueSet.length != columns.size()) {
				throw new RuntimeException("Value size different from column size!");
			}
		}

		if (!terminated) {
			this.append(table).append(" ( ").append(StringUtils.join(columns, ", ")).append(" )").append("VALUES ")
					.append(StringUtils.join(getSqlValues(), ", "));
		}
	}

	/**
	 * 获取字段集合
	 * 
	 * @return Object[]
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:41:03
	 * @Version 1.0.0
	 */
	public Object[] getColumns() {
		Object[] result = new Object[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return columns.toArray();
	}

	/**
	 * 获取值集合
	 * 
	 * @return Object[]
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:41:19
	 * @Version 1.0.0
	 */
	public Object[] getValues() {
		List<Object> v = Lists.newArrayList();
		for (Object[] v2 : values) {
			for (Object value : v2) {
				if (isPrepareStatement && value != null && value.toString().startsWith("nextval")) {
					continue;
				} else if (value != null && value instanceof Date) {
					Date date = (Date) value;
					v.add(new Timestamp(date.getTime()));
				} else {
					v.add(value);
				}
			}
		}
		return v.toArray();
	}

	private String[] getSqlValues() {
		String[] result = new String[values.size()];
		for (int i = 0; i < result.length; i++) {
			Object[] objs = values.get(i);
			result[i] = toValue(objs);
		}
		return result;
	}

	private String toValue(Object[] objs) {
		String[] result = new String[objs.length];

		for (int i = 0; i < result.length; i++) {
			if (objs[i] instanceof String) {
				if (objs[i].toString().startsWith("nextval")) {
					result[i] = objs[i].toString();
				} else {
					result[i] = StringUtils.join("'", objs[i].toString(), "'");
				}
			} else if (objs[i] instanceof Date) {
				Date date = (Date) objs[i];
				result[i] = "'" + new Timestamp(date.getTime()) + "'";
			} else {
				result[i] = objs[i] == null ? "null" : objs[i].toString();
			}
		}
		return "(" + StringUtils.join(result, ", ") + ")";
	}

	/**
	 * 保存并返回ID
	 * 
	 * @param connection
	 * @throws SQLException
	 * @return List<Long> 默认返回ID
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:43:19
	 * @Version 1.0.0
	 */
	public List<Long> insert(Connection connection) throws SQLException {
		List<Long> result = new QueryRunner().insert(connection, this.toString(), rsh, this.getValues());
		debug(result, null);
		return result;
	}

	/**
	 * 保存并返回保存并返回
	 * 
	 * @param connection
	 * @param columnlisthandler
	 *            指定字段集合
	 * @throws SQLException
	 * @return List<T>
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:44:31
	 * @Version 1.0.0
	 */
	public <T> List<T> insert(Connection connection, ColumnListHandler<T> columnlisthandler) throws SQLException {
		List<T> result = new QueryRunner().insert(connection, this.toString(), columnlisthandler,
				this.getValues());
		debug(result, null);
		return result;
	}

	/**
	 * 批量保存并返回ID集合
	 * 
	 * @param connection
	 * @param sql
	 * @param batchParams
	 *            参数
	 * @throws SQLException
	 * @return List<Long>
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:46:26
	 * @Version 1.0.0
	 */
	public List<Long> insertBatch(Connection connection, String sql, Object[][] batchParams) throws SQLException {
		List<Long> result = new QueryRunner().insertBatch(connection, sql, rsh, batchParams);
		debug(result, sql);
		return result;
	}

	/**
	 * 
	 * @param connection
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @return List<Long>
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:49:37
	 * @Version 1.0.0
	 */
	public List<Long> insert(Connection connection, String sql) throws SQLException {
		List<Long> result = new QueryRunner().insert(connection, sql, rsh, this.getValues());
		debug(result, sql);
		return result;
	}

	/**
	 *
	 * @param connection
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @return List<Long>
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:49:37
	 * @Version 1.0.0
	 */
	public List<Long> insertNoValues(Connection connection, String sql) throws SQLException {
		List<Long> result = new QueryRunner().insert(connection, sql, rsh);
		debug(result, sql);
		return result;
	}

	/**
	 * 保存并返回ID集合
	 * 
	 * @param connection
	 * @param sql
	 * @param values
	 * @throws SQLException
	 * @return List<Long>
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:50:13
	 * @Version 1.0.0
	 */
	public List<Long> insert(Connection connection, String sql, Object[] values) throws SQLException {
		List<Long> result = new QueryRunner().insert(connection, sql, rsh, values);
		debug(result, sql);
		return result;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * 是否打印日志
	 * 
	 * @param isDubeg
	 *            true 打印
	 * @return void
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 上午11:50:25
	 * @Version 1.0.0
	 */
	public void setDubeg(boolean isDubeg) {
		this.isDubeg = isDubeg;
	}

	private void debug(Object result, String sql) {
		if (!isDubeg) {
			return;
		}
		if (StringUtils.isEmpty(sql)) {
			log.info("SQL={}", this.toString());
		} else {
			log.info("SQL={}", sql);
		}
		log.info("SQL Parameters={}", this.getValues());
		log.info("SQL Value={}", Jackson2Helper.toJsonString(result));
	}

	public boolean isPrepareStatement() {
		return isPrepareStatement;
	}

	public void setPrepareStatement(boolean prepareStatement) {
		isPrepareStatement = prepareStatement;
	}
}