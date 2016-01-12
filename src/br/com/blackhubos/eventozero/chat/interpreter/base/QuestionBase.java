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

import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;
import br.com.blackhubos.eventozero.chat.interpreter.values.ValueTransformer;

public interface QuestionBase<Value_Type> extends Question<Value_Type> {

    /**
     * Id da questão
     *
     * @return Id da questão
     */
    String id();

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

    // Override Methods

    @Override
    default Question<Value_Type> expect(Predicate<Value_Type> expect) {
        setExpect(expect);
        return this;
    }

    @Override
    default Question<Value_Type> booleanResult(Function<Value_Type, BooleanResult> function) {
        setBooleanResult(function);
        return this;
    }

    @Override
    default Question<Value_Type> yes(Question ifYes) {
        setYes((QuestionBase) ifYes);
        return this;
    }

    @Override
    default Question<Value_Type> no(Question ifNo) {
        setNo((QuestionBase) ifNo);
        return this;
    }

    @Override
    default Question<Value_Type> yes(BiConsumer<Player, Value_Type> ifYesConsumer) {
        setYes(ifYesConsumer);
        return this;
    }

    @Override
    default Question<Value_Type> no(BiConsumer<Player, Value_Type> ifNoConsumer) {
        setNo(ifNoConsumer);
        return this;
    }

    @Override
    default Question<Value_Type> yes(Question ifYes, BiConsumer<Player, Value_Type> ifYesConsumer) {
        setYes((QuestionBase) ifYes, ifYesConsumer);
        return this;
    }

    @Override
    default Question<Value_Type> no(Question ifNo, BiConsumer<Player, Value_Type> ifNoConsumer) {
        setNo((QuestionBase) ifNo, ifNoConsumer);
        return this;
    }


    // Set methods

    /**
     * Define as propriedades dos métodos yes
     *
     * @param questionBase Questão
     * @see #yes(Question)
     */
    void setYes(QuestionBase questionBase);

    /**
     * Define as propriedades dos métodos no
     *
     * @param questionBase Questão
     * @see #no(Question)
     */
    void setNo(QuestionBase questionBase);

    /**
     * Define as propriedades dos métodos yes
     *
     * @param ifYesConsumer Consumidor
     * @see #yes(BiConsumer)
     */
    void setYes(BiConsumer<Player, Value_Type> ifYesConsumer);

    /**
     * Define as propriedades dos métodos no
     *
     * @param ifNoConsumer Consumidor
     * @see #no(BiConsumer)
     */
    void setNo(BiConsumer<Player, Value_Type> ifNoConsumer);

    /**
     * Define as propriedades dos métodos yes
     *
     * @param ifYes         Questão
     * @param ifYesConsumer Consumidor
     * @see #yes(Question, BiConsumer)
     */
    void setYes(QuestionBase ifYes, BiConsumer<Player, Value_Type> ifYesConsumer);

    /**
     * Define as propriedades dos métodos no
     *
     * @param ifNo         Questão
     * @param ifNoConsumer Consumidor
     * @see #no(Question, BiConsumer)
     */
    void setNo(QuestionBase ifNo, BiConsumer<Player, Value_Type> ifNoConsumer);

    /**
     * Define a função que é responsável por definir o que chamará o usará as definições dos métodos
     * yes e no
     *
     * @param function Função que é responsável por definir quais as definições dos métodos yes ou
     *                 no serão consideradas
     * @see #booleanResult(Function)
     */
    void setBooleanResult(Function<Value_Type, BooleanResult> function);

    /**
     * Define o predicado de espera
     *
     * @param expect Predicado de espera
     * @see #expect(Predicate)
     */
    void setExpect(Predicate<Value_Type> expect);


    // Get methods

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
     * Obtém a função definida para avaliar os valores
     *
     * @return BooleanResult caso exista, ou {@link Optional#empty()}
     * @see #booleanResult(Function)
     */
    Optional<Function<Value_Type, BooleanResult>> getBooleanResult();

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
    Optional<Predicate<Value_Type>> getExpect();

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
    default boolean approve(String input) {
        return !getExpect().isPresent() || getExpect().get().test(transform(input));
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
     * #isOk(String)}
     *
     * @param input Texto
     * @return Determina se um texto pode ser aprovado
     * @see #isOk(String)
     */
    default boolean isOk(String input) {
        return pattern().check(input) && approve(input);
    }

    /**
     * Obtém a próxima questão do jogador chamando o método do interpreter/questionário
     *
     * @param player Jogador
     * @return Próxima questão, ou {@link Optional#empty()} caso não haja mais questões
     */
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

        boolean state = true; //state always = yes

        Optional<Function<Value_Type, BooleanResult>> yesOrNo = getBooleanResult();
        if (yesOrNo.isPresent()) {
            state = yesOrNo.get().apply(value) == BooleanResult.YES;
        } else {
            state = pattern().booleanResult(value) == BooleanResult.YES;
        }

        Optional<QuestionBase> question = getQuestion(state);
        Optional<BiConsumer<Player, Value_Type>> consumer = getConsumer(state);
        consumer.ifPresent(playerConsumer -> playerConsumer.accept(player, value));

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

}
