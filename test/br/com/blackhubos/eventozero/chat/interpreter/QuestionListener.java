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
package br.com.blackhubos.eventozero.chat.interpreter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;

public class QuestionListener implements Listener {

    private final Interpreter questionario;

    QuestionListener(Interpreter questionario) {
        this.questionario = questionario;
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Optional<Interpreter> current = Interpreter.getCurrent(player);
        if (current.isPresent()) {
            if (current.get() == questionario) {
                AnswerResult result = current.get().answer(player, event.getMessage());
                AnswerResult.State answerState = result.getState();

                switch (answerState) {
                    case INVALID_ANSWER_FORMAT: {
                        player.sendMessage(ChatColor.RED + "Ops, resposta invalida!");
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
                        player.sendMessage(ChatColor.RED + "Ops, sem questoes!");
                        break;
                    }
                }
            }
            event.setCancelled(true);
        }

    }
}
