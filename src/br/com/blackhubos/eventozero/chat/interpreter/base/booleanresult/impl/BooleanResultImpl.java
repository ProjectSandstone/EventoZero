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
package br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.impl;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;
import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResultBase;

/**
 * Implementação do BooleanResult
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public class BooleanResultImpl<T, ID> implements BooleanResultBase<T, ID> {

    private final Optional<Function<T, Result>> function;
    private final Question<T, ID> question;
    private Optional<QuestionBase> yesNext = Optional.empty();
    private Optional<QuestionBase> noNext = Optional.empty();
    private Optional<BiConsumer<Player, T>> yesConsumer = Optional.empty();
    private Optional<BiConsumer<Player, T>> noConsumer = Optional.empty();

    /**
     * Cria um novo BooleanResuçt
     *
     * @param function Função de avaliação
     * @param question Questão relacionada
     */
    public BooleanResultImpl(Function<T, Result> function, Question<T, ID> question) {
        this.function = Optional.of(function);
        this.question = question;
    }

    public BooleanResultImpl(Question<T, ID> question) {
        this.function = Optional.empty();
        this.question = question;
    }

    @Override
    public BooleanResult<T, ID> yes(Question ifYes) {
        yesNext = Optional.of((QuestionBase) ifYes);
        return this;
    }

    @Override
    public BooleanResult<T, ID> no(Question ifNo) {
        noNext = Optional.of((QuestionBase) ifNo);
        return this;
    }

    @Override
    public BooleanResult<T, ID> yes(BiConsumer<Player, T> ifYesConsumer) {
        yesConsumer = Optional.of(ifYesConsumer);
        return this;
    }

    @Override
    public BooleanResult<T, ID> no(BiConsumer<Player, T> ifNoConsumer) {
        noConsumer = Optional.of(ifNoConsumer);
        return this;
    }

    @Override
    public BooleanResult<T, ID> yes(Question ifYes, BiConsumer<Player, T> ifYesConsumer) {
        yes(ifYes);
        yes(ifYesConsumer);
        return this;
    }

    @Override
    public BooleanResult<T, ID> no(Question ifNo, BiConsumer<Player, T> ifNoConsumer) {
        no(ifNo);
        no(ifNoConsumer);
        return this;
    }

    @Override
    public Question<T, ID> endDefinition() {
        return this.question;
    }

    @Override
    public Optional<QuestionBase> getYesQuestion() {
        return yesNext;
    }

    @Override
    public Optional<QuestionBase> getNoQuestion() {
        return noNext;
    }

    @Override
    public Optional<BiConsumer<Player, T>> getYesConsumer() {
        return yesConsumer;
    }

    @Override
    public Optional<BiConsumer<Player, T>> getNoConsumer() {
        return noConsumer;
    }

    @Override
    public Optional<Function<T, Result>> getFunction() {
        return this.function;
    }
}
