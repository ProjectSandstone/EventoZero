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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.state.AnswerResult;

public class InterpreterListener implements Listener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // Obtem o questionário atual
        Optional<Interpreter> current = Interpreter.getCurrent(player);
        // Se estiver presente processa as mensagens
        if (current.isPresent()) {
            // O método .answer envia as repostas ao processadores e avaliadores, e retorna o estado
            AnswerResult result = current.get().answer(player, event.getMessage());
            // Estado indica a validação das respostas
            AnswerResult.State answerState = result.getState();

            switch (answerState) {
                // Respostas com formato inválido
                case INVALID_ANSWER_FORMAT: {
                    player.sendMessage(ChatColor.RED + "Resposta inválida, tente novamente!");
                    break;
                }
                // Fim do questionário, quando não há mais questões
                case NO_MORE_QUESTIONS: {
                    player.sendMessage(ChatColor.GREEN + "Voce terminou o questionario");
                    break;
                }
                // Quando a respotas é válida
                case OK: {
                    break;
                }
                // Quando não há questionário atual, provavelmente um bug, isto não pode acontecer nunca :P
                // Provavelmente ocorra quando o questionário é desregistrado em paralelo com a verificação de resposta
                // Por motivos de segurança, como nem sempre sabemos o que estes bugs podem gerar retiramos o jogador do questionário atual
                case NO_CURRENT_QUESTIONNAIRE: {
                    player.sendMessage(ChatColor.RED + "Sem questionario atual, isto provavelmente é um erro, tentamos corrigir!");
                    current.get().endNoData(player);
                    break;
                }
                // Quando o jogador for retirado por vontade própria do questionário
                case PLAYER_END: {
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
        // Removemos o jogador do questionário quando ele sair do jogo
        Optional<Interpreter> current = Interpreter.getCurrent(player);
        if (current.isPresent()) {
            current.get().endNoData(player);
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand() != null) {

            if (event.getPlayer().getItemInHand().getType() == Material.GOLDEN_APPLE) {
                // Obtém o questionário de perfil
                Optional<Interpreter<String>> interpreter = Interpreter.getById("perfil");
                // Inicia um questionário para o jogador
                interpreter.get().apply(event.getPlayer(), (player, answerData) -> {
                    // answerData = Respostas do jogador
                    // Este lambda só é chamado ao fim do questionário
                });
            }
        }
    }
}
