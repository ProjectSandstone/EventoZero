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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Classe #Hidden de extensão a Expectation
 *
 * Veja sobre as classes Hidden: {@link br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public interface ExpectationBase<T, ID> extends Expectation<T, ID> {

    /**
     * Testa o valor com o predicado
     *
     * @param value Valor
     * @return Resultado do teste com o predicado
     */
    default boolean test(T value) {
        return getPredicate().test(value);
    }

    /**
     * Chama os métodos relacionados com o valor
     *
     * @param player Jogador
     * @param value  Valor
     * @return Estado
     * @see br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation.State
     */
    default Expectation.State call(Player player, T value) {
        if (test(value)) {
            if (getIfTrue().isPresent())
                return getIfTrue().get().apply(player, value);
            return State.CONTINUE;
        } else {
            if (getIfFalse().isPresent())
                return getIfFalse().get().apply(player, value);
            return State.BREAK;
        }

    }

    /**
     * Obtem o predicado
     *
     * @return Obtem o predicado
     */
    Predicate<T> getPredicate();

    /**
     * Obtém a função True
     *
     * @return Obtém a função True
     */
    Optional<BiFunction<Player, T, State>> getIfTrue();

    /**
     * Obtém a função False
     *
     * @return Obtém a função False
     */
    Optional<BiFunction<Player, T, State>> getIfFalse();
}
