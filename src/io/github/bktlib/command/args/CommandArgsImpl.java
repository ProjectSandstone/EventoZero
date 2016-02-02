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

package io.github.bktlib.command.args;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import io.github.bktlib.command.CommandResult;

class CommandArgsImpl implements CommandArgs
{
	private String[] rawArgs;

	CommandArgsImpl(String... rawArgs)
	{
		// TODO: parser ( parse arg between " this " )
		this.rawArgs = rawArgs;
	}

	@Override
	public int size()
	{
		return rawArgs.length;
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public String get( int argIndex )
	{
		Preconditions.checkPositionIndex( argIndex, size() );

		return rawArgs[argIndex];
	}

	@Override
	public String[] getRawArgs()
	{
		return rawArgs;
	}

	@Override
	public OptionalInt getAsInt( int argIdx )
	{
		try
		{
			return OptionalInt.of(
					Integer.parseInt( get( argIdx ) ) );
		}
		catch ( NumberFormatException e )
		{
			return OptionalInt.empty();
		}
	}

	@Override
	public OptionalDouble getAsDouble( int argIdx )
	{
		try
		{
			return OptionalDouble.of(
					Double.parseDouble( get( argIdx ) ) );
		}
		catch ( NumberFormatException e )
		{
			return OptionalDouble.empty();
		}
	}

	@Override
	public Optional<Boolean> getAsBoolean( int argIdx )
	{
		try
		{
			return Optional.of(
					Boolean.parseBoolean( get( argIdx ) ) );
		}
		catch ( NumberFormatException e )
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<Player> getAsPlayer( int argIdx )
	{
		Player player = Bukkit.getPlayer( get( argIdx ) );

		return player == null
				? Optional.empty()
				: Optional.of( player );
	}

	@Override
	public int tryGetAsInt( int argIdx, Function<String, CommandResult> failCallback )
	{
		OptionalInt i = getAsInt( argIdx );

		if ( i.isPresent() )
			return i.getAsInt();

		CommandResult result = failCallback.apply( get( argIdx ) );

		throw new io.github.bktlib.command.CommandException( result );
	}

	@Override
	public double tryGetAsDouble( int argIdx, Function<String, CommandResult> failCallback )
	{
		OptionalInt i = getAsInt( argIdx );

		if ( i.isPresent() )
			return i.getAsInt();

		CommandResult result = failCallback.apply( get( argIdx ) );

		throw new io.github.bktlib.command.CommandException( result );
	}

	@Override
	public boolean tryGetAsBoolean( int argIdx, Function<String, CommandResult> failCallback )
	{
		Optional<Boolean> i = getAsBoolean( argIdx );

		if ( i.isPresent() )
			return i.get();

		CommandResult result = failCallback.apply( get( argIdx ) );

		throw new io.github.bktlib.command.CommandException( result );
	}

	@Override
	public Player tryGetAsPlayer( int argIdx, Function<String, CommandResult> failCallback )
	{
		Optional<Player> i = getAsPlayer( argIdx );

		if ( i.isPresent() )
			return i.get();

		CommandResult result = failCallback.apply( get( argIdx ) );

		throw new io.github.bktlib.command.CommandException( result );
	}

	@Override
	public int unsafeGetAsInt( int argIdx )
	{
		return Integer.parseInt( get( argIdx ) );
	}

	@Override
	public double unsafeGetAsDouble( int argIdx )
	{
		return Double.parseDouble( get( argIdx ) );
	}

	@Override
	public boolean unsafeGetAsBoolean( int argIdx )
	{
		return Boolean.parseBoolean( get( argIdx ) );
	}

	@Override
	public Player unsafeGetAsPlayer( int argIdx )
	{
		return Bukkit.getPlayer( get( argIdx ) );
	}

	@Override
	public String toString()
	{
		return Arrays.toString( rawArgs );
	}
}
