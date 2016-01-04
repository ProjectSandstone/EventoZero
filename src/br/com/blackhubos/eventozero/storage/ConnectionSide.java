/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright © 2016 BlackHub OS and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package br.com.blackhubos.eventozero.storage;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class ConnectionSide implements Connection
{

	private final Connection jdbc;
	private boolean activated;
	private long currentStartedTime;

	public ConnectionSide(final Connection jdbconnection)
	{
		this.jdbc = jdbconnection;
		this.activated = false;
		this.currentStartedTime = 0;
	}

	public void terminate()
	{
		try
		{
			this.jdbc.close();
		}
		catch (final SQLException ex)
		{
		}
	}

	/**
	 * RIUINR - Register In Use If Not Registered
	 *
	 * @return retorna false se já estava em uso, e true se foi ativado agora.
	 */
	public synchronized boolean riuinr()
	{
		if (this.activated)
		{
			return false;
		}
		else
		{
			this.activated = true;
			this.currentStartedTime = System.currentTimeMillis();
			return true;
		}
	}

	public boolean inUse()
	{
		return this.activated;
	}

	public long getLastUse()
	{
		return this.currentStartedTime;
	}

	@Override
	public void close()
	{
		this.activated = false;
		try
		{
			if (!this.jdbc.getAutoCommit())
			{
				this.jdbc.setAutoCommit(true);
			}
		}
		catch (final SQLException ex)
		{
			DatabaseConnection.connections.remove(this.jdbc);
			this.terminate();
		}
	}

	@Override
	public PreparedStatement prepareStatement(final String sql) throws SQLException
	{
		return this.jdbc.prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(final String sql) throws SQLException
	{
		return this.jdbc.prepareCall(sql);
	}

	@Override
	public Statement createStatement() throws SQLException
	{
		return this.jdbc.createStatement();
	}

	@Override
	public String nativeSQL(final String sql) throws SQLException
	{
		return this.jdbc.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(final boolean autoCommit) throws SQLException
	{
		this.jdbc.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException
	{
		return this.jdbc.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException
	{
		this.jdbc.commit();
	}

	@Override
	public void rollback() throws SQLException
	{
		this.jdbc.rollback();
	}

	@Override
	public boolean isClosed() throws SQLException
	{
		return this.jdbc.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException
	{
		return this.jdbc.getMetaData();
	}

	@Override
	public void setReadOnly(final boolean readOnly) throws SQLException
	{
		this.jdbc.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException
	{
		return this.jdbc.isReadOnly();
	}

	@Override
	public void setCatalog(final String catalog) throws SQLException
	{
		this.jdbc.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException
	{
		return this.jdbc.getCatalog();
	}

	@Override
	public void setTransactionIsolation(final int level) throws SQLException
	{
		this.jdbc.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException
	{
		return this.jdbc.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException
	{
		return this.jdbc.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException
	{
		this.jdbc.clearWarnings();
	}

	@Override
	public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException
	{
		return this.jdbc.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException
	{
		return this.jdbc.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException
	{
		return this.jdbc.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException
	{
		return this.jdbc.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException
	{
		return this.jdbc.createSQLXML();
	}

	@Override
	public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException
	{
		return this.jdbc.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException
	{
		return this.jdbc.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException
	{
		return this.jdbc.createStruct(typeName, attributes);
	}

	@Override
	public Properties getClientInfo() throws SQLException
	{
		return this.jdbc.getClientInfo();
	}

	@Override
	public String getClientInfo(final String name) throws SQLException
	{
		return this.jdbc.getClientInfo(name);
	}

	@Override
	public int getHoldability() throws SQLException
	{
		return this.jdbc.getHoldability();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		return this.jdbc.getTypeMap();
	}

	public boolean isValid()
	{
		try
		{
			return this.jdbc.isValid(1);
		}
		catch (final SQLException ex)
		{
			return false;
		}
	}

	@Override
	public boolean isValid(final int timeout) throws SQLException
	{
		return this.jdbc.isValid(timeout);
	}

	@Override
	public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException
	{
		return this.jdbc.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException
	{
		return this.jdbc.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException
	{
		return this.jdbc.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException
	{
		return this.jdbc.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException
	{
		return this.jdbc.prepareStatement(sql, columnNames);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException
	{
		return this.jdbc.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException
	{
		return this.jdbc.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public void releaseSavepoint(final Savepoint savepoint) throws SQLException
	{
		this.jdbc.releaseSavepoint(savepoint);
	}

	@Override
	public void rollback(final Savepoint savepoint) throws SQLException
	{
		this.jdbc.rollback(savepoint);
	}

	@Override
	public void setClientInfo(final Properties properties) throws SQLClientInfoException
	{
		this.jdbc.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(final String name, final String value) throws SQLClientInfoException
	{
		this.jdbc.setClientInfo(name, value);
	}

	@Override
	public void setHoldability(final int holdability) throws SQLException
	{
		this.jdbc.setHoldability(holdability);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException
	{
		return this.jdbc.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(final String name) throws SQLException
	{
		return this.jdbc.setSavepoint(name);
	}

	@Override
	public void setTypeMap(final Map<String, Class<?>> map) throws SQLException
	{
		this.jdbc.setTypeMap(map);
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException
	{
		return this.jdbc.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException
	{
		return this.jdbc.unwrap(iface);
	}

	@Override
	public void setSchema(final String schema) throws SQLException
	{
	}

	@Override
	public String getSchema() throws SQLException
	{
		return null;
	}

	@Override
	public void abort(final Executor executor) throws SQLException
	{
	}

	@Override
	public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException
	{
	}

	@Override
	public int getNetworkTimeout() throws SQLException
	{
		return 0;
	}
}
