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
package br.com.blackhubos.eventozero.chat.interpreter.base.impl;

import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import com.google.common.base.MoreObjects;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResultBase;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.InputState;

/**
 * Implementação do QuestionBase
 *
 * @param <T>  Tipo da resposta
 * @param <ID> Tipo do ID
 */
public class QuestionImpl<T, ID> implements QuestionBase<T, ID> {

    private final ID id;
    private final String question;
    private final IPattern<T> pattern;
    private final Interpreter interpreter;

    private Optional<Expectation<T, ID>> expect = Optional.empty();
    private Optional<InputState<T, ID>> inputState = Optional.empty();
    private Optional<BooleanResult<T, ID>> booleanResult = Optional.empty();

    /**
     * Construtor de QuestionBase
     *
     * @param id          Id da question para obter posteriormente
     * @param question    Questão
     * @param pattern     Pattern/Modelo que a resposta deve seguir, também será o que irá
     *                    transformar a respota em valor
     * @param interpreter Interpretador
     */
    public QuestionImpl(ID id, String question, IPattern<T> pattern, Interpreter interpreter) {
        this.id = id;
        this.question = question;
        this.pattern = pattern;
        this.interpreter = interpreter;
    }


    @Override
    public ID id() {
        return this.id;
    }

    @Override
    public String question() {
        return this.question;
    }

    @Override
    public IPattern<T> pattern() {
        return this.pattern;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<QuestionBase> getQuestion(boolean approved) {
        if (getBooleanResult().isPresent()) {
            if (approved)
                return ((BooleanResultBase) booleanResult.get()).getYesQuestion();
            else
                return ((BooleanResultBase) booleanResult.get()).getNoQuestion();
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<BiConsumer<Player, T>> getConsumer(boolean approved) {
        if (getBooleanResult().isPresent()) {
            if (approved)
                return ((BooleanResultBase) getBooleanResult().get()).getYesConsumer();
            else
                return ((BooleanResultBase) getBooleanResult().get()).getNoConsumer();
        }
        return Optional.empty();
    }

    @Override
    public Optional<BooleanResult<T, ID>> getBooleanResult() {
        return this.booleanResult;
    }

    @Override
    public void setBooleanResult(BooleanResult<T, ID> booleanResult) {
        this.booleanResult = Optional.of(booleanResult);
    }

    @Override
    @Nonnull
    public Interpreter getInterpreter() {
        return this.interpreter;
    }

    @Override
    public Optional<Expectation<T, ID>> getExpect() {
        return this.expect;
    }

    @Override
    public void setExpect(Expectation<T, ID> expect) {
        this.expect = Optional.of(expect);
    }

    @Override
    public Optional<InputState<T, ID>> getInputState() {
        return inputState;
    }

    @Override
    public void setInputState(InputState<T, ID> state) {
        this.inputState = Optional.of(state);
    }

    @Override
    public String toText(Object value) {
        return MoreObjects.toStringHelper(this)
                .add("id", id())
                .add("question", question())
                .add("pattern", pattern().toText(value))
                .toString();
    }

}
