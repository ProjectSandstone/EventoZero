/**
 *
 * EventoZero - .
 * Copyright © 2016 BlackHub OS.
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
package br.com.blackhubos.eventozero;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

public abstract class EventCommand implements Serializable, CommandExecutor
{

	private static final long serialVersionUID = -3918201243764031687L;
	private String command;
	private String commandDesc;
	private Vector<String> aliases;

	public EventCommand command(final String commandName)
	{
		this.command = commandName;
		return this;
	}

	public String command()
	{
		return this.command;
	}

	public String description()
	{
		return this.commandDesc;
	}

	public Vector<String> aliases()
	{
		return this.aliases;
	}

	public EventCommand description(final String commandDesc)
	{
		this.commandDesc = commandDesc;
		return this;
	}

	public EventCommand aliases(final Collection<String> aliases)
	{
		this.aliases = new Vector<String>(aliases);
		return this;
	}

	public void register()
	{
		try {
			Field bukkitCommandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMapField.setAccessible(true);
			SimpleCommandMap commandMap = (SimpleCommandMap) bukkitCommandMapField.get(Bukkit.getServer());
			Command commandToRegister = new Command(command, commandDesc, "/" + command, aliases) {
				@Override
				public boolean execute(CommandSender sender, String label, String[] args) {
					onCommand(sender, this, label, args);
					return true;
				}
			};
			commandMap.register("EventoZero", commandToRegister);
		} catch (Exception e) {
			// TODO: Log the exception xD
			e.printStackTrace();
		}
	}

	@Override
	public abstract boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args);

}
