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

public interface Question<T> extends QuestionBase<T> {


    // Layout methods
    @SuppressWarnings("unchecked")
    default Question<T> expect(Predicate<T> expect) {
        setExpect(expect);
        return this;
    }

    default Question<T> yesOrNoIf(Function<YesOrNo, T> predicate) {
        setYesOrNoIf(predicate);
        return this;
    }

    default Question<T> yes() {
        return this;
    }

    default Question<T> no() {
        return this;
    }

    default Question<T> yes(QuestionBase ifYes) {
        setYes(ifYes);
        return this;
    }

    default Question<T> no(QuestionBase ifNo) {
        setNo(ifNo);
        return this;
    }

    @SuppressWarnings("unchecked")
    default Question<T> yes(BiConsumer<Player, T> ifYesConsumer) {
        setYes(ifYesConsumer);
        return this;
    }

    @SuppressWarnings("unchecked")
    default Question<T> no(BiConsumer<Player, T> ifNoConsumer) {
        setNo(ifNoConsumer);
        return this;
    }

    @SuppressWarnings("unchecked")
    default Question<T> yes(QuestionBase ifYes, BiConsumer<Player, T> ifYesConsumer) {
        setYes(ifYes, ifYesConsumer);
        return this;
    }

    @SuppressWarnings("unchecked")
    default Question<T> no(QuestionBase ifYes, BiConsumer<Player, T> ifNoConsumer) {
        setNo(ifYes, ifNoConsumer);
        return this;
    }

}
