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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import br.com.blackhubos.eventozero.chat.interpreter.base.impl.QuestionImpl;
import br.com.blackhubos.eventozero.chat.interpreter.data.AnswerData;
import br.com.blackhubos.eventozero.chat.interpreter.data.InterpreterData;
import br.com.blackhubos.eventozero.chat.interpreter.game.events.PlayerStartQuestionnaire;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.IPattern;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;

/**
 * Interpretador, classe responsável por gerenciar tudo!
 *
 * Somente é permitido uma sessão de perguntas por jogador!
 *
 * Classes avaliadoras: São sempre chamadas para avaliar os valores que entraram e definir se são
 * validos ou não, algumas delas são: {@link IPattern} e {@link br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation}
 *
 * Classes monitoras: são chamadas somente para mostrarem os valores, estando eles com erro ou não,
 * elas não poderão avaliar somente monitorar, estas clases são chamadas depois das avaliadoras, e
 * as avaliadoras definem o método que será chamado das monitoras e monitoras finais, uma delas é a
 * {@link br.com.blackhubos.eventozero.chat.interpreter.state.input.InputState}
 *
 * Classes monitoras finais: são chamadas quando terminar a questão (Obs: antes do questionário ser
 * concluido). Estas classes somente monitoram as respostas, elas não podem fazer mais nada,
 * diferente do que as monitoras podem fazer, as monitoras podem analisar todas as entradas do
 * usuário, já as monitoras finais, somente o valor válido. Elas são chamadas adepois das monitoras
 * e somente com a permissão das classes avaliadoras, uma delas é: {@link
 * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult} e {@link
 * IPattern}
 *
 * A classe IPattern tem 1 avaliador, 1 monitor final e 1 tradutor de valores, o avaliador é o
 * {@link IPattern#check}. O monitor final é {@link IPattern#booleanResult} e seu tradutor de
 * valores, que pode ser considerado um monitor final dependendo do ponto de vista: {@link
 * IPattern#transformer}.
 *
 * Classes Hidden: São as classes principais de suas classes pai, as classes hidden extende sua
 * classe API. As classes Hidden não estão visiveis para quem for utilizar a API, mas está visivel
 * para implementação, e a implementação deve ser somente feita a partir das classes Hidden, nunca
 * de suas classes pai, elas são somente uma interface, as classes hidden tem o nome 'Base' no
 * final, que é basicamente a Base de suas classes pai. Para o Interpreter trabalhar com
 * implementação de classes que não implementem a Hidden precisará de uma modificação muito grandle
 * pois há muitos cast para estas classes. As implementações padrões estão em um pacote 'impl' do
 * mesmo pacote das classes Hidden Classes Hidden: {@link QuestionBase} {@link
 * br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult} {@link
 * br.com.blackhubos.eventozero.chat.interpreter.base.expectation.ExpectationBase} {@link
 * br.com.blackhubos.eventozero.chat.interpreter.state.input.InputStateBase}
 */
public class Interpreter<T> {

    private static final List<Interpreter<?>> interpreters = new ArrayList<>();
    private static final Map<Player, InterpreterData> playerInterpreter = new HashMap<>();
    private final Deque<QuestionBase> registeredQuestion = new LinkedList<>();
    private final T id;
    private String finalizarQuestionario = "!sair";
    private String iniciouQuestionario = ChatColor.GREEN + "Voce iniciou o questionário, para sair digite: " + finalizarQuestionario;
    private String mensagemFinalizouQuestionario = ChatColor.RED + "Voce finalizou o questionário.";

    /**
     * Construtor
     *
     * O interpretador é adicionado automaticamente ao ser construido
     *
     * @param id Id para obter o interpretador posteriormente
     */
    public Interpreter(T id) {
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
    public static <T> Optional<Interpreter> expectCurrent(Player player, T id) {
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
    public static <T> Optional<Interpreter<T>> getById(T id) {
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
     * @param <E>      Tipo do valor
     * @return Questão para definir as preferencias da mesma
     * @see #alternativeQuestion(Object, String, IPattern) Caso deseje uma questão alternativa que
     * não será registrada
     * @see IPattern Verificador de respostas
     * @see Question Questão para definir as preferencias
     */
    public <E, ID> Question<E, ID> question(ID id, String question, IPattern<E> pattern) {
        Question<E, ID> questionAdd = alternativeQuestion(id, question, pattern);
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
     * @see #question(Object, String, IPattern) Caso deseje uma questão registrada
     * @see IPattern Verificador de respostas
     * @see Question Questão para definir as preferencias
     */
    public <T, ID> Question<T, ID> alternativeQuestion(ID id, String question, IPattern<T> pattern) {
        return new QuestionImpl<>(id, question, pattern, this);
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

        PlayerStartQuestionnaire<T> playerStartQuestionnaire = new PlayerStartQuestionnaire<>(player, this);
        Bukkit.getPluginManager().callEvent(playerStartQuestionnaire);
        if (playerStartQuestionnaire.isCancelled()) {
            return false;
        }

        Deque<QuestionBase> copy = new LinkedList<>(registeredQuestion);
        InterpreterData interpreterData = new InterpreterData(this, copy, endListener);

        playerInterpreter.put(player, interpreterData);

        Optional<QuestionBase> baseOptional = next(player);
        if (baseOptional.isPresent()) {
            player.sendMessage(iniciouQuestionario);
            player.sendMessage(baseOptional.get().question());
        } else {
            endNoData(player);
            return false;
        }
        return true;
    }

    /**
     * !!! Este método não é sincronizado !!! USE: {@link #answer(Player, String)}
     *
     * @since 1.0.1
     * @deprecated Método não sincronizado nem envia a mensagem de questão ao jogador
     */
    @Deprecated
    public AnswerResult deprecated__answer(Player player, String answer) {
        if (answer.equalsIgnoreCase(finalizarQuestionario)) {
            if (endNoData(player)) {
                warnEnd(player);
            }
            return new AnswerResult(Optional.empty(), AnswerResult.State.PLAYER_END);
        } else {
            Optional<QuestionBase> baseOptional = current(player);
            AnswerResult result = hiddenAnswer(baseOptional, player, answer);
            if (baseOptional.isPresent()) {
                baseOptional.get().processAnswer(player, answer, result);
            }
            return result;
        }
    }

    /**
     * Envia a reposta do jogador ao gerenciador para ela ser processada Se a mensagem for igual a
     * {@link #finalizarQuestionario} o questionário será finalizado
     *
     * @param player Jogador
     * @param answer Resposta em texto
     * @return {@link AnswerResult} com os dados do ocorrido no processamento
     * @see AnswerResult
     */
    public AnswerResult answer(Player player, String answer) {
        if (answer.equalsIgnoreCase(finalizarQuestionario)) {
            if (endNoData(player)) {
                warnEnd(player);
            }
            return new AnswerResult(Optional.empty(), AnswerResult.State.PLAYER_END);
        } else {
            Optional<QuestionBase> baseOptional = current(player);
            AnswerResult result = hiddenAnswer(baseOptional, player, answer);
            if (baseOptional.isPresent()) {
                baseOptional.get().processAnswer(player, answer, result);
            }
            if (result.getNext().isPresent()) {
                player.sendMessage(result.getNext().get().question());
            }
            return result;
        }
    }

    /**
     * Define o texto que é necessário para finalizar o questionário
     *
     * Ao finalizar todas as respostas serão perdidas
     *
     * @param finalize Texto necessário para finalizar
     * @return Este interpretador
     */
    public Interpreter<T> finalizeInput(@Nonnull String finalize) {
        this.finalizarQuestionario = finalize;
        return this;
    }

    /**
     * Mensagem que aparecerá ao finalizar o questionário
     *
     * @param finalizeMessage Mensagem
     * @return Este questionário
     */
    public Interpreter<T> finalizeMessage(@Nonnull String finalizeMessage) {
        this.iniciouQuestionario = finalizeMessage;
        return this;
    }

    /**
     * Mensagem que aparecerá ao iniciar o questionário
     *
     * @param startMessage Mensagem
     * @return Este questionário
     */
    public Interpreter<T> startQuestionnaire(@Nonnull String startMessage) {
        this.finalizarQuestionario = startMessage;
        return this;
    }

    /**
     * Avisa que o jogador finalizou o questionário
     *
     * @param player Jogador
     */
    private void warnEnd(Player player) {
        player.sendMessage(mensagemFinalizouQuestionario);
    }

    /**
     * Envia a reposta do jogador ao gerenciador para ela ser processada chamar o método {@link
     * #answer}
     *
     * @param baseOptional Questão
     * @param player       Jogador
     * @param answer       Resposta em texto
     * @return {@link AnswerResult} com os dados do ocorrido no processamento
     * @see #answer(Player, String)
     * @see AnswerResult
     * @deprecated Este método não ativa o {@link br.com.blackhubos.eventozero.chat.interpreter.state.input.InputState}
     * nem envia mensagem ao jogador Use: {@link #answer(Player, String)}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    private AnswerResult hiddenAnswer(Optional<QuestionBase> baseOptional, Player player, String answer) {
        if (baseOptional.isPresent()) {
            if (!baseOptional.get().isOk(player, answer)) {
                return new AnswerResult(Optional.empty(), AnswerResult.State.INVALID_ANSWER_FORMAT);
            } else {
                QuestionBase questionBase = playerInterpreter.get(player).answer(baseOptional.get().transform(answer));
                Optional<QuestionBase> next = questionBase.processAndNext(player, answer);
                if (next.isPresent()) {
                    return new AnswerResult(next, AnswerResult.State.OK);
                } else {
                    return new AnswerResult(Optional.empty(), AnswerResult.State.NO_MORE_QUESTIONS);
                }
            }
        } else {
            return new AnswerResult(Optional.empty(), AnswerResult.State.NO_CURRENT_QUESTIONNAIRE);
        }
    }

    /**
     * Obtém o ID do Interpreter
     *
     * @return O ID do Interpreter
     */
    public T getId() {
        return id;
    }

    /**
     * Obtem uma questão baseado em seu ID
     *
     * @param id   Id da questão
     * @param <T>  Tipo do valor
     * @param <ID> Tipo do ID
     * @return Questao se existir, ou {@link Optional#empty()}
     */
    @SuppressWarnings("unchecked")
    public <T, ID> Optional<Question<T, ID>> getQuestionByID(ID id) {
        for (QuestionBase questionBase : registeredQuestion) {
            if (questionBase.id().equals(id)) {
                return Optional.of(questionBase);
            }
        }
        return Optional.empty();
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
     * Finaliza o questionário do jogador caso ele não esteja mais presente no servidor, este método
     * não chama o endListener, todas as respostas do jogador serão perdidas, para evitar isto use o
     * matodo: {@link #end(Player)}, porém, este metodo poderá gerar erros caso o listener tente
     * executar ações com o jogador já offline.
     *
     * @param player Jogador
     * @return True caso finalize com sucesso, false caso não haja questionário atual
     */
    public boolean endNoData(Player player) {
        if (!playerInterpreter.containsKey(player))
            return false;
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
