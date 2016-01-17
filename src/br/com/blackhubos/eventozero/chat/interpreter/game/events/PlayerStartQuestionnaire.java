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

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;

/**
 * Evento chamado quando o jogador iniciar um questionário
 *
 * @param <T> Tipo do ID do questionário
 */
public class PlayerStartQuestionnaire<T> extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Interpreter<T> questionnaire;
    private boolean cancelled;

    /**
     * Evento chamado quando o jogador iniciar um questionário
     *
     * @param who           Jogador
     * @param questionnaire Questionário
     */
    public PlayerStartQuestionnaire(Player who, Interpreter<T> questionnaire) {
        super(who);
        this.questionnaire = questionnaire;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Obtém o questionário
     *
     * @return O questionário
     */
    public Interpreter<T> getQuestionnaire() {
        return questionnaire;
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
