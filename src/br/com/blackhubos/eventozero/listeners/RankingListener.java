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
package br.com.blackhubos.eventozero.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.events.PlayerRankingUpdateEvent;
import br.com.blackhubos.eventozero.ranking.BlockStructure;
import br.com.blackhubos.eventozero.ranking.Ranking;
import br.com.blackhubos.eventozero.util.Framework;

// id | evento | cabeca
public final class RankingListener implements Listener
{

	@EventHandler(priority = EventPriority.MONITOR)
	private void onRankingUpdate(final PlayerRankingUpdateEvent event)
	{
		if (event.hasBlockUpdateEnabled())
		{
			new Thread()
			{
				@Override
				public void run()
				{
					final ResultSet ranking = EventoZero.getStorage().search("SELECT * FROM evento WHERE ? = ? ORDER BY ?", Ranking.VITORIAS.getTable(), event.getEvent().getEventName().toLowerCase(), Ranking.VITORIAS.getColuna());
					int id = 0;
					try
					{
						while (ranking.next()) // -- id | tipo | evento | index | mundo | localizacao
						{
							id++;
							final ResultSet signs = EventoZero.getStorage().search("SELECT * FROM ? WHERE evento = ? AND tipo = 1 AND ? = ?", Ranking.VITORIAS.getTable(), event.getEvent().getEventName().toLowerCase(), "index", id);
							while (signs.next())
							{
								final String pos = signs.getString(6);
								final Location loc = Framework.toLocation(pos);
								final BlockStructure block = new BlockStructure(null, loc.getBlock(), event.getPlayer().getName(), event.getEventName(), Ranking.VITORIAS.getId(), event.getPlayerRankingPosition());
								block.update();
							}
						}
					}
					catch (final SQLException e)
					{
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

}
