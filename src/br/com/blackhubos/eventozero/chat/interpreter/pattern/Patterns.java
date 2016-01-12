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
package br.com.blackhubos.eventozero.chat.interpreter.pattern;

import java.util.regex.Pattern;

import br.com.blackhubos.eventozero.util.Framework;

public class Patterns {

    public static final IPattern<String> ALL = new IPattern<>(value -> true, java.lang.String::valueOf);
    public static final IPattern<Boolean> Boolean = new IPattern<>(Framework::tryBoolean, Framework::getBoolean);
    public static final IPattern<Integer> Integer = new IPattern<>(Pattern.compile("\\-?[0-9]+"), java.lang.Integer::parseInt);

}
