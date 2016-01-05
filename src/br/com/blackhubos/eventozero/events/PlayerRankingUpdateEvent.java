/**
 *
 * EventoZero - .
 * Copyright © 2016 BlackHub OS.
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
package br.com.blackhubos.eventozero.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.blackhubos.eventozero.ranking.Ranking;
import br.com.blackhubos.eventozero.ranking.RankingAction;

public final class PlayerRankingUpdateEvent extends Event implements Cancellable
{

	private final static HandlerList handler = new HandlerList();
	private final OfflinePlayer player;
	private final br.com.blackhubos.eventozero.factory.Event event;
	private final RankingAction action;
	private final Ranking rank;
	private boolean cancelled;
	private boolean updateBlocks;
	private long valor;

	public PlayerRankingUpdateEvent(final OfflinePlayer player, final Ranking rank, final long valor, final br.com.blackhubos.eventozero.factory.Event event, final RankingAction action, final boolean update)
	{
		this.player = player;
		this.event = event;
		this.action = action;
		this.updateBlocks = update;
		this.rank = rank;
		this.valor = valor;
	}

	public OfflinePlayer getPlayer()
	{
		return this.player;
	}

	public Ranking getRanking()
	{
		return this.rank;
	}

	public br.com.blackhubos.eventozero.factory.Event getEvent()
	{
		return this.event;
	}

	public RankingAction getAction()
	{
		return this.action;
	}

	public void setValue(final long newValue)
	{
		this.valor = newValue;
	}

	public long getValue()
	{
		return this.valor;
	}

	public void setBlockUpdateEnabled(final boolean value)
	{
		this.updateBlocks = value;
	}

	public boolean hasBlockUpdateEnabled()
	{
		return this.updateBlocks;
	}

	@Override
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	@Override
	public void setCancelled(final boolean cancel)
	{
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers()
	{
		return PlayerRankingUpdateEvent.handler;
	}

}
