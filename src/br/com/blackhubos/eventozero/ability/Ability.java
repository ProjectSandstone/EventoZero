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
package br.com.blackhubos.eventozero.ability;

import org.bukkit.entity.Player;

public abstract class Ability {
	
	private long cooldown;
	private long lastTime;
	
	public Ability(long cooldown){
		this.cooldown = cooldown;
		this.lastTime = 0;
	}
	
	public long getRemaingTime(){
		return (lastTime - (cooldown * 1000));
	}
	
	public long getRemaingTimePostive(){
		return (getRemaingTime() >= 0? getRemaingTime() : 0);
	}
	
	public boolean canUse(){
		return (getRemaingTimePostive() == 0);
	}
	
	public abstract boolean tryUse(Player player);
	
	public abstract void foceUse(Player player);
	
}
