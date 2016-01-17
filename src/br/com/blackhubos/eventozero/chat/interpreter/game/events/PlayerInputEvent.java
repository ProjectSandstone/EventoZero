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

/**
 * Evento chamado quando o jogador digitar algum comando ou texto
 */
public class PlayerInputEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final String inputText;
    private boolean cancelled;

    /**
     * Construtor do evento de entrada do jogador
     *
     * @param who       Jogador
     * @param inputText Comando ou Texto
     */
    public PlayerInputEvent(Player who, String inputText) {
        super(who);
        this.inputText = inputText;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Obtém o texto ou commando digitado pelo jogador
     *
     * @return O texto ou commando digitado pelo jogador
     */
    public String getInputText() {
        return this.inputText;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
