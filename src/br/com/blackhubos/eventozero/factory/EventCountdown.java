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

import org.bukkit.Bukkit;

public class EventCountdown implements Runnable
{

	private final Event event;
	private int seconds;

	public EventCountdown(final Event event, final int seconds)
	{
		this.event = event;
		this.seconds = seconds;
		this.run();
	}

	@Override
	public void run()
	{
		if (this.seconds > 0)
		{
			this.seconds--;
			Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EventoZero"), this, 20L);
		}
		else
		{
			this.event.forceStart();
		}

	}

}
