package com.onefamily.common.dsutils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class WrappedDataSource implements DataSource {
	transient String da2sPoolName;

	public WrappedDataSource() {
	}

	public WrappedDataSource(String da2sPoolName) {
	}

	public String getDa2sPoolName() {
		return da2sPoolName;
	}

	public void setDa2sPoolName(String da2sPoolName) {
		this.da2sPoolName = da2sPoolName;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return DataSourceManager.getInstance().getDataSource(da2sPoolName).getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new SQLException("unimplemented");

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new SQLException("unimplemented");

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return DataSourceManager.getInstance().getDataSource(da2sPoolName).getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return DataSourceManager.getInstance().getDataSource(da2sPoolName).getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("unimplemented");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSourceManager.getInstance().getDataSource(da2sPoolName).isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DataSourceManager.getInstance().getDataSource(da2sPoolName).getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return DataSourceManager.getInstance().getDataSource(da2sPoolName).getConnection(username, password);
	}
	
	public void close(){
		DataSourceManager.getInstance().removeDataSource(da2sPoolName);
	}

}
