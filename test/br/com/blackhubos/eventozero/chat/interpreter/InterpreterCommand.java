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
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;

public class InterpreterCommand implements CommandExecutor {

    private final Interpreter questionario;

    InterpreterCommand(Interpreter questionario) {
        this.questionario = questionario;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Optional<Interpreter> current = Interpreter.getCurrent(player);
            if (current.isPresent() && current.get() == questionario) {

            } else {
                sender.sendMessage(ChatColor.GREEN + "Responda o questionario:");
                questionario.apply(player, (playerSub, data) -> {
                    playerSub.sendMessage(ChatColor.GREEN + "Suas respostas:");
                    for (Map.Entry<QuestionBase, Object> entry : data.toMap().entrySet()) {
                        playerSub.sendMessage(entry.getKey().question() + " = " + entry.getValue() + " [" + entry.getValue().getClass() + "]");
                    }
                });
            }
        } else {
            sender.sendMessage(Color.RED + "Voce precisa ser um jogador");
        }
        return false;
    }
}
