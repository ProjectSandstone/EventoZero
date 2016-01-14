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
package br.com.blackhubos.eventozero.chat.interpreter.game.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;
import br.com.blackhubos.eventozero.factory.ItemFactory;

public class InterpreterListener implements Listener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Optional<Interpreter> current = Interpreter.getCurrent(player);
        if (current.isPresent()) {
            AnswerResult result = current.get().answer(player, event.getMessage());
            AnswerResult.State answerState = result.getState();

            switch (answerState) {
                case INVALID_ANSWER_FORMAT: {
                    player.sendMessage(ChatColor.RED + "Resposta inválida, tente novamente!");
                    break;
                }
                case NO_MORE_QUESTIONS: {
                    player.sendMessage(ChatColor.GREEN + "Voce terminou o questionario");
                    break;
                }
                case OK: {
                    break;
                }

                case NO_CURRENT_QUESTION: {
                    player.sendMessage(ChatColor.RED + "Sem questionario atual, isto provavelmente é um erro, tentamos corrigir!");
                    current.get().endNoData(player);
                    break;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent playerQuitEvent) {
        handleDC(playerQuitEvent.getPlayer());
    }

    private void handleDC(Player player) {
        Optional<Interpreter> current = Interpreter.getCurrent(player);
        if(current.isPresent()) {
            current.get().endNoData(player);
        }
    }

}
