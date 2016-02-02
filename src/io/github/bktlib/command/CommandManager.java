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

import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;

import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.args.CommandArgs;

/**
 * Classe reponsavel pelo gerenciamento dos {@link CommandBase comandos}
 * 
 * A maioria dos metodos lancam um {@link NullPointerException} caso o objeto
 * passado por argumento seja nulo.
 */
public interface CommandManager
{
	/**
	 * Registra o comando com a instancia passada por parametro.
	 *
	 * @param commandInstance
	 *            Instancia da classe do comando
	 */
	void register( CommandBase commandInstance );

	/**
	 * Registra o comando com a classe do comando passada por parametro.
	 * 
	 * @param command
	 *            A classe do comando.
	 */
	void register( Class<? extends CommandBase> command );

	/**
	 * Registra um metodo que representa um comando.
	 * 
	 * <p>
	 * O comando deve retornar um {@link CommandResult} e deve receber apenas 2
	 * parametros, o primeiro deve ser do tipo {@link CommandSource}, e o
	 * segundo deve ser do tipo {@link CommandArgs}
	 * <p>
	 * 
	 * @param methodClass
	 *            Classe que o metodo esta definido.
	 * @param methodName
	 *            Nome do metodo a ser registrado. (diferencia maiusculas e
	 *            minusculas).
	 *
	 * @throws IllegalArgumentException
	 *             Caso a {@code methodClass} seja abstrata, ou nao tenha um
	 *             construtor visivel.
	 *
	 * @throws IllegalArgumentException
	 *             Caso {@code methodName} seja nulo ou vazio.
	 */
	void registerMethod( Class<?> methodClass, String methodName );

	/**
	 * @param instance
	 *            Objeto em que o metodo esta definido.
	 * @param methodName
	 *            Nome do metodo a ser registrado.
	 * 
	 * @see #registerMethod(Class, String)
	 */
	void registerMethod( Object instance, String methodName );

	/**
	 * Registrar todos os comandos no plugin.
	 */
	void registerAll();

	/**
	 * Registra todos os metodos que contem a anotacao {@link Command} e seguem
	 * a assinatura {@link #registerMethod(Class, String) correta}.
	 * 
	 * @param klass
	 *            Classe que deseja registrar os comandos.
	 * 
	 * @see #registerMethod(Class, String)
	 */
	void registerAll( Class<?> klass );

	/**
	 * Pega um comando pela classe.
	 * 
	 * @param klass
	 *            Classe do comando.
	 * @return {@link Optional#empty()} caso nao exista um comando com essa
	 *         classe, senao retorna um {@link Optional} contendo a instancia do
	 *         comando.
	 */
	<T extends CommandBase> Optional<T> getCommandByClass( Class<T> klass );

	/**
	 * Pega um comando pelo nome.
	 * 
	 * @param name
	 *            Nome do comando.
	 * @return {@link Optional#empty()} caso nao exista um comando com esse
	 *         nome, senao retorna um {@link Optional} contendo a instancia do
	 *         comando.
	 */
	Optional<CommandBase> getCommandByName( String name );

	/**
	 * @return O plugin que passado por parametro no
	 *         {@link #newInstance(Plugin)}
	 */
	Plugin getOwner();

	/**
	 * Retorna uma nova instancia da implementacao dessa interface.
	 * 
	 * @param plugin
	 *            O plugin que est� rodando.
	 * 
	 * @return Uma nova instancia da implementacao dessa interface.
	 * 
	 * @throws NullPointerException
	 *             Caso o {@code plugin} seja nulo.
	 */
	static CommandManager newInstance( final Plugin plugin )
	{
		return new CommandManagerImpl( Preconditions.checkNotNull( plugin, "plugin cannot be null" ) );
	}
}
