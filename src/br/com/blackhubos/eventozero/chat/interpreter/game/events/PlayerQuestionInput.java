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
package br.com.blackhubos.eventozero.chat.interpreter.game.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;

/**
 * Evento chamado quando o jogador reponde alguma questão
 */
public class PlayerQuestionInput extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final AnswerResult result;
    private final String playerInput;
    private final Optional<Interpreter> current;
    private boolean cancelled;

    /**
     * Evento chamado quando o jogador reponde alguma questão
     *
     * @param who                Jogador que respondeu a questão
     * @param playerInput        Entrada do jogador (resposta)
     * @param result             Resultado da avaliação da questão
     * @param currentInterpreter Questionário atual do jogador
     */
    public PlayerQuestionInput(Player who, String playerInput, AnswerResult result, Optional<Interpreter> currentInterpreter) {
        super(who);
        this.result = result;
        this.playerInput = playerInput;
        this.current = currentInterpreter;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Obtém o questionário atual do jogador
     *
     * @return O questionário atual do jogador
     */
    public Optional<Interpreter> getCurrentInterpreter() {
        return current;
    }

    /**
     * Obtem o resultado da avaliação da questão
     *
     * @return O resultado da avaliação da questão
     */
    public AnswerResult getResult() {
        return result;
    }

    /**
     * Obtem a entrada do jogador
     *
     * @return A entrada do jogador
     */
    public String getPlayerInput() {
        return playerInput;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
