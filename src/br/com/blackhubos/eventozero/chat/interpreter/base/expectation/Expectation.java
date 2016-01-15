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
package br.com.blackhubos.eventozero.chat.interpreter.base.expectation;

import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;

/**
 * Expectation é uma classe avaliadora. Sendo uma classe avaliadora ela define quando a resposta é
 * válida ou não, permitindo um melhor controle do que se pode ser passado como resposta. Apesar de
 * ja se ter a classe {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern}
 * responsável por 2 tipos de avaliações, no qual 1 destes inclui avaliação das respostas, a classe
 * {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern} tem como função principal
 * agregar transformadores e avaliadores mais complexos, apesar de também agregar os principais. Já
 * a classe Expection agregar adaptações dos avaliadores do {@link br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern}
 *
 * Expectation chama o método {@link #True(BiFunction)} caso o resposta seja válido e {@link
 * #False(BiFunction)} caso a mesma seja inválida, seus resultados são {@link State} que dizem se
 * deve continuar mesmo não valiando a resposta, ou não continuar mesmo validando, por padrão, não
 * continuará com respostas invalidas e continuará com as validas.
 *
 * Veja mais sobre classes avaliadoras, monitoras e monitoras finais da questão aqui: {@link
 * br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public interface Expectation<T, ID> {

    /**
     * Função True chamada quando o valor é o esperado.
     *
     * Deve retornar um {@link State}, que irá indicar se deve continuar ou não mesmo que tenha
     * recebido o valor esperado
     *
     * @return Proprio Expectation
     */
    Expectation<T, ID> True(BiFunction<Player, T, State> ifTrue);

    /**
     * Função False chamada quando o valor não é o esperado.
     *
     * Deve retornar um {@link State}, que irá indicar se deve continuar ou não mesmo que não tenha
     * recebido o valor esperado
     *
     * @return Proprio Expectation
     */
    Expectation<T, ID> False(BiFunction<Player, T, State> ifFalse);

    /**
     * Obtém a questão relacionada
     *
     * @return Obtém a questão relacionada
     */
    Question<T, ID> endDefinition();

    /**
     * Estado
     */
    enum State {
        /**
         * Continuar o questionario
         */
        CONTINUE,
        /**
         * Impedir que ele continue, repetindo a mesma questão
         */
        BREAK
    }
}
