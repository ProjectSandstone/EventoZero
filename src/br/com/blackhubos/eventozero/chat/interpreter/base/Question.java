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

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.InputState;

/**
 * Interface da questão
 *
 * @param <T> Tipo da resposta
 */
public interface Question<T, ID> {


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
    Expectation<T, ID> expect(Predicate<T> expect);

    /**
     * Com este método você poderá definir quais valores serão considerados baseado na sua
     * avaliação, este resultado subscreve o resultado do {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern#booleanResult(Object)},
     * os valores considerados são os definidos no método {@link BooleanResult#yes()} e {@link
     * BooleanResult#no()}
     *
     * @return Própria questão
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult#yes(BiConsumer)
     * Definição do consumidor que será chamado caso a função retorne yes
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult#yes(Question)
     * Definição da questão que será considerada como a próxima (alternativa) caso a função retorne
     * yes
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult#yes(Question,
     * BiConsumer) Define ambos, consumidor e questão caso a função retorne yes
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult#no(BiConsumer)
     * Definição do consumidor que será chamado caso a função retorne no
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult#no(Question)
     * Definição da questão que será considerada como a próxima (alternativa) caso a função retorne
     * no
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult#no(Question,
     * BiConsumer) Define ambos, consumidor e questão caso a função retorne no
     */
    BooleanResult<T, ID> booleanResult(Function<T, BooleanResult.Result> booleanResult);

    /**
     * Mesma função do {@link #booleanResult(Function)}, porém, utilizado para obter os valores do
     * {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern}
     *
     * @return Boolean result
     */
    BooleanResult<T, ID> booleanResult();

    /**
     * Gerenciador de entrada.
     *
     * O método {@link InputState#ok(BiConsumer)} é chamado quando a resposta é válida
     *
     * O método {@link InputState#error(BiConsumer)} é chamado quando a resposta é inválida
     *
     * @return Gerenciador de entrada
     */
    InputState<T, ID> inputState();

    /**
     * Converte para texto baseado em um valor
     *
     * @param value Valor que será tido com o Tipo do {@link br.com.blackhubos.eventozero.chat.interpreter.values.ValueTransformer}
     * @return Texto
     */
    String toText(Object value);
}
