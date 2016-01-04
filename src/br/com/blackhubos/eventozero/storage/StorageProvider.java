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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public final class StorageProvider extends Storage
{

	@Override
	public <T> T getPlayerData(final Module service, final String player, final String key)
	{

		return null;
	}

	@Override
	public void setPlayerData(final String player, final String key, final Object value)
	{
		final String table = Storage.Module.OTHER.getTable();

		try
		{
			final ResultSet exists = this.search("SELECT * FROM ? WHERE key = ?;", table, player.toLowerCase() + "." + key, key.toLowerCase());
			if (exists.next())
			{
				this.update("UPDATE ? SET value=? WHERE key=?;", table, value, player.toLowerCase() + "." + key);
			}
			else
			{
				// insert("INSERT INTO ? (key,valor) VALUES ();", table, key, value, player.toLowerCase());
			}
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void depositPlayerRankingPoints(final String player, final String evento, final String tipo, final long value)
	{
		final String table = Storage.Module.RANKING.getTable();
		long atual = this.getPlayerRankingPoints(player, evento, tipo);
		if ((atual + value) >= Long.MAX_VALUE)
		{
			atual -= ((atual + value) - Long.MAX_VALUE);
		}
		else
		{
			atual = atual + value;
		}

		this.update("UPDATE ? SET ?=? WHERE player=? AND evento=?;", table, tipo.toLowerCase(), atual, player.toLowerCase(), evento.toLowerCase());
	}

	@Override
	public void withdrawPlayerRankingPoints(final String player, final String evento, final String tipo, final long value)
	{
		final String table = Storage.Module.RANKING.getTable();
		long atual = this.getPlayerRankingPoints(player, evento, tipo);
		if ((atual - value) < 0)
		{
			atual = 0;
		}
		else
		{
			atual = atual - value;
		}

		this.update("UPDATE ? SET ?=? WHERE player=? AND evento=?;", table, tipo.toLowerCase(), atual, player.toLowerCase(), evento.toLowerCase());
	}

	@Override
	public long getPlayerRankingPoints(final String player, final String evento, final String tipo)
	{
		final String table = Storage.Module.RANKING.getTable();
		final ResultSet rs = this.search("SELECT * FROM ? WHERE jogador=? AND evento=? ORDER BY ?;", table, player.toLowerCase(), evento.toLowerCase(), tipo);

		try
		{
			return rs.getLong(tipo);
		}
		catch (final SQLException e)
		{
			return -1L;
		}
	}

	@Override
	public int update(final String sql, final Object... set)
	{
		int rs = 2;

		try
		{
			final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
			for (int i = 0; i < set.length; i++)
			{
				final Object value = set[i];
				if (value instanceof String)
				{
					ps.setString(i + 1, String.valueOf(value));
				}
				else if (value instanceof Long)
				{
					ps.setLong(i + 1, Long.parseLong(String.valueOf(value)));
				}
				else if (value instanceof Integer)
				{
					ps.setInt(i + 1, Integer.parseInt(String.valueOf(value)));
				}
				else if (value instanceof Date)
				{
					ps.setDate(i + 1, (Date) value);
				}
				else
				{
					ps.setObject(i + 1, value);
				}
			}

			rs = ps.executeUpdate();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		return rs;
	}

	@Override
	public ResultSet search(final String query, final Object... set)
	{
		ResultSet rs = null;

		try
		{
			final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query);
			for (int i = 0; i < set.length; i++)
			{
				final Object value = set[i];
				if (value instanceof String)
				{
					ps.setString(i + 1, String.valueOf(value));
				}
				else if (value instanceof Long)
				{
					ps.setLong(i + 1, Long.parseLong(String.valueOf(value)));
				}
				else if (value instanceof Integer)
				{
					ps.setInt(i + 1, Integer.parseInt(String.valueOf(value)));
				}
				else if (value instanceof Date)
				{
					ps.setDate(i + 1, (Date) value);
				}
				else
				{
					ps.setObject(i + 1, value);
				}
			}

			rs = ps.executeQuery();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		return rs;
	}

	@Override
	public Entry<Integer, Boolean> backupPlayer(final Player player, final String evento)
	{

		return null;
	}

	@Override
	public boolean hasBackup(final String player, final String evento)
	{

		return false;
	}

	@Override
	public boolean hasBackup(final int id)
	{

		return false;
	}

}
