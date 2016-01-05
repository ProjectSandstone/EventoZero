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
package br.com.blackhubos.eventozero.ranking;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.material.Sign;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.factory.Event;

public final class BlockStructure
{

	private final Player player;
	private final Block bloco;
	private final int tipo;
	private final int top;
	private final String name;
	private final String evento;

	public BlockStructure(final Player player, final Block block, final String name, final String evento, final int tipo, final int top)
	{
		this.player = player;
		this.bloco = block;
		this.name = name;
		this.tipo = tipo;
		this.evento = evento;
		this.top = top;
	}

	public void update()
	{
		final Event e = EventoZero.getEventHandler().getEventByName(this.evento);
		final Location pos = this.bloco.getLocation();
		final Location pos2 = pos.clone().add(0, 1, 0);
		final Block bloco = pos2.getBlock();
		bloco.setType(Material.SIGN_POST);
		final org.bukkit.block.Sign placa = (org.bukkit.block.Sign) bloco.getState();
		final Sign data = new Sign(Material.SIGN_POST);
		data.setFacingDirection(this.getDirection(this.player).getOppositeFace());
		placa.setData(data);
		for (int i = 0; (i < EventoZero.getRankingConfiguration().getStringList("sign.output").size()) && (i < 3); i++)
		{
			final String line = EventoZero.getRankingConfiguration().getStringList("sign.output").get(i);
			placa.setLine(i, ChatColor.translateAlternateColorCodes('&', line.replace("{evento}", e.getEventDisplayName()).replace("{rank}", this.top + "").replace("{player}", this.name).replace("{tipo}", Ranking.byId(this.tipo))));
		}
		placa.update(true);
		bloco.setType(Material.SKULL);
		final Skull skull = (Skull) bloco;
		skull.setOwner(this.name);
		skull.setRotation(this.getDirection(this.player).getOppositeFace());
		skull.update(true);
	}

	public BlockFace getDirection(final Player player)
	{
		BlockFace dir = null;
		float y = player.getLocation().getYaw();

		if (y < 0)
		{
			y += 360;
		}

		y %= 360;

		final int i = (int) ((y + 8) / 22.5);
		if (i == 0)
		{
			dir = BlockFace.WEST;
		}
		else if (i == 1)
		{
			dir = BlockFace.WEST_NORTH_WEST;
		}
		else if (i == 2)
		{
			dir = BlockFace.NORTH_WEST;
		}
		else if (i == 3)
		{
			dir = BlockFace.NORTH_NORTH_WEST;
		}
		else if (i == 4)
		{
			dir = BlockFace.NORTH;
		}
		else if (i == 5)
		{
			dir = BlockFace.NORTH_NORTH_EAST;
		}
		else if (i == 6)
		{
			dir = BlockFace.NORTH_EAST;
		}
		else if (i == 7)
		{
			dir = BlockFace.EAST_NORTH_EAST;
		}
		else if (i == 8)
		{
			dir = BlockFace.EAST;
		}
		else if (i == 9)
		{
			dir = BlockFace.EAST_SOUTH_EAST;
		}
		else if (i == 10)
		{
			dir = BlockFace.SOUTH_EAST;
		}
		else if (i == 11)
		{
			dir = BlockFace.SOUTH_SOUTH_EAST;
		}
		else if (i == 12)
		{
			dir = BlockFace.SOUTH;
		}
		else if (i == 13)
		{
			dir = BlockFace.SOUTH_SOUTH_WEST;
		}
		else if (i == 14)
		{
			dir = BlockFace.SOUTH_WEST;
		}
		else if (i == 15)
		{
			dir = BlockFace.WEST_SOUTH_WEST;
		}
		else
		{
			dir = BlockFace.WEST;
		}
		return dir;
	}

}
