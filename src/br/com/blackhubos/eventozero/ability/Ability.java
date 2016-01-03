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
package br.com.blackhubos.eventozero.ability;

import org.bukkit.entity.Player;

public abstract class Ability implements Cloneable
{

	private final String name;
	private final long cooldown;
	
	protected long lastTime;

	public Ability(String name, final long cooldown)
	{
		this.name = name;
		this.cooldown = cooldown;
		this.lastTime = 0;
	}
	
	public String getName(){
		return this.name;
	}
	
	public long getCooldown(){
		return this.cooldown;
	}

	public long getRemaingTime()
	{
		return (this.lastTime - (getCooldown() * 1000));
	}

	public long getRemaingTimePostive()
	{
		return (this.getRemaingTime() >= 0 ? this.getRemaingTime() : 0);
	}

	public boolean canUse()
	{
		return (this.getRemaingTimePostive() == 0);
	}
	
	public abstract Ability clone();

	public abstract boolean tryUse(final Player player);

	public abstract void foceUse(final Player player);
	
	public Ability updateTime(){
		this.lastTime = System.currentTimeMillis();
		return this;
	}
	public Ability updateTime(final long lastTime){
		this.lastTime = lastTime;
		return this;
	}
}
