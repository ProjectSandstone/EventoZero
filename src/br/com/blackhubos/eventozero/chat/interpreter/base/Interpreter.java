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

/**
 * Interpretador, classe responsável por gerenciar tudo!
 *
 * Somente é permitido uma sessão de perguntas por jogador!
 */
public class Interpreter {

    private static final List<Interpreter> interpreters = new ArrayList<>();
    private static final Map<Player, InterpreterData> playerInterpreter = new HashMap<>();

    private final Deque<QuestionBase> registeredQuestion = new LinkedList<>();

    private final String id;

    /**
     * Construtor
     *
     * O interpretador é adicionado automaticamente ao ser construido
     *
     * @param id Id para obter o interpretador posteriormente
     */
    public Interpreter(String id) {
        this.id = id;
        interpreters.add(this);
    }

    /**
     * Obtém o interpretador atual do jogador
     *
     * @param player Jogador
     * @return {@link Optional} do interpretador atual do jogador
     */
    public static Optional<Interpreter> getCurrent(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();
        return Optional.of(playerInterpreter.get(player).getInterpreter());
    }

    /**
     * Obtem o interpreter atual do jogador e espera que ele seja um id especifico, se não for,
     * retorna {@link Optional#empty()}
     *
     * @param player Jogador
     * @param id     Id esperado
     * @return {@link Optional} do interpretador atual do jogador se ele for igual ao id
     */
    public static Optional<Interpreter> expectCurrent(Player player, String id) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();

        InterpreterData interpreterData = playerInterpreter.get(player);
        if (interpreterData.getInterpreter().getId().equals(id)) {
            return Optional.of(interpreterData.getInterpreter());
        }
        return Optional.empty();
    }

    /**
     * Obtém um interpretador pelo ID
     *
     * @param id ID do interpretador
     * @return {@link Optional} do interpretador correspondente ao ID
     */
    public static Optional<Interpreter> getById(String id) {
        for (Interpreter interpreter : interpreters) {
            if (interpreter.getId().equals(id)) {
                return Optional.of(interpreter);
            }
        }
        return Optional.empty();
    }

    /**
     * Cria uma nova questão e a salva na lista de questões!
     *
     * @param id       Id da nova questão
     * @param question Questão para ser perguntada ao jogador
     * @param pattern  IPattern para avaliar a resposta
     * @param <T>      Tipo do valor
     * @return Questão para definir as preferencias da mesma
     * @see #alternativeQuestion(String, String, IPattern) Caso deseje uma questão alternativa que
     * não será registrada
     * @see IPattern Verificador de respostas
     * @see Question Questão para definir as preferencias
     */
    public <T> Question<T> question(String id, String question, IPattern<T> pattern) {
        Question<T> questionAdd = alternativeQuestion(id, question, pattern);
        registeredQuestion.offerLast((QuestionBase) questionAdd);
        return questionAdd;
    }

    /**
     * Cria uma nova questão alternativa, que não será registrada, use ela se ela for ser definida
     * em uma outra
     *
     * @param id       Id da nova questão
     * @param question Questão para ser perguntada ao jogador
     * @param pattern  IPattern para avaliar a resposta
     * @param <T>      Tipo do valor
     * @return Questão para definir as preferencias da mesma
     * @see #question(String, String, IPattern) Caso deseje uma questão registrada
     * @see IPattern Verificador de respostas
     * @see Question Questão para definir as preferencias
     */
    public <T> Question<T> alternativeQuestion(String id, String question, IPattern<T> pattern) {
        return new QuestionImpl<T>(id, question, pattern, this);
    }

    /**
     * Inicia o questionário para o jogador.
     *
     * @param player      Jogador
     * @param endListener Quando o questionário terminar este será o consumer que será chamado
     * @return True se conseguir aplicar ao jogador, false caso ele ja esteja em um questionário ou
     * caso o questionário não tenha questões.
     */
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

    /**
     * Envia a reposta do jogador ao gerenciador para ela ser processada
     *
     * @param player Jogador
     * @param answer Resposta em texto
     * @return {@link AnswerResult} com os dados do ocorrido no processamento
     * @see AnswerResult
     */
    @SuppressWarnings("unchecked")
    public AnswerResult answer(Player player, String answer) {
        Optional<QuestionBase> baseOptional = current(player);
        if (baseOptional.isPresent()) {
            if (!baseOptional.get().isOk(answer)) {
                return new AnswerResult(Optional.empty(), AnswerResult.State.INVALID_ANSWER_FORMAT);
            } else {
                QuestionBase questionBase = playerInterpreter.get(player).answer(baseOptional.get().transform(answer));
                Optional<QuestionBase> next = questionBase.processAndNext(player, answer);
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

    /**
     * Obtém o ID do Interpreter
     *
     * @return O ID do Interpreter
     */
    public String getId() {
        return id;
    }

    /**
     * Obtém a questão atual do jogador
     *
     * @param player Jogador
     * @return Questão atual, ou {@link Optional#empty()} caso não tenha questão atual
     */
    protected Optional<QuestionBase> current(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();
        return Optional.of(playerInterpreter.get(player).getCurrent());
    }

    /**
     * Obtém a próxima questão do jogador
     *
     * @param player Jogador
     * @return Próxima questão, ou {@link Optional#empty()} caso não tenha próxima questão
     */
    protected Optional<QuestionBase> next(Player player) {
        if (!playerInterpreter.containsKey(player))
            return Optional.empty();

        return Optional.of(playerInterpreter.get(player).next());
    }

    /**
     * Vai para a próxima questão, este método chama o método {@link #next(Player)}
     *
     * @param player Jogador
     * @return Próxima questão, ou {@link Optional#empty()} caso não tenha próxima questão
     * @see #next(Player)
     */
    protected Optional<QuestionBase> deque(Player player) {
        return next(player);
    }

    /**
     * Remove a questão do jogador, o método é chamado quando o jogador responde a questão
     *
     * @param player       Jogador
     * @param questionBase Questão para remover
     * @return True caso remova, false caso não haja questionário atual ou caso a questão não esteja
     * presente
     */
    protected boolean remove(Player player, QuestionBase questionBase) {
        return playerInterpreter.containsKey(player) && playerInterpreter.get(player).remove(questionBase);
    }

    /**
     * Determina se há próxima questão para o jogador
     *
     * @param player Jogador
     * @return True caso haja, false caso não haja questionário atual ou caso não haja próxima
     * questão
     */
    public boolean hasNext(Player player) {
        return playerInterpreter.containsKey(player) && playerInterpreter.get(player).hasNext();
    }

    /**
     * Finaliza o questionário do jogador
     *
     * @param player Jogador
     * @return True caso finalize com sucesso, false caso não haja questionário atual
     */
    public boolean end(Player player) {
        if (!playerInterpreter.containsKey(player))
            return false;
        playerInterpreter.get(player).callEnd(player);
        playerInterpreter.remove(player);
        return true;
    }

    /**
     * Define o questionário atual, chamado pelo método processAndNext do {@link
     * QuestionBase#processAndNext(Player, String)}, isto permite que o questionário se mantenha
     * sincronizado com as questões alternativas que podem aparecer no decorrer do questionário
     *
     * @param player   Jogador
     * @param question Questão para definir como atual
     * @return True caso defina com sucesso, false caso não haja questionário atual
     */
    protected boolean setCurrent(Player player, Optional<QuestionBase> question) {
        if (!playerInterpreter.containsKey(player))
            return false;
        playerInterpreter.get(player).setCurrent(question.get());
        return true;
    }
}
