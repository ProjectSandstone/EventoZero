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
package br.com.blackhubos.eventozero.chat.interpreter.state;

import org.bukkit.entity.Player;

import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;

/**
 * Resultado das chamadas do método {@link br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter#answer(Player,
 * String)}
 */
public class AnswerResult {

    private final Optional<QuestionBase> next;
    private final State state;

    /**
     * Construtor do resultado
     *
     * @param next  Próxima questão, o resultado é relativo e dependente do State caso o gerenciador
     *              seja o {@link br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}.
     * @param state Estado
     */
    public AnswerResult(Optional<QuestionBase> next, State state) {
        this.next = next;
        this.state = state;
    }

    /**
     * Obtém a próxima questão
     *
     * @return Próxima questão
     */
    public Optional<QuestionBase> getNext() {
        return next;
    }

    /**
     * Obtém o estado
     *
     * @return O estado
     */
    public State getState() {
        return state;
    }

    public enum State {
        /**
         * Indica que tudo está certo para prosseguir
         */
        OK,

        /**
         * Indica que a resposta é invalida (não passou pelos testes)
         */
        INVALID_ANSWER_FORMAT,

        /**
         * Indica que não há mais questões/Fim do questionário
         */
        NO_MORE_QUESTIONS,

        /**
         * Indica que não há questão atual para o jogador
         */
        NO_CURRENT_QUESTION
    }
}
