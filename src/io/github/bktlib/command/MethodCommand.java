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

import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Throwables;

import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.args.CommandArgs;
import io.github.bktlib.common.Strings;
import io.github.bktlib.reflect.MethodRef;

class MethodCommand extends CommandBase
{
	MethodRef ref;

	MethodCommand(final MethodRef ref)
	{
		super( ref.getMethod().getAnnotation( Command.class ) );
		
		this.ref = ref;
		this.ref.getMethod().setAccessible( true );
	}

	MethodCommand(final MethodRef ref, Command commandAnnotation)
	{
		super( commandAnnotation );
		
		this.ref = ref;
		this.ref.getMethod().setAccessible( true );
	}

	@Override
	public CommandResult onExecute( final CommandSource src, final CommandArgs args )
	{
		try
		{
			return (CommandResult) ref.getMethod().invoke(
					ref.getOwner(), src, args );
		}
		catch ( InvocationTargetException e )
		{
			final Throwable root = Throwables.getRootCause( e );

			if ( root instanceof CommandException )
			{
				return ((CommandException) root).getResult();
			}
			else
			{
				e.printStackTrace();
			}

			return CommandResult.genericError();
		}
		catch ( IllegalAccessException | IllegalArgumentException e )
		{
			e.printStackTrace();

			return CommandResult.genericError();
		}
	}

	@Override
	public String toString()
	{
		return Strings.of( ref );
	}
}
