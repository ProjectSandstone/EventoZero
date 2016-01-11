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
package br.com.blackhubos.eventozero.chat.interpreter.data;

import java.util.Map;
import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;

public class AnswerData {
    private final Map<QuestionBase, Object> answers;

    public AnswerData(Map<QuestionBase, Object> answers) {
        this.answers = answers;
    }

    public Optional<Object> getAnswer(String id) {
        for (QuestionBase questionBase : answers.keySet()) {
            if (questionBase.id().equals(id)) {
                return Optional.of(answers.get(questionBase));
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getAnswerAs(String id) {
        Optional<Object> answer;
        if ((answer = getAnswer(id)).isPresent()) {
            return Optional.of((T) answer.get());
        }
        return Optional.empty();
    }
}
