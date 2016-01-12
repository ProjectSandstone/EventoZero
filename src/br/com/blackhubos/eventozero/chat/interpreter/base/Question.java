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

/**
 * Interface da questão
 *
 * @param <T> Tipo da resposta
 */
public interface Question<T> {


    // Layout methods

    /**
     * Método que indica "espera de valor", com este método você pode definir como espera que o
     * valor seja, se ele não for igual ao esperado, irá gerar um erro, geralmente o gerenciador irá
     * retornar {@link br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult.State#INVALID_ANSWER_FORMAT}
     * para indicar que o valor não era o esperado, sendo assim, terá que ser informado um valor que
     * seja o esperado!
     *
     * @param expect Lambda que indica a espera, veja os testes para saber melhor como é seu uso
     *               lógico.
     * @return Própria questão
     */
    Question<T> expect(Predicate<T> expect);

    /**
     * Com este método você poderá definir quais valores serão considerados baseado na sua
     * avaliação, este resultado subscreve o resultado do {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)},
     * os valores considerados são os definidos no método {@link #yes()} e {@link #no()}
     *
     * @param function Função(lambda) de avaliação, veja os testes para saber melhor como é seu uso
     *                 lógico.
     * @return Própria questão
     * @see #yes(BiConsumer) Definição do consumidor que será chamado caso a função retorne yes
     * @see #yes(Question) Definição da questão que será considerada como a próxima (alternativa)
     * caso a função retorne yes
     * @see #yes(Question, BiConsumer) Define ambos, consumidor e questão caso a função retorne yes
     * @see #no(BiConsumer) Definição do consumidor que será chamado caso a função retorne no
     * @see #no(Question) Definição da questão que será considerada como a próxima (alternativa)
     * caso a função retorne no
     * @see #no(Question, BiConsumer) Define ambos, consumidor e questão caso a função retorne no
     */
    Question<T> booleanResult(Function<T, BooleanResult> function);

    /**
     * Método yes vazio, não define nada!
     *
     * @return Própria questão
     * @see #yes(BiConsumer) Definição do consumidor que será chamado caso a função retorne yes
     * @see #yes(Question) Definição da questão que será considerada como a próxima (alternativa)
     * caso a função retorne yes
     * @see #yes(Question, BiConsumer) Define ambos, consumidor e questão caso a função retorne yes
     */
    default Question<T> yes() {
        return this;
    }

    /**
     * Método no vazio, não define nada!
     *
     * @return Própria questão
     * @see #no(BiConsumer) Definição do consumidor que será chamado caso a função retorne no
     * @see #no(Question) Definição da questão que será considerada como a próxima (alternativa)
     * caso a função retorne no
     * @see #no(Question, BiConsumer) Define ambos, consumidor e questão caso a função retorne no
     */
    default Question<T> no() {
        return this;
    }

    /**
     * Define qual será a próxima questão caso as afirmações sejam {@link BooleanResult#YES}
     *
     * @param ifYes Próxima questão caso as afirmações sejam {@link BooleanResult#YES}
     * @return Própria questão
     * @see BooleanResult#YES
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see #booleanResult(Function)
     */
    Question<T> yes(Question ifYes);

    /**
     * Define qual será a próxima questão caso as afirmações sejam {@link BooleanResult#NO}
     *
     * @param ifNo Próxima questão caso as afirmações sejam {@link BooleanResult#NO}
     * @return Própria questão
     * @see BooleanResult#NO
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see #booleanResult(Function)
     */
    Question<T> no(Question ifNo);

    /**
     * Define qual será o consumidor que será chamado caso as afirmações sejam {@link
     * BooleanResult#YES}
     *
     * @param ifYesConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                      BooleanResult#YES}
     * @return Própria questão
     * @see BooleanResult#YES
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see #booleanResult(Function)
     */
    Question<T> yes(BiConsumer<Player, T> ifYesConsumer);

    /**
     * Define qual será o consumidor que será chamado caso as afirmações sejam {@link
     * BooleanResult#NO}
     *
     * @param ifNoConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                     BooleanResult#NO}
     * @return Própria questão
     * @see BooleanResult#NO
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see #booleanResult(Function)
     */
    Question<T> no(BiConsumer<Player, T> ifNoConsumer);

    /**
     * Define ambos, consumidor e questão caso as afirmações sejam {@link BooleanResult#YES}
     *
     * @param ifYes         Próxima questão caso as afirmações sejam {@link BooleanResult#YES}
     * @param ifYesConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                      BooleanResult#YES}
     * @return Própria questão
     * @see BooleanResult#YES
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see #booleanResult(Function)
     */
    Question<T> yes(Question ifYes, BiConsumer<Player, T> ifYesConsumer);

    /**
     * Define ambos, consumidor e questão caso as afirmações sejam {@link BooleanResult#NO}
     *
     * @param ifNo         Próxima questão caso as afirmações sejam {@link BooleanResult#NO}
     * @param ifNoConsumer Consumidor que será chamado caso as afirmações sejam {@link
     *                     BooleanResult#NO}
     * @return Própria questão
     * @see BooleanResult#NO
     * @see br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)
     * @see #booleanResult(Function)
     */
    Question<T> no(Question ifNo, BiConsumer<Player, T> ifNoConsumer);

}
