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

import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;

public class AnswerState {
    private final Optional<QuestionBase> next;
    private final State state;

    public AnswerState(Optional<QuestionBase> next, State state) {
        this.next = next;
        this.state = state;
    }

    public Optional<QuestionBase> getNext() {
        return next;
    }

    public State getState() {
        return state;
    }

    public enum State {
        OK,
        INVALID_ANSWER_FORMAT,
        NO_MORE_QUESTIONS,
        NO_CURRENT_QUESTION
    }
}
