package com.onefamily.common.dao.base;

import com.onefamily.platform.sqls.Query;
import com.onefamily.platform.utils.PaginationQC;
import com.onefamily.platform.utils.PaginationSupport;
import org.apache.commons.dbutils.handlers.AbstractListHandler;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public interface IBaseDao {

	/**
	 * 批量保存
	 * 
	 * @param entities
	 * @throws SQLException
	 * @return List<Long>
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 下午12:04:24
	 * @Version 1.0.0
	 */
	List<Long> insertBatch(List<?> entities) throws SQLException;

	/**
	 * 保存
	 * 
	 * @param entities
	 * @throws SQLException
	 * @return Long
	 * @Author 杨健/YangJian
	 * @Date 2015年8月6日 下午12:04:46
	 * @Version 1.0.0
	 */
	Long insert(Object entities) throws SQLException;

	/**
	 * 更新记录
	 * 
	 * @param entity
	 * @param columns
	 *            指定字段
	 * @throws SQLException
	 * @return int
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午8:30:11
	 * @Version 1.0.0
	 */
	int update(Object entity, String... columns) throws SQLException;

	/**
	 * 根据ids删除数据
	 * 
	 * @param entity
	 *            删除实体类
	 * @param ids
	 *            删除条件
	 * @return
	 */
	int delete(Class<?> entity, List<Serializable> ids) throws SQLException;

	/**
	 * 删除数据
	 * 
	 * @param query
	 * @throws SQLException
	 * @return int
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午8:44:18
	 * @Version 1.0.0
	 */
	int delete(Query query) throws SQLException;

	/**
	 * 统计记录
	 * 
	 * @param clazz
	 *            统计条件
	 * @return Long
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:52:55
	 * @Version 1.0.0
	 */
	Long getCount(Class<?> clazz) throws SQLException;

	/**
	 * 根据id查询
	 * 
	 * @param clazz
	 * @param id
	 * @return E
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:52:41
	 * @Version 1.0.0
	 */
	<E> E get(Class<?> clazz, Serializable id) throws SQLException;

	/**
	 * 统计记录
	 * 
	 * @param query
	 *            统计条件
	 * @return Long
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:54:21
	 * @Version 1.0.0
	 */
	Long getCount(Query query) throws SQLException;

	/**
	 * 按指定字段返回List<String>集合
	 * 
	 * @param column
	 * @throws SQLException
	 * @return List<T>
	 * @Author 杨健/YangJian
	 * @Date 2016年9月9日 下午3:59:42
	 * @Version 1.0.0
	 */
	<T> List<T> listColumn(Query query, String column) throws SQLException;

	/**
	 * 根据query查找记录
	 * 
	 * @param query
	 *            查询条件
	 * @param query
	 * @return List<E>
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:54:28
	 * @Version 1.0.0
	 */
	<E extends Serializable> List<E> query(Query query) throws SQLException;

	/**
	 * 查询所有
	 * 
	 * @param clazz
	 *            实体类
	 * @return List<E>
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:53:40
	 * @Version 1.0.0
	 */
	<E extends Serializable> List<E> queryAll(Class<?> clazz) throws SQLException;

	/**
	 * 根据query查找记录
	 * 
	 * @param query
	 *            查询条件
	 * @param firstResult
	 *            起始行
	 * @param maxResults
	 *            结束行
	 * @return List<E>
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:53:28
	 * @Version 1.0.0
	 */
	<E extends Serializable> List<E> query(Query query, int firstResult, int maxResults) throws SQLException;

	/**
	 * 分页查询
	 * 
	 * @param <T>
	 * 
	 * @param query
	 *            查询条件
	 * @param start
	 *            页号
	 * @param limit
	 *            每页显示条数
	 * @return PaginationSupport<?>
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午7:54:14
	 * @Version 1.0.0
	 */
	<T> PaginationSupport<T> queryPage(Query query, int start, int limit) throws SQLException;

	<T> PaginationSupport<T> queryPage(Query query, Class<T> clazz, int start, int limit) throws SQLException;

	<T> PaginationSupport<T> queryPage(Query query, Class<T> clazz, PaginationQC pageQC) throws SQLException;

	/**
	 * 分页查询
	 * 
	 * @param <T>
	 * 
	 * @param query
	 *            查询条件
	 * @param page
	 *            分页数据及返回结果集
	 * @throws SQLException
	 * @return void
	 * @Author 杨健/YangJian
	 * @Date 2015年12月3日 下午8:58:24
	 * @Version 1.0.0
	 */
	<T> void queryPage(Query query, PaginationSupport<T> page) throws SQLException;

	<T> void queryPage(Query query, Class<T> clazz, PaginationSupport<T> page) throws SQLException;

	<T> List<T> queryByNativeSQL(final String sql, Class<T> mappedClass, final List<Serializable> params)
			throws SQLException;

	<T> List<T> queryByNativeSQL(final String sql, AbstractListHandler<T> handler, final List<Serializable> params) throws SQLException;

	List<Object[]> queryObjectArray(String sql, Object... params) throws SQLException;

	List<Object[]> queryObjectArray(String sql) throws SQLException;

	int updateBySql(String sql, Object... params) throws SQLException;

	int getCountNativeSQL(final String sql, final List<Serializable> params) throws SQLException;
}
