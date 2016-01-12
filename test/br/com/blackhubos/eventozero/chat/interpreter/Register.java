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

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.base.Question;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.Patterns;

public class Register {


    public static final Interpreter questionario1 = new Interpreter("questionario1");

    public void setupInterpreters() {
        questionario1.question("nome", ChatColor.GREEN + "Qual seu nome (Permitido: somente letras)?", Patterns.ALL)
                .expect((nome) -> nome.matches("(?i)[A-Z ]+"))
                .yes((player, i) -> player.sendMessage("Seu nome é: " + i));

        questionario1.question("idade", ChatColor.GREEN + "Qual sua idade (Permitido: entre 0 e 190)?", Patterns.Integer)
                .expect((i) -> i > 0 && i < 190)
                .yes((player, i) -> player.sendMessage("Sua idade é: " + i));

        questionario1.question("genero", ChatColor.GREEN + "Qual seu genero [Permitido: masculino | feminino]?", Patterns.ALL)
                .expect((s) -> s.equals("masculino") || s.equals("feminino"))
                .yes((player, i) -> player.sendMessage("Seu genero é: " + i))
                .no((player, i) -> player.sendMessage("Seu genero não é: " + i));

        Question irSpawn = questionario1.alternativeQuestion("ir_spawn", ChatColor.GREEN + "Deseja ir para o Spawn (Permitido: y, n, t, f, yes, no, true, false)?", Patterns.Boolean)
                .yes((player, i) -> player.teleport(player.getWorld().getSpawnLocation()))
                .no((player, i) -> player.sendMessage(ChatColor.GREEN + "Tudo certo"));

        questionario1.question("ir_para_cama", ChatColor.GREEN + "Deseja ir para a localização da cama (Permitido: y, n, t, f, yes, no, true, false)?", Patterns.Boolean)
                .yes((player, i) -> {
                    if (player.getBedSpawnLocation() == null) {
                        player.sendMessage(ChatColor.RED + "Cama nao encontrada");
                    } else {
                        player.teleport(player.getBedSpawnLocation());
                    }

                })
                .no(irSpawn, (player, i) -> player.sendMessage("Entao ok!"));
    }
}
