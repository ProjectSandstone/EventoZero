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

import org.bukkit.ChatColor;

import java.util.Optional;

/**
 * Essa classe representa o resultado da execução do um comando.
 */
public final class CommandResult
{
	private static final String GENERIC_ERROR_MESSAGE = "§4An internal error occurred attempting to execute this command.";

	private static final CommandResult SUCCESS = new CommandResult( null, ResultType.SUCCESS );
	private static final CommandResult SHOW_USAGE = new CommandResult( null, ResultType.SHOW_USAGE );

	private String message;
	private ResultType type;

	private CommandResult(final String message, final ResultType type)
	{
		this.message = message;
		this.type = type;
	}

	/**
	 * Retorna a mensagem do resultado, no caso do #success() ou
	 * #showUsage() ela é {@code nula}
	 * 
	 * @return Um Optional} contendo a mensagem do resultado, ou
	 *         Optional#empty() caso a mensagem seja {@code nula}
	 */
	public Optional<String> getMessage()
	{
		return message == null
				? Optional.empty()
				: Optional.of( type.getColor() + message );
	}

	/**
	 * @return O ResultType tipo} do resultado.
	 * 
	 * @see ResultType
	 */
	public ResultType getType()
	{
		return type;
	}

	/**
	 * Indica que um erro ocorreu.
	 * 
	 * @param message
	 *            Mensagem do ocorrido
	 * @return Nova instancia dessa classe com as informações necessarias para
	 *         serem usadas internamente.
	 */
	public static CommandResult fail( final String message )
	{
		return new CommandResult( message, ResultType.FAIL );
	}

	/**
	 * @see #fail(String)
	 */
	public static CommandResult fail( final String message, final Object... formatArgs )
	{
		return new CommandResult( String.format( message, formatArgs ), ResultType.FAIL );
	}

	/**
	 * Indica que ocorreu alguma coisa inesperada..
	 *
	 * @param message
	 *            Mensagem do ocorrido
	 * @return Nova instancia dessa classe com as informações necessarias para
	 *         serem usadas internamente.
	 */
	public static CommandResult warning( final String message )
	{
		return new CommandResult( message, ResultType.WARNING );
	}

	/**
	 * @see #warning(String)
	 */
	public static CommandResult warning( final String message, final Object... formatArgs )
	{
		return new CommandResult( String.format( message, formatArgs ), ResultType.WARNING );
	}

	/**
	 * Indica que a execução foi um sucesso.
	 * 
	 * @return Nova instancia dessa classe com as informações necessarias para
	 *         serem usadas internamente.
	 */
	public static CommandResult success()
	{
		return SUCCESS;
	}

	/**
	 * Indica ao sistema que ele deve enviar a CommandBase#getUsage()
	 * para o jogador. Geralmente usado quando o jogador digita o comando com os
	 * argumentos incorretos.
	 * 
	 * @return Nova instancia dessa classe com as informações necessarias para
	 *         serem usadas internamente.
	 */
	public static CommandResult showUsage()
	{
		return SHOW_USAGE;
	}

	/**
	 * Indica que ocorreu um erro generico.
	 * 
	 * <p>
	 * A mensagem de erro generico PADRÃO é
	 * {@code "An internal error occurred attempting to execute this command."}
	 * </p>
	 * 
	 * @return Nova instancia dessa classe com as informações necessarias para
	 *         serem usadas internamente.
	 */
	public static CommandResult genericError()
	{
		return new CommandResult( GENERIC_ERROR_MESSAGE, ResultType.GENERIC_ERROR );
	}

	/**
	 * Tipos de resultado. Usado internamente.
	 */
	public enum ResultType
	{
		/**
		 * @see CommandResult#success()
		 */
		SUCCESS(ChatColor.GREEN),

		/**
		 * @see CommandResult#fail(String)
		 */
		FAIL(ChatColor.RED),

		/**
		 * @see CommandResult#genericError()
		 */
		GENERIC_ERROR(FAIL.getColor()),

		/**
		 * @see CommandResult#warning(String)
		 */
		WARNING(ChatColor.YELLOW),

		/**
		 * @see CommandResult#showUsage()
		 */
		SHOW_USAGE(ChatColor.GRAY);

		ChatColor color;

		ResultType(ChatColor color)
		{
			this.color = color;
		}

		public ChatColor getColor()
		{
			return color;
		}
	}
}
