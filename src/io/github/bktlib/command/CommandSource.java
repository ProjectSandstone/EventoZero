/*
 *  Copyright (C) 2016 Leonardosc
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package io.github.bktlib.command;

import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import io.github.bktlib.command.annotation.Command;

/**
 * Essa classe é meio que uma wrapper de {@link CommandSender}
 */
public class CommandSource
{
	private static final char SECT_CH = '\u00a7';

	private CommandSender wrappedSender;

	CommandSource(CommandSender wrappedSender)
	{
		this.wrappedSender = wrappedSender;
	}

	public static CommandSource getConsoleSource()
	{
		return LazyConsoleSourceHolder.INSTANCE;
	}

	public void sendMessages( String... messages )
	{
		if ( messages == null )
			return;

		Stream.of( messages )
				.map( msg -> CharMatcher.anyOf( "&" ).collapseFrom( msg, SECT_CH ) )
				.forEach( wrappedSender::sendMessage );
	}

	public void sendMessage( String message, Object... args )
	{
		wrappedSender.sendMessage(
				String.format(
						CharMatcher.anyOf( "&" ).collapseFrom( message, SECT_CH ),
						args ) );
	}

	public void sendMessage( String message )
	{
		sendMessage( message, new Object[0] );
	}

	public void sendMessage( Object rawMessage )
	{
		String message;

		if ( rawMessage instanceof String )
			message = (String) rawMessage;
		else
			message = String.valueOf( rawMessage );

		sendMessage( message );
	}

	public String getName()
	{
		return wrappedSender.getName();
	}

	public boolean isOp()
	{
		return wrappedSender.isOp();
	}

	public boolean isPlayer()
	{
		return wrappedSender instanceof Player;
	}

	public boolean isConsole()
	{
		return !isPlayer();
	}

	public void setOp( boolean op )
	{
		wrappedSender.setOp( op );
	}

	public boolean hasPermission( String permission )
	{
		return wrappedSender.hasPermission( permission );
	}

	public boolean canUse( CommandBase command )
	{
		Optional<String> commandPermission = command.getPermission();

		return commandPermission.isPresent() &&
				hasPermission( commandPermission.get() );
	}

	public boolean canUse( Command annotation )
	{
		return hasPermission( annotation.permission() );
	}

	public CommandSender toCommandSender()
	{
		return wrappedSender;
	}

	public Player toPlayer()
	{
		Preconditions.checkState( isPlayer(),
				"Cannot cast console to player!" );

		return (Player) wrappedSender;
	}

	private static class LazyConsoleSourceHolder
	{
		public static final CommandSource INSTANCE = new CommandSource( Bukkit.getConsoleSender() );
	}
}
