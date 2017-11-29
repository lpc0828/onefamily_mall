package com.onefamily.common.dsutils;

import com.google.common.base.Preconditions;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceManager {

	private final static Logger log = LoggerFactory.getLogger(DataSourceManager.class);
	static private ConcurrentHashMap<String, DataSource> pools = new ConcurrentHashMap<>();
	static private ConcurrentHashMap<String, String> dbTypes = new ConcurrentHashMap<>();
	static private String catalina_base = System.getProperty("catalina.base");
	static private String log_file_name = "dbcp2_exception.log";

	static private String DefaultConnectionPoolName = null;

	//static private ConcurrentHashMap<String, Map<String, Object>> connInfoMap = new ConcurrentHashMap<>();
	// 定时记录线程日志
	/*
	 * private static Timer timer = new Timer(); static { timer.schedule(new
	 * TimerTask() { public void run() { Enumeration<String> enums =
	 * connInfoMap.keys();
	 * log.info("check, connInfoMap.size={}",connInfoMap.size()); while
	 * (enums.hasMoreElements()) { String time = enums.nextElement();
	 * Map<String, Object> tmpMap = connInfoMap.get(time); Connection conn =
	 * (Connection)tmpMap.get("CONN"); long ldt = (long)tmpMap.get("CURT"); int
	 * keepHoldTimeoutSec = (int)tmpMap.get("KEEP"); boolean isClosed = true;
	 * try { isClosed = conn.isClosed(); } catch (Throwable e) { log.error("",
	 * e); } if(!isClosed && (System.currentTimeMillis()-ldt >
	 * keepHoldTimeoutSec*1000)){
	 * log.error("time={},isClosed={}, connInfo={},isTimeout=true", isClosed,
	 * tmpMap); } if (isClosed) { connInfoMap.remove(time); } } } }, 1000L,
	 * 1000L); }
	 */

	/**
	 * 建构函数私有以防止其它对象创建本类实例
	 */
	private DataSourceManager() {
		initDataSource();
	}

	/**
	 * 返回唯一实例.如果是第一次调用此方法,则创建实例
	 *
	 * @return DBConnectionManager 唯一实例
	 */
	static public DataSourceManager getInstance() {
		return DataSourceManager2Holder.instance;
	}

	/** 该类的一个对象，整个系统公用这一个对象。 */
	private static class DataSourceManager2Holder {
		private static DataSourceManager instance = new DataSourceManager();
	}

	/**
	 * 根据指定属性创建连接池实例.
	 *
	 *  连接池属性
	 */
	private void initDataSource() {
		XMLConfiguration config;
		try {
			config = new XMLConfiguration("da2s.xml");
			config.setThrowExceptionOnMissing(false);
		} catch (org.apache.commons.configuration.ConfigurationException exc) {
			log.error("GlobalConfigurationException", exc);
			throw new RuntimeException(exc);
		}

		DefaultConnectionPoolName = config.getString("DefaultConnectionPool");
		// 该项未配置，则值为null
		log.debug("DefaultConnectionPoolName is " + DefaultConnectionPoolName + "...");
		List<?> poolList = config.getList("connectionPool.dbtype");
		String connPoolName = new String();
		String dbtype = new String();
		String driverClassName = new String();
		String url = new String();
		String username = new String();
		String password = new String();
		boolean defaultAutoCommit = false;
		boolean defaultReadOnly = false;
		int initialSize = 0;
		int maxActive = 0;
		int maxIdle = 0;
		int minIdle = 0;
		long maxWait = 0;
		String validationQuery = new String();
		boolean testOnBorrow = true;
		boolean removeAbandoned = true;
		int removeAbandonedTimeout = 0;
		boolean logAbandoned = true;
		long maxConnLifetimeMillis = 0;

		try {
			for (int i = 0, j = poolList.size(); i < j; i++) {
				connPoolName = config.getString("connectionPool(" + i + ")[@name]");
				dbtype = config.getString("connectionPool(" + i + ").dbtype");
				driverClassName = config.getString("connectionPool(" + i + ").driverClassName");
				url = config.getString("connectionPool(" + i + ").url");
				username = config.getString("connectionPool(" + i + ").username");
				password = config.getString("connectionPool(" + i + ").password");
				defaultAutoCommit = config.getBoolean("connectionPool(" + i + ").datasourceProperty.defaultAutoCommit", false);
				defaultReadOnly = config.getBoolean("connectionPool(" + i + ").datasourceProperty.defaultReadOnly", false);
				initialSize = config.getInt("connectionPool(" + i + ").datasourceProperty.initialSize", 3);
				maxActive = config.getInt("connectionPool(" + i + ").datasourceProperty.maxActive", 50);
				maxIdle = config.getInt("connectionPool(" + i + ").datasourceProperty.maxIdle", 20);
				minIdle = config.getInt("connectionPool(" + i + ").datasourceProperty.minIdle", 5);
				maxWait = config.getLong("connectionPool(" + i + ").datasourceProperty.maxWait", 3000);
				maxConnLifetimeMillis = config.getLong("connectionPool(" + i + ").datasourceProperty.maxLifetime", 600000);// 10分钟
				validationQuery = config.getString("connectionPool(" + i + ").datasourceProperty.validationQuery", "select 1");
				testOnBorrow = config.getBoolean("connectionPool(" + i + ").datasourceProperty.testOnBorrow", true);
				removeAbandoned = config.getBoolean("connectionPool(" + i + ").datasourceProperty.removeAbandoned", true);
				removeAbandonedTimeout = config.getInt("connectionPool(" + i + ").datasourceProperty.removeAbandonedTimeout", 180);
				logAbandoned = config.getBoolean("connectionPool(" + i + ").datasourceProperty.logAbandoned", true);

				BasicDataSource bds2 = new BasicDataSource();
				bds2.setDriverClassName(driverClassName);
				bds2.setUrl(url);
				bds2.setUsername(username);
				bds2.setPassword(password);
				bds2.setDefaultAutoCommit(defaultAutoCommit);
				bds2.setDefaultReadOnly(defaultReadOnly);
				bds2.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				// 初始化连接数
				bds2.setInitialSize(initialSize);
				// 最小空闲连接
				bds2.setMinIdle(minIdle);
				// 最大空闲连接
				bds2.setMaxIdle(maxIdle);
				// 超时回收时间(以毫秒为单位)
				bds2.setMaxWaitMillis(maxWait);
				// 最大连接数
				bds2.setMaxTotal(maxActive);
				bds2.setTestOnBorrow(testOnBorrow);
				bds2.setValidationQuery(validationQuery);
				// 一个连接的最大存活毫秒数。如果超过这个时间，则连接在下次激活、钝化、校验时都将会失败。如果设置为0或小于0的值，则连接的存活时间是无限的。
				bds2.setMaxConnLifetimeMillis(maxConnLifetimeMillis);

				// 空闲对象驱逐线程运行时的休眠毫秒数，如果设置为非正数，则不运行空闲对象驱逐线程。
				long timeBetweenEvictionRunsMillis = 1000;
				bds2.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

				// 超时取回
				bds2.setLogAbandoned(logAbandoned);
				bds2.setAbandonedUsageTracking(logAbandoned);
				bds2.setRemoveAbandonedOnMaintenance(removeAbandoned);
				bds2.setRemoveAbandonedOnBorrow(removeAbandoned);
				bds2.setRemoveAbandonedTimeout(removeAbandonedTimeout);
				/*
				PrintWriter pw = null;
				try {
					if (catalina_base == null || catalina_base.trim().isEmpty()) {
						pw = new PrintWriter(new OutputStreamWriter(System.err));
					} else {
						String fileNameStr = catalina_base +"/logs/" + log_file_name;
						log.debug("PoolName:{}, AbandonedLogWriteFile:{}",connPoolName, fileNameStr);
						File tmpFile = new File(fileNameStr);
						if (!tmpFile.exists()) {
							tmpFile.createNewFile();
						}
						pw = new PrintWriter(new FileWriter(tmpFile));
					}
				} catch (Throwable e) {
					log.error("AbandonedLogWriter Create Error, ", e);
				}
				if (pw != null) {
					PrintWriter writer = bds2.getAbandonedLogWriter();
					if (writer != null){
						writer.close();
					}
					bds2.setAbandonedLogWriter(pw);
				}
				*/
				pools.put(connPoolName, bds2);
				dbTypes.put(connPoolName, dbtype);
				log.debug("Init DataSource " + connPoolName + "...");

			}
		} catch (Exception e) {
			log.error("Init DataSource " + connPoolName + "...ERROR", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 动态添加连接池
	 */
	public boolean addDataSource(String key, DataSource datasource) {
		pools.put(key, datasource);
		return true;
	}
	/**
	 * 动态删除连接池
	 */
	public void removeDataSource(String key) {
		if (key == null)
			return;
		BasicDataSource cds = (BasicDataSource) pools.remove(key);
		try {
			cds.close();
		} catch (SQLException e) {
			log.error("Close DS Error key={}", key, e);
		}
		cds = null;
	}
	/**
	 * 获取一个默认的可用连接.
	 *
	 * @return DataSource
	 */
	public DataSource getDataSource() {
		if (DefaultConnectionPoolName == null || DefaultConnectionPoolName.trim().isEmpty())
			return null;
		else
			return (DataSource) pools.get(DefaultConnectionPoolName);
	}

	/**
	 * 获取一个可用连接.
	 *
	 * @param name
	 *            连接池名字
	 * @return DataSource
	 */
	public DataSource getDataSource(String name) {
		return (DataSource) pools.get(name);
	}

	/**
	 * close all connection. <br>
	 * 关闭所有闲置连接.
	 */

	public synchronized void shutdown() {
		Enumeration<String> allkeys = pools.keys();

		while (allkeys.hasMoreElements()) {
			String poolName = (String) allkeys.nextElement();
			log.warn("DataSourceManager shutdown pool[{}]...", poolName);
			BasicDataSource cpds = (BasicDataSource) pools.remove(poolName);
			try {
				cpds.close();
			} catch (SQLException e) {
				log.error("Close DS Error key={}", poolName, e);
			}
			cpds = null;
			dbTypes.remove(poolName);
		}
	}

	public String getDefaultDataSourceName() {
		return DefaultConnectionPoolName;
	}

	public String getDBType(String name) {
		return (String) dbTypes.get(name);
	}
	public Set<String> getPoolNames() {
		return pools.keySet();
	}

	/**
	 * 获取一个默认的可用连接.
	 *
	 * @return DataSource
	 */
	public Connection getConnection() {
		return getConnection(DefaultConnectionPoolName);
	}

	/**
	 * 获取一个可用连接.
	 *
	 * @param name
	 *            连接池名字
	 * @return DataSource
	 */
	public Connection getConnection(String name) {
		// Objects.requireNonNull(name, "PoolName should not be null");
		Preconditions.checkNotNull(name, "PoolName should not be null");
		Preconditions.checkArgument(!name.trim().isEmpty(), "PoolName should not be empty");

		BasicDataSource thisDS = (BasicDataSource)pools.get(name);
		//String dbType = dbTypes.get(name);
		Preconditions.checkState(thisDS != null, "DataSource " + name + " is null");
		Preconditions.checkState(!thisDS.isClosed(), "DataSource " + name + " has closed");
		try {
			Connection conn = thisDS.getConnection();
			//setCallerInfo(conn);
			return conn;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void setCallerInfo(Connection conn) {
		String myName = this.getClass().getName();
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		for (StackTraceElement ste : stack) {
			if (!myName.equals(ste.getClassName())) {
				try {
					conn.setClientInfo("UUID", UUID.randomUUID().toString());
					conn.setClientInfo("INVOKER", ste.getClassName());
					conn.setClientInfo("METHOD", ste.getMethodName());
					conn.setClientInfo("LINE", "" + ste.getLineNumber());
				} catch (SQLClientInfoException e) {
					log.error("",e);
				}
				break;
			}
		}
	}

	/**
	 * 获取直接的数据库conn
	 * 
	 * @param driverClassName
	 * @param dbUrl
	 * @param userName
	 * @param password
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getDirectJDBCConnection(String driverClassName, String dbUrl, String userName, String password) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		Class.forName(driverClassName).newInstance();
		Connection conn = DriverManager.getConnection(dbUrl, userName, password);
		return conn;
	}

}
