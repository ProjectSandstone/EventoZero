/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright ï¿½ 2016 BlackHub OS and contributors.
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

public class Event {
	
	private final String eventName;
	private final EventData eventData;
	
	private final Vector<Player> joineds;
	private final Vector<Player> spectators;
	private final Vector<Party> partys;
	
	private String eventDescription;
	private EventState eventoState;
	
	public Event(String name){
		this.eventName = name;
		this.joineds = new Vector<>();
		this.spectators = new Vector<>();
		this.partys = new Vector<>();
		this.eventData = new EventData();
	}
	
	public String getEventName(){
		return this.eventName;
	}
	
	public String getEventDescription(){
		return this.eventDescription;
	}
	
	public EventData geEventData(){
		return this.eventData;
	}
	
	public EventState geEventState(){
		return this.eventoState;
	}
	
	public Event updateDescription(String desc){
		eventDescription = desc;
		return this;
	}
	
	public Vector<Party> getPartys(){
		return this.partys;
	}
	
	public Vector<Player> getPlayers(){
		return joineds;
	}
	
	public Vector<Player> getSpectators(){
		return spectators;
	}
	
	public Event playerJoin(Player player){
		if(player == null || (player != null && !player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(!hasPlayerJoined(player)){
			joineds.add(player);
			playerBackup(player);
			updateSigns();
		}
		return this;
	}
	
	public Event playerQuit(Player player){
		if(player == null || (player != null && !player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(hasPlayerJoined(player)){
			joineds.remove(player);
			spectatorQuit(player);
			playerRestore(player);
			updateSigns();
		}
		return this;
	}
	
	public Event spectatorJoin(Player player){
		if(player == null || (player != null && !player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(!spectators.contains(player)){
			for(Player obj : getPlayers()){
				obj.hidePlayer(player);
			}
			player.setAllowFlight(true);
			player.setFlying(true);
			spectators.add(player);
		}
		return this;
	}
	
	public Event spectatorQuit(Player player){
		if(player == null || (player != null && player.isOnline()))
			throw new NullArgumentException("Player is null");
		if(spectators.contains(player)){
			for(Player obj : getPlayers()){
				obj.showPlayer(player);
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
		if(geEventState() == EventState.OPENED){
			new EventCountdown(this, (Integer) geEventData().getData("options.countdown.seconds"));
			updateSigns();
		}
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
		if(getPlayers().size() < (Integer) geEventData().getData("event.min")){
			// STOP
			// MESSAGE CANCELED MIN PLAYER
			forceStop();
		}
		// CODE START
		updateSigns();
	}
	
	public void playerBackup(Player player){
		// BACKUP PLAYER
		geEventData().updateData(player.getName() + ".health", player.getHealth());
		geEventData().updateData(player.getName() + ".food", player.getFoodLevel());
		geEventData().updateData(player.getName() + ".exp", player.getExp());
		geEventData().updateData(player.getName() + ".expLevel", player.getLevel());
		geEventData().updateData(player.getName() + ".location", player.getLocation());
		geEventData().updateData(player.getName() + ".inventory.contents", player.getInventory().getContents());
		geEventData().updateData(player.getName() + ".inventory.armorContents", player.getInventory().getArmorContents());
	}
	
	public void playerRestore(Player player){
		// RESTORE BACKUP PLAYER
		player.setHealth((Integer)geEventData().getData(player.getName() + 							 ".health"));
		player.setFoodLevel((Integer)geEventData().getData(player.getName() + 						 ".food"));
		player.setExp((Float)geEventData().getData(player.getName() + 								 ".exp"));
		player.setLevel((Integer)geEventData().getData(player.getName() + 							 ".expLevel"));
		player.teleport((Location)geEventData().getData(player.getName() + 							 ".location"));
		player.getInventory().setContents((ItemStack[])geEventData().getData(player.getName() +      ".inventory.contents"));
		player.getInventory().setArmorContents((ItemStack[])geEventData().getData(player.getName() + ".inventory.armorContents"));
	}
	

	
	public void updateSigns(){
		if(geEventData().containsKey("options.signs.locations") && (geEventData().getData("options.signs.locations") != null)){
			List<Location> locations = (List<Location>) geEventData().getData("options.signs.locations");
			for(Location location : locations){
				Block block = location.getWorld().getBlockAt(location);
				if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
					String string =  String.valueOf(geEventData().getData("options.message." + geEventState().getPath()));
					Sign sign = (Sign) block.getState();
					sign.setLine(0, String.valueOf(geEventData().getData("options.signs.line.1")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.setLine(0, String.valueOf(geEventData().getData("options.signs.line.2")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.setLine(0, String.valueOf(geEventData().getData("options.signs.line.3")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.setLine(0, String.valueOf(geEventData().getData("options.signs.line.4")).replace("{state]", string).replace("{size}", String.valueOf(getPlayers().size())).replace("{name}", getEventName()).replaceAll("&", "§"));
					sign.update();
				}
			}
		}
	}
	


}
