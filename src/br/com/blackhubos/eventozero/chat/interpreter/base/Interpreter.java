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
import java.util.function.BiConsumer;

import br.com.blackhubos.eventozero.chat.interpreter.base.question.QuestionImpl;
import br.com.blackhubos.eventozero.chat.interpreter.data.AnswerData;
import br.com.blackhubos.eventozero.chat.interpreter.data.InterpreterData;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;

public class Interpreter {

    private static final List<Interpreter> interpreters = new ArrayList<>();
    private static final Map<Player, InterpreterData> playerInterpreter = new HashMap<>();

    private final Deque<QuestionBase> registeredQuestion = new LinkedList<>();

    private final String id;

    public Interpreter(String id) {
        this.id = id;
        interpreters.add(this);
    }

    public static Optional<Interpreter> getCurrent(Player player) {
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
        Question<T> questionAdd = alternativeQuestion(id, question, pattern);
        registeredQuestion.offerLast((QuestionBase) questionAdd);
        return questionAdd;
    }

    public <T> Question<T> alternativeQuestion(String id, String question, IPattern<T> pattern) {
        return new QuestionImpl<T>(id, question, pattern, this);
    }

    public boolean apply(Player player, BiConsumer<Player, AnswerData> endListener) {
        if (playerInterpreter.containsKey(player))
            return false;

        Deque<QuestionBase> copy = new LinkedList<>(registeredQuestion);
        InterpreterData interpreterData = new InterpreterData(this, copy, endListener);

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
    public AnswerResult answer(Player player, String answer) {
        Optional<QuestionBase> baseOptional = current(player);
        if (baseOptional.isPresent()) {
            if (!baseOptional.get().isOk(answer)) {
                return new AnswerResult(Optional.empty(), AnswerResult.State.INVALID_ANSWER_FORMAT);
            } else {
                QuestionBase questionBase = playerInterpreter.get(player).answer(baseOptional.get().transform(answer));
                Optional<QuestionBase> next = questionBase.next(player, answer);
                if (next.isPresent()) {
                    player.sendMessage(next.get().question());
                } else {
                    return new AnswerResult(Optional.empty(), AnswerResult.State.NO_MORE_QUESTIONS);
                }
                return new AnswerResult(next, AnswerResult.State.OK);
            }
        } else {
            return new AnswerResult(Optional.empty(), AnswerResult.State.NO_CURRENT_QUESTION);
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

    public boolean hasNext(Player player) {
        if (!playerInterpreter.containsKey(player))
            return false;
        return playerInterpreter.get(player).hasNext();
    }

    public boolean end(Player player) {
        if (!playerInterpreter.containsKey(player))
            return false;
        playerInterpreter.get(player).callEnd(player);
        playerInterpreter.remove(player);
        return true;
    }

    protected boolean setCurrent(Player player, Optional<QuestionBase> question) {
        if (!playerInterpreter.containsKey(player))
            return false;
        playerInterpreter.get(player).setCurrent(question.get());
        return true;
    }
}
