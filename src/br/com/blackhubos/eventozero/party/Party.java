/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright � 2016 BlackHub OS and contributors.
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
package br.com.blackhubos.eventozero.party;

import java.util.Vector;

import org.bukkit.entity.Player;

public final class Party
{

	private final Vector<Player> joineds;
	private final Vector<Player> inviteds;
	private final int maxPlayers;

	public Party(final int maxPlayers)
	{
		this.joineds = new Vector<>();
		this.inviteds = new Vector<>();
		this.maxPlayers = maxPlayers;
	}

	public int getMaxPlayers()
	{
		return this.maxPlayers;
	}
	
	public Player getOwner()
	{
		return this.joineds.get(0);
	}

	public Vector<Player> getJoineds()
	{
		return this.joineds;
	}

	public Vector<Player> getInviteds()
	{
		return this.inviteds;
	}

	public Party playerJoin(final Player player)
	{
		this.joineds.add(player);
		return this;
	}

	public Party playerQuit(final Player player)
	{
		this.joineds.remove(player);
		return this;
	}

	public Party playerJoinInvited(final Player player)
	{
		this.inviteds.addElement(player);
		return this;
	}

	public Party playerQuitInvited(final Player player)
	{
		this.inviteds.remove(player);
		return this;
	}

}
