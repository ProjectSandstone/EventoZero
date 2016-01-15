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
package br.com.blackhubos.eventozero.chat.interpreter.game.register;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter;
import br.com.blackhubos.eventozero.chat.interpreter.base.Question;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.base.expectation.Expectation;
import br.com.blackhubos.eventozero.chat.interpreter.game.listener.InterpreterListener;
import br.com.blackhubos.eventozero.chat.interpreter.pattern.Patterns;

/**
 * Para melhorar a organização esta classe registrará tudo relacionado ao interpreter
 */
public class InterpreterRegister {

    /**
     * Registra as questions e os listeners
     */
    public static void registerAll(final Plugin plugin) {
        InterpreterRegister.registerQuestions(plugin);
        InterpreterRegister.registerListeners(plugin);
    }

    /**
     * Registrar as questões
     */
    private static void registerQuestions(final Plugin plugin) {
        // Criamos um novo questionário com ID de perfil, o ID pode ser qualquer objeto, até enums,
        // somente é necessário informar o tipo genérico, exemplo: Interpreter<MyEnum> interpreter = new ....
        Interpreter<String> interpreter = new Interpreter<>("perfil");
        // Criamos uma nova questão registrada com id de 'nome', a regra do id se aplica igual ao do Interpreter
        // Definimos que a pergunta é o nome completo composto somente por letras e um regex de regra para o nome
        // Inicio a cada espaço: Maiuscula, demais letras: Minusculas. Contendo pelo menos dois nomes (nome e sobrenome).
        interpreter.question("nome", ChatColor.GREEN + "Nome completo (somente letras): ", Patterns.ALL)
                // Obtém o gerenciador de "espera de como os dados devem ser"
                // Método de espera de valor com o regex supracitado
                .expect(nome -> nome.matches("([A-Z][a-z]+[ ]?){2,}"))
                // O Nome não indica sua função neste caso, somente melhora o entendimento do código
                // Este método irá "fechar" as definições que abrimos com o método .expect
                // Sua função real é retornar a questão atual
                .endDefinition()
                // Obtemos o gerenciador de entrada
                .inputState()
                // Definimos que toda entrada que for inválida mostrará a mensagem definida no lambda
                .error((player, s) -> {
                    player.sendMessage(ChatColor.RED + "Nome Incorreto, seu nome deve ter maiusculas no inicio!");
                    player.sendMessage(ChatColor.RED + "Exemplo: Alberto Silva Campos!");
                })
                // Definimos que toda entrada que for válida irá mostra a mensagem definida no lambda
                .ok((player, s) -> player.sendMessage(ChatColor.GREEN + "Nome aceito!"));

        // Definimos uma questão alternativa, esta não é registrada, mostrarei mais a frente seu uso.
        // Esta questão tem os tipos genéricos Boolean, que é o tipo do valor que teremos como resultado
        // E String, que é o tipo do ID
        // Esta questão pergunta ao jogador se ele quer ir a area vip
        // Usamos o pattern boolean, que tem o tradutor de valores boolean
        Question<Boolean, String> alternativa = interpreter.alternativeQuestion("area_vip",
                ChatColor.YELLOW+"Deseja ir para a área "+ChatColor.DARK_PURPLE+"VIP"+ChatColor.YELLOW+"?",
                Patterns.Boolean)
                // Um boolean result vazio irá considerar o resultado do Patterns
                // E como o patterns é um boolean, todas booleans true irão referenciar o método yes
                // E as booleans false irão referenciar o método no
                // Todos patterns podem fazer isto, veja a documentação do IPattern
                .booleanResult()
                .yes((jogador, resultado) -> {
                    // Caso o jogador queira ir para área vip vamos levar ele até ela, como eu não tenho área vip
                    // Vou jogar ele em um lugar "aleatorio bem alto" só para me divertir
                    jogador.teleport(new Location(jogador.getWorld(), 10, 128, 10));
                })
                // Caso ele não queira, vamos lhe enviar uma mensagem
                .no((jogador, resultado) -> jogador.sendMessage(ChatColor.RED+"É você quem sabe..."))
                // Neste caso, precisamos que o retorno seja a classe Question,
                // se não, não poderemos definir a váriavel, então, fechamos as definições abertas
                // em .booleanResult
                .endDefinition();

        // Criamos aqui uma questão mais complexa e a mais completa
        interpreter.question("idade", ChatColor.GREEN + "Idade (entre 13 e 190):", Patterns.Integer)
                // Vamos esperar que a idade dele seja entre 13 e 190 anos
                .expect(idade -> idade >= 13 && idade <= 190)
                // Se for, vamos aceitar a idade
                .True((jogador, idade) -> {
                    jogador.sendMessage(ChatColor.GREEN + "Idade aceita: " + idade);
                    // Informa que aceitamos a idade
                    return Expectation.State.CONTINUE;
                })
                // Se não for, vamos considerar 1 ano de diferença do valor informado
                .False((jogador, idade) -> {
                    // Se a idade for igual a 13 - 1, ou seja, 12, vamos considerar também, só para a felicidade do jogador
                    if (idade == 13 - 1) {
                        // Avisamos que consideramos
                        jogador.sendMessage(ChatColor.RED + "Considerando diferença de 1 ano!");
                        // Informa que aceitamos a idade
                        return Expectation.State.CONTINUE;
                    }else{
                        jogador.sendMessage(ChatColor.RED + "Idade nao aceita: " + idade);
                    }
                    // Informa que rejeitamos a idade
                    return Expectation.State.BREAK;
                })
                // Fechamos as definições que abrimos no método .expect
                .endDefinition()
                // Vamos definir as ações para os resultados com o boolean result
                // Aqui iremos informar se ele é ou não maior de idade
                .booleanResult(idade -> idade >= 18 ? BooleanResult.YES : BooleanResult.NO)
                // Se ele for, enviamos a mensagem de que é, e definimos que a próxima questão é a da
                // área vip, o método yes pode ser usado sozinho, sem necessidade do lambda, somente com
                // a proxima questão
                .yes(alternativa, (jogador, idade) -> {
                    jogador.sendMessage(ChatColor.GREEN + "Voce é maior de idade");
                })
                // Se ele for menor de idade, infelizmente, não tem area vip
                .no((jogador, idade) -> {
                    jogador.sendMessage(ChatColor.RED + "Voce é menor de idade");
                })
                // Fechamos as definições do .booleanResult
                .endDefinition()
                // Obtemos o gerenciador de entrada de dados
                .inputState()
                // Informamos ao jogador que está tudo certo, e informamos a idade dele
                .ok((player, s) -> {
                    player.sendMessage(ChatColor.GREEN + "Tudo certo, você tem " + s + " anos");
                })
                // Informamos aqui, que a idade é incorreta, somente informamos, e ele terá que
                // reescrever a idade, a mensagem sempre aparecerá quando ele digitar uma idade que
                // não estiver dentro das definições
                .error((jogador, idade) -> {
                    jogador.sendMessage(ChatColor.RED + "Nada certo, você tem não " + idade + " anos");
                }).endDefinition();

    }

    /**
     * Registrar os listeners
     */
    private static void registerListeners(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new InterpreterListener(), plugin);
    }

}
