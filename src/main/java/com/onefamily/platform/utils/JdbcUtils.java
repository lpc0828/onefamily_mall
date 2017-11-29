package com.onefamily.platform.utils;

import com.onefamily.common.dsutils.DataSourceManager;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcUtils {

	// 使用ThreadLocal存储当前线程中的Connection对象
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	/**
	 * 获取读数据源
	 * @return
	 * @throws SQLException
	 */
	public static DataSource getReadDataSource(){
		return DataSourceManager.getInstance().getDataSource("5000");
	}
	/**
	 * 获取写数据源
	 * @return
	 */
	public static DataSource getWriteDataSource(){
		return DataSourceManager.getInstance().getDataSource("3000");
	}

	public static Connection getConnection() throws SQLException {
		// 从当前线程中获取Connection
		Connection conn = threadLocal.get();
		if (conn == null) {
			throw new SQLException("no connection init");
		}
		return conn;
	}
	
	public static Connection getConnection(boolean isCreate) throws SQLException {
		// 从当前线程中获取Connection
		Connection conn = threadLocal.get();
		if (conn == null&&isCreate) {
			 loadReadConnection();
		}
		return getConnection();
	}

	/**
	 * @Method: startTransaction
	 * @Description: 开启事务
	 *
	 */
	public static void loadReadConnection() {
		try {
			Connection conn = threadLocal.get();
			if (conn == null) {
				conn = getReadDataSource().getConnection();
				// 把 conn绑定到当前线程上
				threadLocal.set(conn);
			}
			// 开启事务
			conn.setAutoCommit(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @Method: startTransaction
	 * @Description: 开启事务
	 *
	 */
	public static void startTransaction() {
		long t = System.currentTimeMillis();
		try {
			Connection conn = threadLocal.get();
			if (conn == null) {
				conn = getWriteDataSource().getConnection();
				// 把 conn绑定到当前线程上
				threadLocal.set(conn);
			}
			// 开启事务
			conn.setAutoCommit(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: rollback
	 * @Description:回滚事务
	 *
	 */
	public static void rollback() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				// 回滚事务
				conn.rollback();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: commit
	 * @Description:提交事务
	 *
	 */
	public static void commit() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				conn.commit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: close
	 * @Description:关闭数据库连接(注意，并不是真的关闭，而是把连接还给数据库连接池)
	 *
	 */
	public static void close() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				conn.close();
				// 解除当前线程上绑定conn
				threadLocal.remove();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isClosed(){
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public static Integer getTableNum(Long id){
       return id.hashCode() % 256;
    }
	
	public static QueryRunner getRunner() {
		return new QueryRunner();
	}
}
