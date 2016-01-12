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
package br.com.blackhubos.eventozero.chat.interpreter.base.question;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import br.com.blackhubos.eventozero.chat.interpreter.base.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;

/**
 * Implementação do QuestionBase
 *
 * @param <T> Tipo da resposta
 */
public class QuestionImpl<T> implements QuestionBase<T> {

    private final String id;
    private final String question;
    private final IPattern<T> pattern;
    private final Interpreter interpreter;

    private Optional<QuestionBase> yesNext = Optional.empty();
    private Optional<QuestionBase> noNext = Optional.empty();

    private Optional<BiConsumer<Player, T>> yesConsumer = Optional.empty();
    private Optional<BiConsumer<Player, T>> noConsumer = Optional.empty();

    private Optional<Predicate<T>> expect = Optional.empty();
    private Optional<Function<T, BooleanResult>> booleanResultFunction = Optional.empty();

    /**
     * Construtor de QuestionBase
     *
     * @param id          Id da question para obter posteriormente
     * @param question    Questão
     * @param pattern     Pattern/Modelo que a resposta deve seguir, também será o que irá
     *                    transformar a respota em valor
     * @param interpreter Interpretador
     */
    public QuestionImpl(String id, String question, IPattern<T> pattern, Interpreter interpreter) {
        this.id = id;
        this.question = question;
        this.pattern = pattern;
        this.interpreter = interpreter;
    }


    @Override
    public String id() {
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

    @Override
    public void setYes(QuestionBase questionBase) {
        this.yesNext = Optional.of(questionBase);
    }

    @Override
    public void setNo(QuestionBase questionBase) {
        this.noNext = Optional.of(questionBase);
    }

    @Override
    public void setYes(BiConsumer<Player, T> ifYesConsumer) {
        this.yesConsumer = Optional.of(ifYesConsumer);
    }

    @Override
    public void setNo(BiConsumer<Player, T> ifNoConsumer) {
        this.noConsumer = Optional.of(ifNoConsumer);
    }

    @Override
    public void setYes(QuestionBase ifYes, BiConsumer<Player, T> ifYesConsumer) {
        this.setYes(ifYes);
        this.setYes(ifYesConsumer);
    }

    @Override
    public void setNo(QuestionBase ifNo, BiConsumer<Player, T> ifNoConsumer) {
        this.setNo(ifNo);
        this.setNo(ifNoConsumer);
    }

    @Override
    public Optional<QuestionBase> getQuestion(boolean approved) {
        if (approved)
            return yesNext;
        else
            return noNext;
    }

    @Override
    public Optional<BiConsumer<Player, T>> getConsumer(boolean approved) {
        if (approved)
            return yesConsumer;
        else
            return noConsumer;
    }

    @Override
    public Optional<Function<T, BooleanResult>> getBooleanResult() {
        return booleanResultFunction;
    }

    @Override
    public void setBooleanResult(Function<T, BooleanResult> function) {
        booleanResultFunction = Optional.of(function);
    }

    @Override
    public Interpreter getInterpreter() {
        return this.interpreter;
    }

    @Override
    public Optional<Predicate<T>> getExpect() {
        return this.expect;
    }

    @Override
    public void setExpect(Predicate<T> expect) {
        this.expect = Optional.of(expect);
    }
}
