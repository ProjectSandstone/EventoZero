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
package br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult;

import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;

/**
 * BooleanResult é uma classe 'monitora final' da questão Ela é monitora final, chamando os métodos
 * {@link #yes} e {@link #no} dependendo do resultado da avaliação Os resultados dela sobscreve os
 * resultados do {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern}, porém, para
 * obter os valores do IPattern é necessário chamar o {@link Question#booleanResult()} vazio.
 *
 * Veja mais sobre classes avaliadoras, monitoras e monitoras finais da questão aqui: {@link
 * br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public interface BooleanResult<T, ID> {
    /**
     * @see Result#YES
     */
    Result YES = Result.YES;
    /**
     * @see Result#NO
     */
    Result NO = Result.NO;

    /**
     * Aplica o valor e obtém o resultado
     *
     * @param value Valor
     * @return Resultado da chamada
     */
    Result apply(T value);

    /**
     * Método yes vazio, não define nada!
     *
     * @return Proprio BooleanResult
     * @see #yes(BiConsumer) Definição do consumidor que será chamado caso a função retorne yes
     * @see #yes(Question) Definição da questão que será considerada como a próxima (alternativa)
     * caso a função retorne yes
     * @see #yes(Question, BiConsumer) Define ambos, consumidor e questão caso a função retorne yes
     */
    default BooleanResult<T, ID> yes() {
        return this;
    }

    /**
     * Método no vazio, não define nada!
     *
     * @return Proprio BooleanResult
     * @see #no(BiConsumer) Definição do consumidor que será chamado caso a função retorne no
     * @see #no(Question) Definição da questão que será considerada como a próxima (alternativa)
     * caso a função retorne no
     * @see #no(Question, BiConsumer) Define ambos, consumidor e questão caso a função retorne no
     */
    default BooleanResult<T, ID> no() {
        return this;
    }

    /**
     * Define qual será a próxima questão caso as afirmações sejam {@link
     * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     *
     * @param ifYes Próxima questão caso as afirmações sejam {@link br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     * @return Proprio BooleanResult
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see Question#booleanResult(Function)
     */
    BooleanResult<T, ID> yes(Question ifYes);

    /**
     * Define qual será a próxima questão caso as afirmações sejam {@link
     * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     *
     * @param ifNo Próxima questão caso as afirmações sejam {@link br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     * @return Proprio BooleanResult
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see Question#booleanResult(Function)
     */
    BooleanResult<T, ID> no(Question ifNo);

    /**
     * Define qual será o consumidor que será chamado caso as afirmações sejam {@link
     * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     *
     * @param ifYesConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                      br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     * @return Proprio BooleanResult
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see Question#booleanResult(Function)
     */
    BooleanResult<T, ID> yes(BiConsumer<Player, T> ifYesConsumer);

    /**
     * Define qual será o consumidor que será chamado caso as afirmações sejam {@link
     * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     *
     * @param ifNoConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                     br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     * @return Proprio BooleanResult
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see Question#booleanResult(Function)
     */
    BooleanResult<T, ID> no(BiConsumer<Player, T> ifNoConsumer);

    /**
     * Define ambos, consumidor e questão caso as afirmações sejam {@link
     * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     *
     * @param ifYes         Próxima questão caso as afirmações sejam {@link br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     * @param ifYesConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                      br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES}
     * @return Proprio BooleanResult
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#YES
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see Question#booleanResult(Function)
     */
    BooleanResult<T, ID> yes(Question ifYes, BiConsumer<Player, T> ifYesConsumer);

    /**
     * Define ambos, consumidor e questão caso as afirmações sejam {@link
     * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     *
     * @param ifNo         Próxima questão caso as afirmações sejam {@link br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     * @param ifNoConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                     br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO}
     * @return Proprio BooleanResult
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult.Result#NO
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see Question#booleanResult(Function)
     */
    BooleanResult<T, ID> no(Question ifNo, BiConsumer<Player, T> ifNoConsumer);

    /**
     * Retorna a questão novamente
     *
     * @return Retorna a questão
     */
    Question<T, ID> endDefinition();

    /**
     * Resultado
     */
    enum Result {
        /**
         * Chama o método .yes
         */
        YES,
        /**
         * Chama o método .no
         */
        NO
    }

}
