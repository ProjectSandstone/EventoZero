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

/**
 * Definições da sessão do questionário
 */
public class InterpreterData {

    private final Interpreter interpreter;
    private final Deque<QuestionBase> questionBaseList;
    private final Map<QuestionBase, Object> answers = new HashMap<>();
    private final BiConsumer<Player, AnswerData> endListener;
    private QuestionBase current;

    /**
     * Instancia da sessão
     *
     * @param interpreter      Gerenciador do questionário
     * @param questionBaseList Cópia da lista de questões
     * @param endListener      Lambda que será chamado ao final
     */
    public InterpreterData(Interpreter interpreter, Deque<QuestionBase> questionBaseList, BiConsumer<Player, AnswerData> endListener) {
        this.interpreter = interpreter;
        this.questionBaseList = questionBaseList;
        this.endListener = endListener;
    }

    /**
     * Obtém o interpretador
     *
     * @return O interpretador
     */
    public Interpreter getInterpreter() {
        return interpreter;
    }

    /**
     * Obtém a próxima questão
     *
     * @return Próxima questão
     */
    public QuestionBase next() {
        return current = questionBaseList.pollFirst();
    }

    /**
     * Obtém a questão atual
     *
     * @return Questão atual
     */
    public QuestionBase getCurrent() {
        return current;
    }

    /**
     * Define a questão atual para manter o sistema sincronizado com as questões alternativas
     *
     * @param current Questão atual
     */
    public void setCurrent(QuestionBase current) {
        this.current = current;
    }

    /**
     * Define a resposta
     *
     * @param answer Valor da resposta
     * @return Questão atual
     */
    public QuestionBase answer(Object answer) {
        answers.put(getCurrent(), answer);
        return getCurrent();
    }

    /**
     * Remove uma questão
     *
     * @param questionBase Questão para remover
     * @return True caso remova, false caso ela não esteja na lista
     */
    public boolean remove(QuestionBase questionBase) {
        return questionBaseList.remove(questionBase);
    }

    /**
     * Determina se há próxima questão
     *
     * @return Determina se há próxima questão
     */
    public boolean hasNext() {
        return !questionBaseList.isEmpty();
    }

    /**
     * Chama o fim do questionário para que o consumidor de fim do questionário seja chamado
     *
     * @param player Jogador
     */
    public void callEnd(Player player) {
        this.endListener.accept(player, new AnswerData(getAnswers()));
    }

    /**
     * Obtém uma cópia do mapa de todas as respostas
     *
     * @return Uma cópia do mapa de todas as respostas
     */
    public Map<QuestionBase, Object> getAnswers() {
        Map<QuestionBase, Object> answersCopy = new HashMap<>(answers);
        return answersCopy;
    }

}
