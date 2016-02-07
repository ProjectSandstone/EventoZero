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

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.args.CommandArgs;
import io.github.bktlib.common.Builder;

public class CommandBuilder implements Builder<CommandBase>
{
	private String name, usage, permission, description;
	private String[] aliases, subCommands;
	private UsageTarget usageTarget;
	private CommandFunction executor;
	
	private CommandBuilder( String name )
	{
		this.name = name;
		
		usage = permission = description = "";
		aliases = subCommands = new String[0];
		usageTarget = UsageTarget.BOTH;
	}

	public static CommandBuilder name( String name ) 
	{
		checkNotNull( name, "name cannot be null." );
		
		return new CommandBuilder( name );
	}
	
	public CommandBuilder permission( String permission )
	{
		if ( permission != null )
			this.permission = permission;
		return this;
	}

	public CommandBuilder description( String description )
	{
		if ( description != null )
			this.description = description;
		return this;
	}

	public CommandBuilder usage( String usage )
	{
		if ( usage != null )
			this.usage = usage;
		return this;
	}

	public CommandBuilder aliases( String ... aliases )
	{
		if ( aliases != null )
			this.aliases = aliases;
		return this;
	}

	public CommandBuilder subCommands( String ... subCommands )
	{
		if ( subCommands != null )
			this.subCommands = subCommands;
		return this;
	}

	public CommandBuilder usageTarget( UsageTarget usageTarget )
	{
		if ( usageTarget != null )
			this.usageTarget = usageTarget;
		return this;
	}

	public CommandBuilder onExecute( CommandFunction executor )
	{
		checkNotNull( executor, "executor cannot be null." );
		this.executor = executor;
		return this;
	}

	public CommandBase buildAndRegister( CommandManager manager )
	{
		checkNotNull( manager, "manager cannot be null." );
		CommandBase built = build();
		manager.register( built );
		return built;
	}

	@Override
	public CommandBase build()
	{
		checkNotNull( executor, "executor not defined, use onExecutor( (src, args) -> {...} ) to define it." );
		
		Command commandAnn = CommandManagerImpl.createCommandAnnotation( 
				name, 
				permission, 
				description, 
				usage, 
				aliases, 
				subCommands, 
				usageTarget 
		);
		
		return new CommandBase( commandAnn ) 
		{
			@Override
			public CommandResult onExecute( final CommandSource src, final CommandArgs args )
			{
				return executor.apply( src, args );
			}
		};
	}
}
