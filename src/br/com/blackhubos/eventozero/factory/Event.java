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

package br.com.blackhubos.eventozero.factory;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.NullArgumentException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.blackhubos.eventozero.party.Party;

/**
 * TODO: arrumar index do setSign (todos 0)
 * TODO: adicionar logs (getLoggerService()) nos backups e afins para deixar registrado
 * TODO: adicionar as mensagens e seus replaces aos respectivos voids necessários
 * TODO: documentar (javadoc) todos os métodos e construtores em Português BR.
 * Falta algo? documente aqui com um TODO: mensagem
 * TODO = To Do (a fazer)
 *
 */
public class Event
{

	private final String eventName;
	private final EventData eventData;

	private final Vector<Player> joineds;
	private final Vector<Player> spectators;
	private final Vector<Party> partys;

	private String eventDescription;
	private EventState eventoState;

	public Event(final String name)
	{
		this.eventName = name;
		this.joineds = new Vector<>();
		this.spectators = new Vector<>();
		this.partys = new Vector<>();
		this.eventData = new EventData();
	}

	public String getEventName()
	{
		return this.eventName;
	}

	public String getEventDescription()
	{
		return this.eventDescription;
	}

	/**
	 * O nome estava incorreto, estava geEventState() (faltava o t no get), renomeado por Atom
	 *
	 * @return
	 */
	public EventData getEventData()
	{
		return this.eventData;
	}

	/**
	 * O nome estava incorreto, estava geEventState() (faltava o t no get), renomeado por Atom
	 *
	 * @return
	 */
	public EventState getEventState()
	{
		return this.eventoState;
	}

	public Event updateDescription(final String desc)
	{
		this.eventDescription = desc;
		return this;
	}

	public Vector<Party> getPartys()
	{
		return this.partys;
	}

	public Vector<Player> getPlayers()
	{
		return this.joineds;
	}

	public Vector<Player> getSpectators()
	{
		return this.spectators;
	}

	public Event playerJoin(final Player player)
	{
		if ((player == null) || ((player != null) && !player.isOnline()))
		{
			throw new NullArgumentException("Player is null");
		}
		if (!this.hasPlayerJoined(player))
		{
			this.joineds.add(player);
			this.playerBackup(player);
			this.updateSigns();
		}
		return this;
	}

	public Event playerQuit(final Player player)
	{
		if ((player == null) || ((player != null) && !player.isOnline()))
		{
			throw new NullArgumentException("Player is null");
		}
		if (this.hasPlayerJoined(player))
		{
			this.joineds.remove(player);
			this.spectatorQuit(player);
			this.playerRestore(player);
			this.updateSigns();
		}
		return this;
	}

	public Event spectatorJoin(final Player player)
	{
		if ((player == null) || ((player != null) && !player.isOnline()))
		{
			throw new NullArgumentException("Player is null");
		}
		if (!this.spectators.contains(player))
		{
			for (final Player obj : this.getPlayers())
			{
				obj.hidePlayer(player);
			}
			player.setAllowFlight(true);
			player.setFlying(true);
			this.spectators.add(player);
		}
		return this;
	}

	public Event spectatorQuit(final Player player)
	{
		if ((player == null) || ((player != null) && player.isOnline()))
		{
			throw new NullArgumentException("Player is null");
		}
		if (this.spectators.contains(player))
		{
			for (final Player obj : this.getPlayers())
			{
				obj.showPlayer(player);
			}
			player.setAllowFlight(false);
			player.setFlying(false);
			this.spectators.remove(player);
		}
		return this;

	}

	public boolean hasPlayerJoined(final Player player)
	{
		return this.joineds.contains(player);
	}

	public void stop()
	{
		// STOP EVENT, GET WINNERS, ALIVES
		this.updateSigns();
	}

	public void start()
	{
		// START THE COUNTDOWN
		if (this.getEventState() == EventState.OPENED)
		{
			new EventCountdown(this, (Integer) this.getEventData().getData("options.countdown.seconds"));
			this.updateSigns();
		}
	}

	public void forceStop()
	{
		// STOP FORCE EVENT
		for (final Player player : this.joineds)
		{
			this.playerQuit(player);
		}
		this.updateSigns();
	}

	public void forceStart()
	{
		// START EVENT
		if (this.getPlayers().size() < (Integer) this.getEventData().getData("event.min"))
		{
			// STOP
			// MESSAGE CANCELED MIN PLAYER
			this.forceStop();
		}
		// CODE START
		this.updateSigns();
	}

	public void playerBackup(final Player player)
	{
		// BACKUP PLAYER
		this.getEventData().updateData(player.getName() + ".health", player.getHealth());
		this.getEventData().updateData(player.getName() + ".food", player.getFoodLevel());
		this.getEventData().updateData(player.getName() + ".exp", player.getExp());
		this.getEventData().updateData(player.getName() + ".expLevel", player.getLevel());
		this.getEventData().updateData(player.getName() + ".location", player.getLocation());
		this.getEventData().updateData(player.getName() + ".inventory.contents", player.getInventory().getContents());
		this.getEventData().updateData(player.getName() + ".inventory.armorContents", player.getInventory().getArmorContents());
	}

	public void playerRestore(final Player player)
	{
		// RESTORE BACKUP PLAYER
		player.setHealth((Integer) this.getEventData().getData(player.getName() + ".health"));
		player.setFoodLevel((Integer) this.getEventData().getData(player.getName() + ".food"));
		player.setExp((Float) this.getEventData().getData(player.getName() + ".exp"));
		player.setLevel((Integer) this.getEventData().getData(player.getName() + ".expLevel"));
		player.teleport((Location) this.getEventData().getData(player.getName() + ".location"));
		player.getInventory().setContents((ItemStack[]) this.getEventData().getData(player.getName() + ".inventory.contents"));
		player.getInventory().setArmorContents((ItemStack[]) this.getEventData().getData(player.getName() + ".inventory.armorContents"));
	}

	@SuppressWarnings("unchecked")
	public void updateSigns()
	{
		if (this.getEventData().containsKey("options.signs.locations") && (this.getEventData().getData("options.signs.locations") != null))
		{
			/**
			 * TODO: trocar List por Vector
			 */
			final List<Location> locations = (List<Location>) this.getEventData().getData("options.signs.locations");
			for (final Location location : locations)
			{
				final Block block = location.getWorld().getBlockAt(location);
				if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN))
				{
					/*
					 * TODO: arrumar o sign.setLine(), note que todos indexes são 0
					 */
					final String string = String.valueOf(this.getEventData().getData("options.message." + this.getEventState().getPath()));
					final Sign sign = (Sign) block.getState();
					sign.setLine(0, String.valueOf(this.getEventData().getData("options.signs.line.1")).replace("{state]", string).replace("{size}", String.valueOf(this.getPlayers().size())).replace("{name}", this.getEventName()).replaceAll("&", "�"));
					sign.setLine(0, String.valueOf(this.getEventData().getData("options.signs.line.2")).replace("{state]", string).replace("{size}", String.valueOf(this.getPlayers().size())).replace("{name}", this.getEventName()).replaceAll("&", "�"));
					sign.setLine(0, String.valueOf(this.getEventData().getData("options.signs.line.3")).replace("{state]", string).replace("{size}", String.valueOf(this.getPlayers().size())).replace("{name}", this.getEventName()).replaceAll("&", "�"));
					sign.setLine(0, String.valueOf(this.getEventData().getData("options.signs.line.4")).replace("{state]", string).replace("{size}", String.valueOf(this.getPlayers().size())).replace("{name}", this.getEventName()).replaceAll("&", "�"));
					sign.update();
				}
			}
		}
	}

}
