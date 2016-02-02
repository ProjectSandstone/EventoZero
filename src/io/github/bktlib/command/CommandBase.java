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

import static io.github.bktlib.command.UsageTarget.IN_GAME;
import static io.github.bktlib.command.UsageTarget.NOT_IN_GAME;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.args.CommandArgs;
import io.github.bktlib.common.Strings;

public abstract class CommandBase
{
	private static final String NOT_ALLOWED_INGAME = "§cEste comando nao pode ser usado in-game.";
	private static final String ONLY_ALLOWED_INGAME = "§cEste comando so pode ser usado in-game.";

	Command commandAnnotation;
	Map<String, CommandBase> subCommands;

	CommandBase(final Command annotation)
	{
		commandAnnotation = annotation;
		subCommands = Maps.newHashMap();
	}

	protected CommandBase()
	{
		commandAnnotation = Preconditions.checkNotNull(
				getClass().getAnnotation( Command.class ), "Missing 'Command' annotation" );
		subCommands = Maps.newHashMap();
	}

	/**
	 * @return Nome desse comando
	 */
	public String getName()
	{
		return commandAnnotation.name();
	}

	/**
	 * @return Um {@link Set} Contendo as 'aliases' desse comando
	 */
	public Set<String> getAliases()
	{
		return Sets.newHashSet( commandAnnotation.aliases() );
	}

	/**
	 * @return Um {@link Set} Contendo os sub comandos desse comando
	 */
	public Set<CommandBase> getSubCommands()
	{
		return Sets.newHashSet( subCommands.values() );
	}

	/**
	 * @return Retorna o {@link Command#usageTarget() UsageTarget} desse
	 *         comando.
	 * @see UsageTarget
	 */
	public UsageTarget getUsageTarget()
	{
		return commandAnnotation.usageTarget();
	}

	/**
	 * @return A {@link Command#description() descrição} desse commando
	 */
	public Optional<String> getDescription()
	{
		return Optional.ofNullable(
				Strings.emptyToNull( commandAnnotation.description() ) );
	}

	/**
	 * @return A {@link Command#permission() permissão} desse commando
	 */
	public Optional<String> getPermission()
	{
		return Optional.ofNullable(
				Strings.emptyToNull( commandAnnotation.permission() ) );
	}

	/**
	 * @return A {@link Command#usage() usage} desse commando
	 */
	public Optional<String> getUsage()
	{
		return Optional.ofNullable(
				Strings.emptyToNull( commandAnnotation.usage() ) );
	}

	/**
	 * Retorna a anotação {@link Command} desse comando, essa anotação é
	 * obrigatoria em todos os comandos.
	 *
	 * @return A anotação {@link Command}
	 */
	public Command getAnnotation()
	{
		return commandAnnotation;
	}

	/**
	 * Esse método é chamado quando o comando é executado.
	 *
	 * @param src
	 *            A {@link CommandSource fonte} do comando, o jogador/coisa que
	 *            enviou esse comando.
	 * @param args
	 *            Os argumentos passados ao executar esse comando, por exeplo,
	 *            ao executar /give jogador DIAMOND 1, os argumentos seriam
	 *            {@literal {jogador, DIAMOND, 1}}
	 *
	 * @return O {@link CommandResult} resultado da execução do comando.
	 *
	 * @see CommandSource
	 * @see CommandResult
	 */
	public abstract CommandResult onExecute( CommandSource src, CommandArgs args );

	/**
	 * Método usado internamente para fazer verificações, chamar sub comandos
	 * etc.
	 *
	 * Caso tudo ocorra como desejado ele irá chamar o método
	 * {@link #onExecute(CommandSource, CommandArgs)}
	 */
	void execute( final CommandSender sender, final String[] rawArgs )
	{
		final UsageTarget target = getUsageTarget();

		if ( target == IN_GAME && !(sender instanceof Player) )
		{
			sender.sendMessage( ONLY_ALLOWED_INGAME );
		}
		else if ( target == NOT_IN_GAME && (sender instanceof Player) )
		{
			sender.sendMessage( NOT_ALLOWED_INGAME );
		}
		else
		{
			final CommandSource src = sender == Bukkit.getConsoleSender()
					? CommandSource.getConsoleSource()
					: new CommandSource( sender );

			try
			{
				if ( rawArgs.length > 0 )
				{
					final CommandBase subCmd = subCommands.get( rawArgs[0].toLowerCase() );

					if ( subCmd != null )
					{
						subCmd.execute( sender, Arrays.copyOfRange( rawArgs, 1, rawArgs.length ) );
						return;
					}
				}

				final CommandResult result = onExecute( src, CommandArgs.of( rawArgs ) );

				if ( result == CommandResult.success() )
					return;

				if ( result == CommandResult.showUsage() )
				{
					final String msg = Strings.of(
							result.getType().getColor(), "Use: /", getName(), " ", getUsage().orElse( "" ) );

					sender.sendMessage( msg );

					return;
				}

				result.getMessage().ifPresent( sender::sendMessage );
			}
			catch ( CommandException ex )
			{
				if ( ex.getResult() == CommandResult.success() )
					return;

				ex.getResult().getMessage().ifPresent( sender::sendMessage );
			}
		}
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper( this )
				.add( "name", getName() )
				.toString();
	}
}
