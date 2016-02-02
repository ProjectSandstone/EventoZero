/*
 * Copyright (C) 2016 Leonardosc
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package io.github.bktlib.command;

import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bktlib.command.annotation.Command;

/**
 * Essa classe é meio que uma wrapper de {@link CommandSender}
 */
public class CommandSource
{
	private static final char SECT_CH = '\u00a7';

	private final CommandSender wrappedSender;

	CommandSource(final CommandSender wrappedSender)
	{
		this.wrappedSender = wrappedSender;
	}

	public CommandSender getSender()
	{
		return this.wrappedSender;
	}

	public static CommandSource getConsoleSource()
	{
		return LazyConsoleSourceHolder.INSTANCE;
	}

	public void sendMessages(final String... messages)
	{
		if (messages == null)
		{
			return;
		}

		Stream.of(messages).map(msg -> CharMatcher.anyOf("&").collapseFrom(msg, CommandSource.SECT_CH)).forEach(this.wrappedSender::sendMessage);
	}

	public void sendMessage(final String message, final Object... args)
	{
		this.wrappedSender.sendMessage(String.format(CharMatcher.anyOf("&").collapseFrom(message, CommandSource.SECT_CH), args));
	}

	public void sendMessage(final String message)
	{
		this.sendMessage(message, new Object[0]);
	}

	public void sendMessage(final Object rawMessage)
	{
		String message;

		if (rawMessage instanceof String)
		{
			message = (String) rawMessage;
		}
		else
		{
			message = String.valueOf(rawMessage);
		}

		this.sendMessage(message);
	}

	public String getName()
	{
		return this.wrappedSender.getName();
	}

	public boolean isOp()
	{
		return this.wrappedSender.isOp();
	}

	public boolean isPlayer()
	{
		return this.wrappedSender instanceof Player;
	}

	public boolean isConsole()
	{
		return !this.isPlayer();
	}

	public void setOp(final boolean op)
	{
		this.wrappedSender.setOp(op);
	}

	public boolean hasPermission(final String permission)
	{
		return this.wrappedSender.hasPermission(permission);
	}

	public boolean canUse(final CommandBase command)
	{
		final Optional<String> commandPermission = command.getPermission();

		return commandPermission.isPresent() && this.hasPermission(commandPermission.get());
	}

	public boolean canUse(final Command annotation)
	{
		return this.hasPermission(annotation.permission());
	}

	public CommandSender toCommandSender()
	{
		return this.wrappedSender;
	}

	public Player toPlayer()
	{
		Preconditions.checkState(this.isPlayer(), "Cannot cast console to player!");

		return (Player) this.wrappedSender;
	}

	private static class LazyConsoleSourceHolder
	{
		public static final CommandSource INSTANCE = new CommandSource(Bukkit.getConsoleSender());
	}
}
