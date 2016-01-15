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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResultBase;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.impl.BooleanResultImpl;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.ExpectationBase;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.impl.ExpectationImpl;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.InputState;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.InputStateBase;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.impl.InputStateImpl;
import br.com.blackhubos.eventozero.chat.interpreter.values.ValueTransformer;

/**
 * Classe #Hidden de extensão a Question
 *
 * Veja sobre as classes Hidden: {@link br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <Value_Type> Tipo dos valores
 * @param <ID>         Id da questão
 */
public interface QuestionBase<Value_Type, ID> extends Question<Value_Type, ID> {

    /**
     * Id da questão
     *
     * @return Id da questão
     */
    ID id();

    /**
     * Questão que será feita ao jogador
     *
     * @return Questão que será feita ao jogador
     */
    String question();

    /**
     * Pattern de avaliação das entradas
     *
     * @return Pattern de avaliação das entradas
     */
    IPattern<Value_Type> pattern();

    @Override
    default Expectation<Value_Type, ID> expect(Predicate<Value_Type> expect) {

        if (!getExpect().isPresent()) {
            setExpect(new ExpectationImpl<>(expect, this));
        }

        return getExpect().get();
    }

    @Override
    default BooleanResult<Value_Type, ID> booleanResult(Function<Value_Type, BooleanResult.Result> booleanResult) {
        if (!getBooleanResult().isPresent()) {
            setBooleanResult(new BooleanResultImpl<>(booleanResult, this));
        }

        return getBooleanResult().get();
    }

    @Override
    default BooleanResult<Value_Type, ID> booleanResult() {
        if (!getBooleanResult().isPresent()) {
            setBooleanResult(new BooleanResultImpl<>(this));
        }

        return getBooleanResult().get();
    }

    @Override
    default InputState<Value_Type, ID> inputState() {
        if (!getInputState().isPresent())
            setInputState(new InputStateImpl<>(this));
        return getInputState().get();
    }

    /**
     * Obtém a próxima questão baseado na aprovação
     *
     * @param approved Aprovação
     * @return Próxima questão correspondente a aprovação caso exista, ou {@link Optional#empty()}
     */
    Optional<QuestionBase> getQuestion(boolean approved);

    /**
     * Obtém o consumidor que deve ser chamado dependendo da aprovação
     *
     * @param approved Aprovação
     * @return Consumidor correspondente a aprovação caso exista, ou {@link Optional#empty()}
     */
    Optional<BiConsumer<Player, Value_Type>> getConsumer(boolean approved);

    /**
     * Obtém o BooleanResult
     *
     * @return Obtém o BooleanResult
     */
    Optional<BooleanResult<Value_Type, ID>> getBooleanResult();

    /**
     * Define a função que é responsável por definir o que chamará o usará as definições dos métodos
     * yes e no
     *
     * @param booleanResult Função que é responsável por definir quais as definições dos métodos yes
     *                      ou no serão consideradas
     * @see #booleanResult(Function)
     */
    void setBooleanResult(BooleanResult<Value_Type, ID> booleanResult);

    /**
     * Obtém o interpretador/questionário que está com a questão
     *
     * @return Interpretador
     */
    @Nonnull
    Interpreter getInterpreter();

    /**
     * Obtém o predicado de "espera de valor"
     *
     * @return O predicado de "espera de valor" caso exista, ou {@link Optional#empty()}
     * @see #expect(Predicate)
     */
    Optional<Expectation<Value_Type, ID>> getExpect();

    /**
     * Define o predicado de espera
     *
     * @param expect Predicado de espera
     * @see #expect(Predicate)
     */
    void setExpect(Expectation<Value_Type, ID> expect);

    /**
     * Obtem o gerenciador de Entrada
     *
     * @return Obtem o gerenciador de Entrada
     */
    Optional<InputState<Value_Type, ID>> getInputState();

    /**
     * Define o gerenciador de Entrada
     *
     * @param state Estado
     */
    void setInputState(InputState<Value_Type, ID> state);

    // Métodos funcionais, somente implemente caso saiba o que está fazendo

    /**
     * Obtém o conversor de valores, responsável por transformar texto em valores
     *
     * @return Obtém o conversor de valores, responsável por transformar texto em valores
     */
    default ValueTransformer<Value_Type> transformer() {
        return pattern().getTransformer();
    }


    /**
     * Determina se um texto pode ser aprovado
     *
     * @param input Texto
     * @return Determina se um texto pode ser aprovado
     */
    default boolean approve(Player player, String input) {
        if (getExpect().isPresent()) {
            ExpectationBase<Value_Type, ID> expectationBase = (ExpectationBase<Value_Type, ID>) getExpect().get();
            Expectation.State state = expectationBase.call(player, transform(input));
            switch (state) {
                case BREAK:
                    return false;
                case CONTINUE:
                    return true;
            }
        }
        return true;
    }

    /**
     * Transforma o texto em valor
     *
     * @param input Entrada
     * @return Valor convertido
     */
    default Value_Type transform(String input) {
        return transformer().transform(input);
    }

    /**
     * Determina se o texto pode ser aprovado, trabalha em conjunto com o método {@link
     * #approve(Player, String)}
     *
     * @param input Texto
     * @return Determina se um texto pode ser aprovado
     * @see #approve(Player, String)
     */
    default boolean isOk(Player player, String input) {
        return pattern().check(input) && approve(player, input);
    }

    /**
     * Obtém a próxima questão do jogador chamando o método do interpreter/questionário
     *
     * @param player Jogador
     * @return Próxima questão, ou {@link Optional#empty()} caso não haja mais questões
     */
    @SuppressWarnings("unchecked")
    default Optional<QuestionBase> interpreterNext(Player player) {
        return getInterpreter().deque(player);
    }

    /**
     * Remove uma questão do jogador
     *
     * @param player   Jogador
     * @param question Questão a ser removida
     * @return True caso remova, false caso não remova
     */
    default boolean remove(Player player, QuestionBase question) {
        return getInterpreter().remove(player, question);
    }

    /**
     * Determina se há próxima questão para o jogador
     *
     * @param player jogador
     * @return True se houver, false caso contrário
     */
    default boolean hasNext(Player player) {
        return getInterpreter().hasNext(player);
    }

    /**
     * Processa o texto, processa as definições yes e no, sincroniza as questões e obtém a próxima
     * questão para o jogador
     *
     * @param player     jogador
     * @param inputApply Texto
     * @return Próxima questão sincronizada, ou {@link Optional#empty()} caso não haja mais questões
     */
    default Optional<QuestionBase> processAndNext(Player player, String inputApply) {
        Value_Type value = transform(inputApply);

        boolean state;

        Optional<BooleanResult<Value_Type, ID>> booleanResult = getBooleanResult();
        if (booleanResult.isPresent() && ((BooleanResultBase) booleanResult.get()).getFunction().isPresent()) {
            state = booleanResult.get().apply(value) == BooleanResult.Result.YES;
        } else {
            state = pattern().booleanResult(value) == BooleanResult.Result.YES;
        }

        Optional<QuestionBase> question = getQuestion(state);
        Optional<BiConsumer<Player, Value_Type>> consumer = getConsumer(state);
        if (consumer.isPresent()) {
            consumer.get().accept(player, value);
        }

        if (question.isPresent()) {
            remove(player, question.get());
            getInterpreter().setCurrent(player, question);
            return question;
        } else {
            if (!hasNext(player)) {
                getInterpreter().end(player);
                return Optional.empty();
            } else {
                return interpreterNext(player);
            }
        }
    }

    /**
     * Processa a resposta
     *
     * @param player       Jogador
     * @param answer       Resposta
     * @param answerResult Resultado do gerenciador
     * @return true se encontrar um gerenciador de entrada, false caso contrario
     */
    default AnswerResult processAnswer(Player player, String answer, AnswerResult answerResult) {
        if (!getInputState().isPresent())
            return answerResult;
        ((InputStateBase) getInputState().get()).call(player, answer, answerResult.getState());
        return answerResult;
    }
}
