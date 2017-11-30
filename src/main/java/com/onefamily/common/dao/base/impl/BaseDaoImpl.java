package com.onefamily.common.dao.base.impl;

import com.google.common.collect.Lists;
import com.onefamily.common.dao.base.IBaseDao;
import com.onefamily.platform.dataformat.Jackson2Helper;
import com.onefamily.platform.sqls.Insert;
import com.onefamily.platform.sqls.Query;
import com.onefamily.platform.sqls.Update;
import com.onefamily.platform.sqls.mapping.MappingDb;
import com.onefamily.platform.utils.JdbcUtils;
import com.onefamily.platform.utils.PaginationHelper;
import com.onefamily.platform.utils.PaginationQC;
import com.onefamily.platform.utils.PaginationSupport;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.AbstractListHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import static com.onefamily.platform.utils.StringTool.isEmpty;

/**
 * 增删改查
 *
 * @Author 杨健/YangJian
 * @Date 2015年12月3日 下午9:02:16
 */
public class BaseDaoImpl implements IBaseDao {

	private final static int batchSize = 100;// 每次批量操作数

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	public String toNumber(String columnName) {
		// return columnName;
		// return StringUtils.join("to_number(", columnName, ",'999999999999G999D9S')");
		return StringUtils.join(" cast(", columnName, " as numeric)");
	}

	@Override
	public Long insert(Object entities) throws SQLException {
		try {
			Insert insert = new Insert(entities);
			String sql = insert.toString();
			List<Long> ids = insert.insert(JdbcUtils.getConnection(), sql, insert.getValues());
			log.info("BaseDaoImpl-insert SQL:{},params:{},result:{}", sql, Jackson2Helper.toJsonString(entities), Jackson2Helper.toJsonString(ids));
			return ids.get(0);
		} catch (Exception e) {
			log.info("BaseDaoImpl-insert exception:{}", e);
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public List<Long> insertBatch(List<?> entities) throws SQLException {
		if (isEmpty(entities)) {
			log.info("BaseDaoImpl-insertBatch end-listEmpty...");
			return null;
		}

		try {
			Insert insert = new Insert(entities.get(0));
			String sql = insert.toString();

			log.debug("insertBatch-commonSQL={} ", sql);
			log.debug("insertBatch-AllParams={} ", Jackson2Helper.toJsonString(entities));

			List<Long> rets = Lists.newArrayList();

			// 批量插入
			int i = 1;
			List<Long> retBatchs = null;
			Object[][] batchParams = null;
			List<Object[]> list = Lists.newArrayList();
			for (Object entity : entities) {
				insert = new Insert(entity);
				list.add(insert.getValues());
				if (i % batchSize == 0) {
					batchParams = new Object[list.size()][];
					for (int j = 0; j < list.size(); j++) {
						batchParams[j] = list.get(j);
					}
					retBatchs = insert.insertBatch(JdbcUtils.getConnection(), sql, batchParams);
					log.debug("insertBatch-batchSize-SQL={} ", sql);
					log.debug("insertBatch-batchSize-params={} ", Jackson2Helper.toJsonString(batchParams));
					log.debug("insertBatch-batchSize-retBatchs={} ", Jackson2Helper.toJsonString(retBatchs));
					rets.addAll(retBatchs);
					list = Lists.newArrayList();
				}
				i++;
			}
			if (list.size() > 0) {
				batchParams = new Object[list.size()][];
				for (int j = 0; j < list.size(); j++) {
					batchParams[j] = list.get(j);
				}
				retBatchs = insert.insertBatch(JdbcUtils.getConnection(), sql, batchParams);
				log.debug("insertBatch-SQL={} ", sql);
				log.debug("insertBatch-params={} ", Jackson2Helper.toJsonString(batchParams));
				log.debug("insertBatch-retBatchs={} ", Jackson2Helper.toJsonString(retBatchs));
				rets.addAll(retBatchs);
			}
			log.info("BaseDaoImpl-insertBatch end-Rt...insertRt:{}", Jackson2Helper.toJsonString(rets));
			return rets;
		} catch (Exception e) {
			log.info("BaseDaoImpl-insertBatch end-exception:{}", e);
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public int update(Object entity, String... columns) throws SQLException {
		if (isEmpty(entity)) {
			return 0;
		}
		MappingDb mappingDb = new MappingDb(entity);
		if (isEmpty(mappingDb.getIdName()) || isEmpty(mappingDb.getIdValue())) {
			return 0;
		}

		Update update = new Update(entity);
		update.sets(columns);
		update.eq(mappingDb.getIdName(), mappingDb.getIdValue());
		return update.doUpdate(JdbcUtils.getConnection());
	}

	@Override
	public int delete(Class<?> clazz, List<Serializable> ids) throws SQLException {
		MappingDb mappingDb = new MappingDb(clazz);
		Query query = new Query(clazz);
		query.in(mappingDb.getIdName(), ids);
		return query.delete();
	}

	@Override
	public int delete(Query query) throws SQLException {
		return query.delete();
	}

	@Override
	public Long getCount(Class<?> clazz) throws SQLException {
		Query query = new Query(clazz);
		return query.getCount();
	}

	@Override
	public <E> E get(Class<?> clazz, Serializable id) throws SQLException {
		MappingDb mappingDb = new MappingDb(clazz);
		Query query = new Query(clazz);
		query.eq(mappingDb.getIdName(), id);
		return query.singleResult();
	}

	@Override
	public Long getCount(Query query) throws SQLException {
		return query.getCount();
	}

	@Override
	public <T> List<T> listColumn(Query query, String column) throws SQLException {
		return query.listColumn(column);
	}

	@Override
	public <E extends Serializable> List<E> query(Query query) throws SQLException {
		return query.list();
	}

	@Override
	public <E extends Serializable> List<E> queryAll(Class<?> clazz) throws SQLException {
		Query query = new Query(clazz);
		return query.list();
	}

	@Override
	public <E extends Serializable> List<E> query(Query query, int firstResult, int maxResults) throws SQLException {
		query.limit(firstResult, maxResults);
		return query.list();
	}

	@Override
	public <T> PaginationSupport<T> queryPage(Query query, int start, int limit) throws SQLException {

		log.debug("-----开始查询,页码: {}, 每页显示: {}----", start, limit);

		int count = query.getCount().intValue();

		log.debug("-----count:{}----", count);

		PaginationQC pageQC = new PaginationQC();
		pageQC.setStart(start);
		pageQC.setLimit(limit);

		PaginationSupport<T> page = PaginationHelper.getPageInfo(pageQC);
		page.setTotalCount(count);
		query.limit(page.getStartIndex(), page.getPageSize());
		page.setItems(query.list());

		return page;
	}

	@Override
	public <T> PaginationSupport<T> queryPage(Query query, Class<T> clazz, int start, int limit) throws SQLException {

		log.debug("-----开始查询,页码: {}, 每页显示: {}----", start, limit);

		int count = query.getCount().intValue();

		log.debug("-----count:{}----", count);

		PaginationQC pageQC = new PaginationQC();
		pageQC.setStart(start);
		pageQC.setLimit(limit);

		PaginationSupport<T> page = PaginationHelper.getPageInfo(pageQC);
		page.setTotalCount(count);
		query.limit(page.getStartIndex(), page.getPageSize());
		page.setItems(query.list(clazz));

		return page;
	}

	@Override
	public <T> PaginationSupport<T> queryPage(Query query, Class<T> clazz, PaginationQC pageQC) throws SQLException {

		log.debug("-----开始查询,页码: {}, 每页显示: {}----", pageQC.getStart(), pageQC.getLimit());

		int count = query.getCount().intValue();

		log.debug("-----count:{}----", count);

		PaginationSupport<T> page = PaginationHelper.getPageInfo(pageQC);
		query.limit(page.getStartIndex(), page.getPageSize());
		page.setTotalCount(count);
		page.setItems(query.list(clazz));

		return page;
	}

	@Override
	public <T> void queryPage(Query query, PaginationSupport<T> page) throws SQLException {

		log.debug("-----开始查询,页码: {}, 每页显示: {}----", page.getStartIndex(), page.getPageSize());

		int count = query.getCount().intValue();
		log.debug("-----count:{}----", count);

		page.setTotalCount(count);

		query.limit(page.getStartIndex(), page.getPageSize());
		page.setItems(query.list());
	}

	@Override
	public <T> void queryPage(Query query, Class<T> clazz, PaginationSupport<T> page) throws SQLException {

		log.debug("-----开始查询,页码: {}, 每页显示: {}----", page.getStartIndex(), page.getPageSize());

		int count = query.getCount().intValue();
		log.debug("-----count:{}----", count);

		page.setTotalCount(count);

		query.limit(page.getStartIndex(), page.getPageSize());
		page.setItems(query.list(clazz));
	}

	@Override
	public <T> List<T> queryByNativeSQL(final String sql, Class<T> mappedClass,
										final List<Serializable> params) throws SQLException{
		QueryRunner query = new QueryRunner();

		BeanProcessor bean = new GenerousBeanProcessor();
		RowProcessor rowProcessor = new BasicRowProcessor(bean);
		BeanListHandler<T> bh = new BeanListHandler<T>(mappedClass, rowProcessor);

		List<Serializable> queryParam = Lists.newArrayList();
		if (params != null){
			queryParam.addAll(params);
		}
		List<T> pos = query.query(JdbcUtils.getConnection(), sql, bh, queryParam.toArray());
		return pos;
	}

	@Override
	public <T> List<T> queryByNativeSQL(final String sql, AbstractListHandler<T> handler, final List<Serializable> params) throws SQLException {
		QueryRunner query = new QueryRunner();
		List<Serializable> queryParam = Lists.newArrayList();
		if (params != null){
			queryParam.addAll(params);
		}
		List<T> list = query.query(JdbcUtils.getConnection(), sql, handler, queryParam.toArray());
		return list;
	}

	@Override
	public List<Object[]> queryObjectArray(String sql,Object... params) throws SQLException {
		QueryRunner query = new QueryRunner();
		return query.query(JdbcUtils.getConnection(),sql, new ArrayListHandler(), params);
	}

	@Override
	public List<Object[]> queryObjectArray(String sql) throws SQLException {
		QueryRunner query = new QueryRunner();
		return query.query(JdbcUtils.getConnection(),sql, new ArrayListHandler());
	}

	@Override
	public int updateBySql(String sql, Object... params) throws SQLException{
		QueryRunner query = new QueryRunner();
		return query.update(JdbcUtils.getConnection(), sql.toString(), params);
	}

	@Override
	public int getCountNativeSQL(String sql, List<Serializable> params)
			throws SQLException {
		if (isEmpty(sql)) {
			return 0;
		}
		String sqlStr = sql.toString();
		if (!sql.toString().toLowerCase().contains("select count")) {
			sqlStr = StringUtils.join("select count(_t.*) from (", sql.toString(), ") as _t");
		}

		ScalarHandler<Long> handler = new ScalarHandler<Long>(1);
		Long count = JdbcUtils.getRunner().query(JdbcUtils.getConnection(), sqlStr, handler, params.toArray());
		return count.intValue();
	}
}
