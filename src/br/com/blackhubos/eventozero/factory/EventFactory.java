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

import java.io.File;

import org.bukkit.plugin.Plugin;

import br.com.blackhubos.eventozero.util.Framework.Configuration;

public final class EventFactory
{

	public static Event createMyEvent(final String name)
	{
		return new Event(name).updateDescription("");
	}

	public static void loadEvents(final Plugin plugin)
	{
		final File folder = new File(plugin.getDataFolder() + File.separator + "eventos" + File.separator);
		for (final File file : folder.listFiles())
		{
			if (file.getName().endsWith(".yml"))
			{
				final Configuration configuration = new Configuration(file); // Já carrega automaticamente
				final Event event = new Event(configuration.getString("name")).updateDescription(configuration.getString("description"));

				event.getEventData().updateData("options.signs.line.1", configuration.getString("signs.lines.1"));
				event.getEventData().updateData("options.signs.line.2", configuration.getString("signs.lines.2"));
				event.getEventData().updateData("options.signs.line.3", configuration.getString("signs.lines.3"));
				event.getEventData().updateData("options.signs.line.4", configuration.getString("signs.lines.4"));

				event.getEventData().updateData("options.message.opened", configuration.getString("options.message.opened"));
				event.getEventData().updateData("options.message.prestarted", configuration.getString("options.message.prestarted"));
				event.getEventData().updateData("options.message.occurring", configuration.getString("options.message.occurring"));
				event.getEventData().updateData("options.message.ending", configuration.getString("options.message.ending"));
				event.getEventData().updateData("options.message.closed", configuration.getString("options.message.closed"));
			}
		}
	}

}
