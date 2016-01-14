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
package br.com.blackhubos.eventozero.chat.interpreter.game.register;

import org.bukkit.plugin.Plugin;

import br.com.blackhubos.eventozero.chat.interpreter.game.listener.InterpreterListener;

/**
 * Para melhorar a organização esta classe registrará tudo relacionado ao interpreter
 */
public class InterpreterRegister
{

	/**
	 * Registra as questions e os listeners
	 */
	public static void registerAll(final Plugin plugin)
	{
		InterpreterRegister.registerQuestions(plugin);
		InterpreterRegister.registerListeners(plugin);
	}

	/**
	 * Registrar as questões
	 */
	private static void registerQuestions(final Plugin plugin)
	{

	}

	/**
	 * Registrar os listeners
	 */
	private static void registerListeners(final Plugin plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(new InterpreterListener(), plugin);
	}

}
