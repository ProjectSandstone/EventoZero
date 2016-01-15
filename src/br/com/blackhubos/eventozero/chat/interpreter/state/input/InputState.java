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

import java.util.function.BiConsumer;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;

/**
 * A classe InputState é uma monitora (não monitora final, somente monitora). Sua função é monitorar
 * todas entradas, tanto as válidas quanto as invalidas, que são classificadas como válidas que
 * chamam o método {@link #ok} e inválida que chamam o método {@link #error(BiConsumer)}, sua função
 * é simples, informar qual é a entrada correta, ou informar que a respota que ele deu é correta. A
 * classe também pode ser utilizada para monitorar todas as respostas, tanto invalidas como validas
 * e guardar-las, porém, esta não é sua proposta.
 *
 * Veja mais sobre classes avaliadoras, monitoras e monitoras finais da questão aqui: {@link
 * br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public interface InputState<T, ID> {
    /**
     * Define o consumidor de entrada válida
     *
     * @param okConsumer Consumidor
     * @return O proprio InputState
     */
    InputState<T, ID> ok(BiConsumer<Player, String> okConsumer);

    /**
     * Define o consumidor de entrada invalida
     *
     * @param errorConsumer Consumidor
     * @return O proprio InputState
     */
    InputState<T, ID> error(BiConsumer<Player, String> errorConsumer);

    /**
     * Obtém a questão relacionada
     *
     * @return Obtém a questão relacionada
     */
    Question<T, ID> endDefinition();
}
