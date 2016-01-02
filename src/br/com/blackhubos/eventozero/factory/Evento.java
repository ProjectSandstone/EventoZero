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

package br.com.blackhubos.eventozero.factory;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import br.com.blackhubos.eventozero.party.Party;

public class Evento {
	
	private final String eventName;
	private final EventoData eventData;
	
	private final Vector<Player> joineds;
	private final Vector<Player> spectators;
	private final Vector<Party> partys;
	
	private String eventDescription;
	private EventoState eventoState;
	
	public Evento(String name){
		this.eventName = name;
		this.joineds = new Vector<>();
		this.spectators = new Vector<>();
		this.partys = new Vector<>();
		this.eventData = new EventoData();
	}
	
	public String getEventName(){
		return this.eventName;
	}
	
	public String getEventDescription(){
		return this.eventDescription;
	}
	
	public EventoData geEventoData(){
		return this.eventData;
	}
	
	public EventoState geEventoState(){
		return this.eventoState;
	}
	
	public Evento updateDescription(String desc){
		eventDescription = desc;
		return this;
	}
	
	public Vector<Player> getPlayers(){
		return joineds;
	}
	
	public Vector<Player> getSpectators(){
		return spectators;
	}
	
	public Evento playerJoin(Player player){
		if(player == null || (player != null && player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(!hasPlayerJoined(player)){
			joineds.add(player);
			playerBackup(player);
			updateSigns();
		}
		return this;
	}
	
	public Evento playerQuit(Player player){
		if(player == null || (player != null && player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(hasPlayerJoined(player)){
			joineds.remove(player);
			spectatorQuit(player);
			playerRestore(player);
			updateSigns();
		}
		return this;
	}
	
	public Evento spectatorJoin(Player player){
		if(player == null || (player != null && player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(!spectators.contains(player)){
			for(Player obj : Bukkit.getOnlinePlayers()){
				obj.hidePlayer(player);
			}
			player.setAllowFlight(true);
			player.setFlying(true);
			spectators.add(player);
		}
		return this;
	}
	
	public Evento spectatorQuit(Player player){
		if(player == null || (player != null && player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(spectators.contains(player)){
			for(Player obj : Bukkit.getOnlinePlayers()){
				obj.hidePlayer(player);
			}
			player.setAllowFlight(false);
			player.setFlying(false);
			spectators.remove(player);
		}
		return this;
		
	}
	
	public boolean hasPlayerJoined(Player player){
		return joineds.contains(player);
	}
	
	public void stop(){
		// STOP EVENT, GET WINNERS, ALIVES
		updateSigns();
	}
	
	public void start(){
		// START THE COUNTDOWN
		new EventoCountdown(this, (Integer) geEventoData().getData("options.countdown.seconds"));
		updateSigns();
	}
	
	public void forceStop(){
		// STOP FORCE EVENT
		for(Player player : joineds){
			playerQuit(player);
		}
		updateSigns();
	}
	
	public void forceStart(){
		// START EVENT
		if(getPlayers().size() < (Integer) geEventoData().getData("event.min")){
			// STOP
			// MESSAGE CANCELED MIN PLAYER
			forceStop();
		}
		// CODE START
		updateSigns();
	}
	
	public void playerBackup(Player player){
		// BACKUP PLAYER
	}
	
	public void playerRestore(Player player){
		// RESTORE BACKUP PLAYER
	}
	

	
	public void updateSigns(){
		if(geEventoData().containsKey("options.signs.locations") && (geEventoData().getData("options.signs.locations") != null)){
			List<Location> locations = (List<Location>) geEventoData().getData("options.signs.locations");
			for(Location location : locations){
				Block block = location.getWorld().getBlockAt(location);
				if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
					String string =  String.valueOf(geEventoData().getData("options.message." + geEventoState().getPath()));
					Sign sign = (Sign) block.getState();
					sign.setLine(0, String.valueOf(geEventoData().getData("options.signs.line.1")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.setLine(0, String.valueOf(geEventoData().getData("options.signs.line.2")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.setLine(0, String.valueOf(geEventoData().getData("options.signs.line.3")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.setLine(0, String.valueOf(geEventoData().getData("options.signs.line.4")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.update();
				}
			}
		}
	}
	


}
