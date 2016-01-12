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

import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;

public class InterpreterData {
    private final Interpreter interpreter;
    private final Deque<QuestionBase> questionBaseList;
    private final Map<QuestionBase, Object> answers = new HashMap<>();
    private final BiConsumer<Player, AnswerData> endListener;
    private QuestionBase current;

    public InterpreterData(Interpreter interpreter, Deque<QuestionBase> questionBaseList, BiConsumer<Player, AnswerData> endListener) {
        this.interpreter = interpreter;
        this.questionBaseList = questionBaseList;
        this.endListener = endListener;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public QuestionBase next() {
        return current = questionBaseList.pollFirst();
    }

    public QuestionBase getCurrent() {
        return current;
    }

    public void setCurrent(QuestionBase current) {
        this.current = current;
    }

    public QuestionBase answer(Object answer) {
        answers.put(getCurrent(), answer);
        return getCurrent();
    }

    public boolean remove(QuestionBase questionBase) {
        return questionBaseList.remove(questionBase);
    }

    public boolean hasNext() {
        return !questionBaseList.isEmpty();
    }

    public void callEnd(Player player) {
        this.endListener.accept(player, new AnswerData(getAnswers()));
    }

    public Map<QuestionBase, Object> getAnswers() {
        Map<QuestionBase, Object> answersCopy = new HashMap<>(answers);
        return answersCopy;
    }

}
