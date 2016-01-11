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

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.question.QuestionImpl;
import br.com.blackhubos.eventozero.chat.interpreter.data.AnswerData;
import br.com.blackhubos.eventozero.chat.interpreter.data.InterpreterData;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;

public class Interpreter {

    private static final List<Interpreter> interpreters = new ArrayList<>();
    private static final Map<Player, InterpreterData> playerInterpreter = new HashMap<>();

    private final Deque<QuestionBase> registeredQuestion = new LinkedList<>();

    private final String id;

    public Interpreter(String id) {
        this.id = id;
        interpreters.add(this);
    }

    public static Optional<Interpreter> getPlayerCurrent(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();
        return Optional.of(playerInterpreter.get(player).getInterpreter());
    }

    public static Optional<Interpreter> getById(String id) {
        for (Interpreter interpreter : interpreters) {
            if (interpreter.id.equals(id)) {
                return Optional.of(interpreter);
            }
        }
        return Optional.empty();
    }

    public <T> Question<T> question(String id, String question, IPattern<T> pattern) {
        Question<T> questionAdd = new QuestionImpl<T>(id, question, pattern, this);
        registeredQuestion.offerLast(questionAdd);
        return questionAdd;
    }

    public boolean apply(Player player) {
        if (playerInterpreter.containsKey(player))
            return false;

        Deque<QuestionBase> copy = new LinkedList<>(registeredQuestion);
        InterpreterData interpreterData = new InterpreterData(this, copy);

        playerInterpreter.put(player, interpreterData);

        Optional<QuestionBase> baseOptional = next(player);
        if (baseOptional.isPresent()) {
            player.sendMessage(baseOptional.get().question());
        } else {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public Optional<QuestionBase> answer(Player player, String answer) {
        Optional<QuestionBase> baseOptional = current(player);
        if (baseOptional.isPresent()) {
            if (!baseOptional.get().isOk(answer)) {
                return Optional.empty();
            } else {
                QuestionBase questionBase = playerInterpreter.get(player).answer(baseOptional.get().transform(answer));
                return questionBase.next(player, answer);
            }
        } else {
            return Optional.empty();
        }
    }

    protected Optional<QuestionBase> current(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();
        return Optional.of(playerInterpreter.get(player).getCurrent());
    }

    protected Optional<QuestionBase> next(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();

        return Optional.of(playerInterpreter.get(player).next());
    }

    protected Optional<QuestionBase> deque(Player player) {
        return next(player);
    }

    protected boolean remove(Player player, QuestionBase questionBase) {
        return playerInterpreter.containsKey(player) && playerInterpreter.get(player).remove(questionBase);

    }

    public Optional<AnswerData> end(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();
        AnswerData data = new AnswerData(playerInterpreter.get(player).getAnswers());
        playerInterpreter.remove(player);

        return Optional.of(data);
    }

}
