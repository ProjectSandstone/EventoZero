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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

public class EventHandler
{

	private final Set<Event> events;

	public EventHandler()
	{
		this.events = new HashSet<>();
	}

	public Optional<Event> getEventByName(final String name)
	{
		for (final Event e : this.getEvents())
		{
			if (e.getEventName().equals(name))
			{
				return Optional.of(e);
			}
		}
		return Optional.empty();
	}

	public Optional<Event> getEventByPlayer(final Player player)
	{
		for (final Event e : this.getEvents())
		{
			if (e.hasPlayerJoined(player))
			{
				return Optional.of(e);
			}
		}
		return Optional.empty();
	}

	public Set<Event> getEvents()
	{
		return this.events;

	}

	public void loadEvent(final Event event)
	{
		Preconditions.checkNotNull(event, "Event is null");
		this.events.add(event);
	}

}
