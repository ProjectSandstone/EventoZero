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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

public final class DatabaseConnection
{

	protected static Vector<ConnectionSide> connections = new Vector<ConnectionSide>(12);
	protected static ConnectionLost connectionChecker = null;
	protected static String hosting = "";
	protected static String user = "";
	protected static String password = "";
	protected static long timeoutMillis = 45000;

	public DatabaseConnection(final String hosting, final String root, final String password)
	{
		DatabaseConnection.user = root;
		DatabaseConnection.password = password;
		DatabaseConnection.hosting = hosting;
		DatabaseConnection.connectionChecker = new ConnectionLost();
		DatabaseConnection.connectionChecker.start();
	}

	/**
	 *
	 * @return Retorna uma conexão disponível do pool.
	 * @throws SQLException caso haja problemas de conexão
	 */
	protected static synchronized Connection getConnection() throws SQLException
	{
		ConnectionSide conn = null;
		for (int i = 0; i < DatabaseConnection.connections.size(); i++)
		{
			conn = DatabaseConnection.connections.get(i);
			if (conn.riuinr())
			{
				if (conn.isValid())
				{
					return conn;
				}
				else
				{
					DatabaseConnection.connections.remove(conn);
					conn.terminate();
				}
			}
		}

		try
		{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}
		final Connection c = DriverManager.getConnection(DatabaseConnection.hosting, DatabaseConnection.user, DatabaseConnection.password);
		conn = new ConnectionSide(c);
		conn.riuinr();
		if (!conn.isValid())
		{
			conn.terminate();
			throw new SQLException("Failed to validate a brand new connection");
		}

		DatabaseConnection.connections.add(conn);
		return conn;
	}

	/**
	 * Verifica validade das conexões e remove caso necessário.
	 */
	protected static synchronized void check()
	{
		final long timeoutResult = System.currentTimeMillis() - DatabaseConnection.timeoutMillis;
		for (final ConnectionSide conn : DatabaseConnection.connections)
		{
			if (conn.inUse() && (timeoutResult > conn.getLastUse()) && !conn.isValid())
			{
				DatabaseConnection.connections.remove(conn);
			}
		}
	}

	/**
	 * Fecha e remove todas as conexões do pool.
	 */
	protected static synchronized void close()
	{
		final Enumeration<ConnectionSide> conns = DatabaseConnection.connections.elements();
		while (conns.hasMoreElements())
		{
			final ConnectionSide conn = conns.nextElement();
			DatabaseConnection.connections.remove(conn);
			conn.terminate();
		}
	}

}
