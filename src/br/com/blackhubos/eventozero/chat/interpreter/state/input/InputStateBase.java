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
package br.com.blackhubos.eventozero.chat.interpreter.state.input;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;

import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;

/**
 * Classe #Hidden de extensão a InputState
 *
 * Veja sobre as classes Hidden: {@link br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public interface InputStateBase<T, ID> extends InputState<T, ID> {

    /**
     * Chama os métodos do avaliado
     *
     * @param player Jogador
     * @param answer Reposta
     * @param state  Estado
     */
    default void call(Player player, String answer, AnswerResult.State state) {
        switch (state) {
            case INVALID_ANSWER_FORMAT: {
                getError().ifPresent(error -> error.accept(player, answer));
                break;
            }

            case OK:
            case NO_MORE_QUESTIONS: {
                getOk().ifPresent(ok -> ok.accept(player, answer));
                break;
            }
        }
    }

    /**
     * Obtém o consumidor de entrada válida
     *
     * @return Obtém o consumidor de entrada válida
     */
    Optional<BiConsumer<Player, String>> getOk();

    /**
     * Obtém o consumidor de entrada invalida
     *
     * @return Obtém o consumidor de entrada invalida
     */
    Optional<BiConsumer<Player, String>> getError();
}
