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
package br.com.blackhubos.eventozero.chat.interpreter.base;

import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Question<T> {


    // Layout methods
    Question<T> expect(Predicate<T> expect);

    Question<T> booleanResult(Function<T, BooleanResult> function);

    default Question<T> yes() {
        return this;
    }

    default Question<T> no() {
        return this;
    }

    Question<T> yes(Question ifYes);

    Question<T> no(Question ifNo);

    Question<T> yes(BiConsumer<Player, T> ifYesConsumer);

    Question<T> no(BiConsumer<Player, T> ifNoConsumer);

    Question<T> yes(Question ifYes, BiConsumer<Player, T> ifYesConsumer);

    Question<T> no(Question ifYes, BiConsumer<Player, T> ifNoConsumer);

}
