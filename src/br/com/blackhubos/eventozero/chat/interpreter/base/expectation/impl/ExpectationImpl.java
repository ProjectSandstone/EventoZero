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
package br.com.blackhubos.eventozero.chat.interpreter.base.expectation.impl;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.ExpectationBase;

/**
 * Implementação do Expectation
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public class ExpectationImpl<T, ID> implements ExpectationBase<T, ID> {
    private final Predicate<T> predicate;
    private final Question<T, ID> question;
    private Optional<BiFunction<Player, T, State>> trueOpt = Optional.empty();
    private Optional<BiFunction<Player, T, State>> falseOpt = Optional.empty();

    /**
     * Cria um novo Expectation
     *
     * @param predicate Predicado para verificar
     * @param question  Questão relacionada
     */
    public ExpectationImpl(Predicate<T> predicate, Question<T, ID> question) {
        this.predicate = predicate;
        this.question = question;
    }

    @Override
    public Predicate<T> getPredicate() {
        return this.predicate;
    }

    @Override
    public Optional<BiFunction<Player, T, State>> getIfTrue() {
        return trueOpt;
    }

    @Override
    public Optional<BiFunction<Player, T, State>> getIfFalse() {
        return falseOpt;
    }

    @Override
    public Expectation<T, ID> True(BiFunction<Player, T, State> ifTrue) {
        trueOpt = Optional.of(ifTrue);
        return this;
    }

    @Override
    public Expectation<T, ID> False(BiFunction<Player, T, State> ifFalse) {
        falseOpt = Optional.of(ifFalse);
        return this;
    }

    @Override
    public Question<T, ID> endDefinition() {
        return this.question;
    }
}
