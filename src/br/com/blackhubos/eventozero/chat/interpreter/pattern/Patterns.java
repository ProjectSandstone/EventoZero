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

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.blackhubos.eventozero.chat.interpreter.base.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.values.ListTransformer;
import br.com.blackhubos.eventozero.util.Framework;

/**
 * Avaliadores padrões
 */
public final class Patterns {
    /**
     * Avaliador global para textos
     */
    public static final IPattern<String> ALL = new IPattern<>(value -> true, java.lang.String::valueOf);

    /**
     * Avaliador para booleanos
     */
    public static final IPattern<Boolean> Boolean = new IPattern<>(Framework::tryBoolean, Framework::getBoolean, value -> value ? BooleanResult.YES : BooleanResult.NO);

    /**
     * Avaliador para numeros
     */
    public static final IPattern<Integer> Integer = new IPattern<>(Pattern.compile("\\-?[0-9]+"), java.lang.Integer::parseInt);

    /**
     * Avaliador para double
     */
    public static final IPattern<Double> Double = new IPattern<>(Pattern.compile("\\-?[0-9]+[.]?[0-9]+"), java.lang.Double::parseDouble);

    /**
     * Tradutor de listas de Strings (pode conter qualquer caractere)
     */
    public static final IPattern<List<String>> StringList = new IPattern<>(value -> true, ListTransformer.STRING_LIST_TRANSFORMER);

    // Avaliadores menos simples


    /**
     * Avaliador de datas (Tipo: {@link LocalDate})
     */
    public static final IPattern<LocalDate> Date = new IPattern<>(Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{4})"), input -> {
        Matcher matcher = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{4})").matcher(input);
        if (matcher.matches()) {
            return LocalDate.of(java.lang.Integer.parseInt(matcher.group(3)),
                    java.lang.Integer.parseInt(matcher.group(2)),
                    java.lang.Integer.parseInt(matcher.group(1)));
        } else {
            return null;
        }

    });


}
