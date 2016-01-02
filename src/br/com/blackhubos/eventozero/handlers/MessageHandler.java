/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright © 2016 BlackHub OS and contributors.
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
package br.com.blackhubos.eventozero.handlers;

import java.io.Serializable;

import br.com.blackhubos.eventozero.util.Framework.Configuration;

public final class MessageHandler implements Serializable, Cloneable
{

	private static final long serialVersionUID = -8235384195413492546L;
	public static String SEM_PERMISSÃO = "&7Você não tem permissão para fazer isto.";

	/**
	 * Recarrega todas as mensagens da classe MessageHandler, carregando todas as keys encontradas pelo arquivo fornecido na configuração.
	 *
	 * @param flatfile Uma {@link Configuration} para fazer o carregamento em UTF-8 das mensagens.
	 * @since EventoZero v1.0.1-ALPHA
	 */
	public MessageHandler(final Configuration flatfile)
	{

	}

}
