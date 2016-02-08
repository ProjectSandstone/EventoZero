package br.com.blackhubos.eventozero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.base.CharMatcher;

import de.schlichtherle.io.File;
import io.github.bktlib.command.CommandBase;
import io.github.bktlib.command.CommandResult;
import io.github.bktlib.command.CommandSource;
import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.annotation.SubCommand;
import io.github.bktlib.command.args.CommandArgs;

@Command(
	name = "eventozero",
	aliases = "ez",
	description = "...",
	subCommands = "this::*"
)
public class EZCommand extends CommandBase {
	
	@Override
	public CommandResult onExecute(CommandSource src, CommandArgs args) {
		
		/*
		 * TODO: adicionar sistema de paginas, pois são muitos subcomandos
		 */
		
		final boolean isConsole = src.isConsole();
		
		getSubCommands()
			.stream()
			.map( subCmd -> {
				String st = String.format( "&2 &a/ez %s %s&2= %s",
						subCmd.getName(), 
						subCmd.getUsage().orElse(" "), 
						subCmd.getDescription().orElse("Sem descrição")
				);
				
				//if ( isConsole )
				{
					st = CharMatcher.ASCII.negate().removeFrom(st);
				}
				
				return st;
			})
			.forEach(src::sendMessage);
		
		return CommandResult.success();
	}

	@SubCommand(
		name = "iniciar", 
		usage = "<evento> ", 
		description = "Iniciar um evento manualmente"
	)
	private CommandResult iniciar(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "cancelar", 
		usage = "<evento> ", 
		description = "Cancela um evento ocorrendo"
	)
	private CommandResult cancelar(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "add", 
		usage = "entrada <evento> ", 
		description = "Adiciona uma nova entrada"
	)
	private CommandResult add(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "del", 
		usage = "entrada <evento> <id> ", 
		description = "Remove uma entrada"
	)
	private CommandResult del(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "entradas", 
		usage = "<evento> ", 
		description = "Obtém as entradas e suas IDs"
	)
	private CommandResult entradas(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "camarote", 
		usage = "<evento> ", 
		description = "Adiciona um novo camarote"
	)
	private CommandResult camarote(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "pvp", 
		usage = "<evento> <allowdeny> ", 
		description = "Define o status de pvp"
	)
	private CommandResult pvp(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "mc", 
		usage = "<evento> <allowdeny> ", 
		description = "Bloqueia ou permite MC"
	)
	private CommandResult mc(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "tools", 
		usage = "", 
		description = "Abre a caixa de ferramentas do eventozero"
	)
	private CommandResult tools(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "cmd", 
		usage = "", 
		description = "comandos"
	)
	private CommandResult cmd(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "potion", 
		usage = "", 
		description = "poções"
	)
	private CommandResult potion(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "kit", 
		usage = "", 
		description = "kits"
	)
	private CommandResult kit(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "party", 
		usage = "", 
		description = "party"
	)
	private CommandResult party(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "max", 
		usage = "<evento> <quantia> ", 
		description = "Define o número máximo de jogadores (autoinicia)"
	)
	private CommandResult max(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "min", 
		usage = "<evento> <quantia> ", 
		description = "Define o número mínimo de jogadores para poder iniciar o evento"
	)
	private CommandResult min(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "autostop", 
		usage = "<evento> <tempo> ", 
		description = "Define o tempo para terminar o evento se não houver ganhadores."
	)
	private CommandResult autostop(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "setengine", 
		usage = "<evento> <arquivo.sc> ", 
		description = "Definir uma engine para um evento"
	)
	private CommandResult setengine(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "placements", 
		usage = "<evento> <quantia> ", 
		description = "Define o limite de colocações"
	)
	private CommandResult placements(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "points", 
		usage = "", 
		description = "pontos"
	)
	private CommandResult points(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "updates", 
		usage = "", 
		description = "Apenas verifica se há novos updates"
	)
	private CommandResult updates(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "upgrade", 
		usage = "", 
		description = "Verifica e aplica o update automaticamente se existir"
	)
	private CommandResult upgrade(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "reload", 
		usage = "", 
		description = "Faz reload completo no plugin"
	)
	private CommandResult reload(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}

	@SubCommand(
		name = "disable", 
		usage = "", 
		description = "Desativa o plugin completamente"
	)
	private CommandResult disable(final CommandSource src, final CommandArgs args) {
		return CommandResult.success();
	}
}
