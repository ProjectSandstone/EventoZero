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
package br.com.blackhubos.eventozero;

import java.io.File;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.blackhubos.eventozero.chat.interpreter.game.register.InterpreterRegister;
import br.com.blackhubos.eventozero.factory.Event;
import br.com.blackhubos.eventozero.factory.EventFactory;
import br.com.blackhubos.eventozero.factory.EventHandler;
import br.com.blackhubos.eventozero.factory.EventState;
import br.com.blackhubos.eventozero.handlers.KitHandler;
import br.com.blackhubos.eventozero.handlers.MessageHandler;
import br.com.blackhubos.eventozero.handlers.ShopHandler;
import br.com.blackhubos.eventozero.listeners.EventListener;
import br.com.blackhubos.eventozero.ranking.RankingListener;
import br.com.blackhubos.eventozero.storage.Storage;
import br.com.blackhubos.eventozero.updater.Updater;
import br.com.blackhubos.eventozero.util.Framework;
import br.com.blackhubos.eventozero.util.Framework.Configuration;
import br.com.blackhubos.eventozero.util.Framework.LoggerManager;
import br.com.blackhubos.eventozero.util.ThreadUtils;

public final class EventoZero extends JavaPlugin
{

	private static LoggerManager<EventoZero> logger = null;
	private static Configuration config = null;
	private static Configuration config_rankings = null;
	private static Configuration config_points = null;
	private static Configuration config_bans = null;
	private static Configuration config_signs = null;
	private static Configuration config_updater = null;
	private static Configuration config_messages = null;
	private static Storage storage = null;
	private static Updater updater = null;
	private static ShopHandler shopHandler;
	private static KitHandler kitHandler;
	private static EventHandler eventHandler;

	@Override
	public void onEnable()
	{

		new Framework(); // Apenas carrega o WorldGuard e WorldEdit
		EventoZero.config = new Configuration(this, new File(this.getDataFolder(), "config.yml"));
		EventoZero.config_rankings = new Configuration(this, new File(this.getDataFolder(), "ranking.yml"));
		EventoZero.config_points = new Configuration(this, new File(this.getDataFolder(), "points.yml"));
		EventoZero.config_bans = new Configuration(this, new File(this.getDataFolder(), "bans.yml"));
		EventoZero.config_signs = new Configuration(this, new File(this.getDataFolder(), "signs.yml"));
		EventoZero.config_updater = new Configuration(this, new File(this.getDataFolder(), "updater.yml"));
		EventoZero.config_messages = new Configuration(this, new File(this.getDataFolder(), "mensagens.yml"));
		EventoZero.logger = new LoggerManager<EventoZero>(this, new File(this.getDataFolder(), "logs")).init(EventoZero.config.getString("tasks.savelogs"));

		for (final Configuration c : new Configuration[] { EventoZero.config, EventoZero.config_rankings, EventoZero.config_points, EventoZero.config_bans, EventoZero.config_signs, EventoZero.config_updater })
		{
			if (c.copied())
			{
				this.getLogger().info(c.getFile() + " padrão copiada com sucesso..");
			}
		}
		// Registra tudo relacionado ao interpreter
		InterpreterRegister.registerAll();
		EventoZero.shopHandler = new ShopHandler();
		EventoZero.kitHandler = new KitHandler();
		EventoZero.eventHandler = new EventHandler();
		EventoZero.updater = new Updater(this, EventoZero.config_updater);
		EventFactory.loadEvents(this);
		MessageHandler.loadMessages(EventoZero.config_messages);
		EventoZero.kitHandler.loadKits(this);
		EventoZero.shopHandler.loadShops(this);
		this.getServer().getPluginManager().registerEvents(new RankingListener(), this);
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> this.checkEventsToStart(), 20L, 20L);
	}

	public void checkEventsToStart()
	{

	}

	@Override
	public void onDisable()
	{
		// TODO: cancelar eventos ocorrendo
		// TODO: salvar scores se em flatfile; pois é necessário fazer flush do(s) arquivo(s)

		// Remove os listeners do plugin para ter melhor funcionamento com PluginManagers.
		HandlerList.unregisterAll(this);

		// Para todos threads registrados pelo ThreadUtils
		ThreadUtils.stopAllThreads();
	}

	public static void consoleMessage(final String message)
	{
		System.out.println("[EventoZero] " + message);
	}

	/**
	 * Dentro da classe EventoZero há uma variável estática carregada no <code>onEnable()</code> que representa a configuração do plugin, aceitando e usando o formato/charset
	 * utf8.
	 *
	 * @return Retorna uma {@link Configuration} vinda do arquivo config.yml
	 */
	@Override
	public Configuration getConfig()
	{
		return EventoZero.config;
	}

	@SuppressWarnings("unused")
	private void exemploRanking()
	{
		EventoZero.getStorage().depositPlayerRankingPoints("Atom", "Spleef", "vitorias", 1);
		EventoZero.getStorage().withdrawPlayerRankingPoints("atoM", "spleef", "vitorias", 1);
		// NOTA: spleef = Spleef, tal para Atom = atoM. Caso insensitive.
	}

	/**
	 *
	 * @return Retorna a instância do {@link ShopHandler}.
	 */
	public static ShopHandler getShopHandler()
	{
		return EventoZero.shopHandler;
	}

	/**
	 *
	 * @return Retorna a instância do {@link KitHandler}.
	 */
	public static KitHandler getKitHandler()
	{
		return EventoZero.kitHandler;
	}

	/**
	 *
	 * @return Retorna instancia {@link EventHandler}.
	 */
	public static EventHandler getEventHandler()
	{
		return EventoZero.eventHandler;
	}

	/**
	 * Dentro da classe EventoZero há uma variável estática carregada no <code>onEnable()</code> que representa a configuração do plugin, aceitando e usando o formato/charset
	 * utf8.
	 *
	 * @return Retorna uma {@link Configuration} vinda do arquivo config.yml
	 */
	public static Configuration getConfiguration()
	{
		return EventoZero.config;
	}

	/**
	 * @see {@link #getConfiguration()}
	 * @return Retorna uma {@link Configuration} vinda do arquivo points.yml
	 */
	public static Configuration getPointsConfiguration()
	{
		return EventoZero.config_points;
	}

	/**
	 * @see {@link #getConfiguration()}
	 * @return Retorna uma {@link Configuration} vinda do arquivo points.yml
	 */
	public static Configuration getRankingConfiguration()
	{
		return EventoZero.config_rankings;
	}

	/**
	 * @see {@link #getConfiguration()}
	 * @return Retorna uma {@link Configuration} vinda do arquivo signs.yml
	 */
	public static Configuration getSignConfiguration()
	{
		return EventoZero.config_signs;
	}

	/**
	 * @see {@link #getConfiguration()}
	 * @return Retorna uma {@link Configuration} vinda do arquivo bans.yml
	 */
	public static Configuration getBanConfiguration()
	{
		return EventoZero.config_bans;
	}

	/**
	 * Os logs ficam salvos em <code>EventoZero/logs/{data aqui}.txt</code>. Com esta variável, você pode adicionar novos informações aos logs.
	 *
	 * @return A instância única de {@link LoggerManager} para tratar logs.
	 */
	public static LoggerManager<EventoZero> getLoggerService()
	{
		return EventoZero.logger;
	}

	/**
	 * Obtém a implementação da classe Storage, para armazenamento de dados.
	 *
	 * @return A implementação da {@link Storage}.
	 */
	public static Storage getStorage()
	{
		return EventoZero.storage;
	}

	/**
	 * Obtém o Atualizador, veja a documentação na classe {@link Updater}
	 *
	 * @see Updater
	 *
	 * @return Instancia do atualizador.
	 */
	public static Updater getUpdater()
	{
		return EventoZero.updater;
	}

	/**
	 * Obtém a instancia do {@link EventoZero}
	 *
	 * @return Obtém a instancia do {@link EventoZero}
	 */
	public static EventoZero getInstance()
	{
		try
		{
			final PluginDescriptionFile pluginDescriptionFile = new PluginDescriptionFile(EventoZero.class.getResourceAsStream("/plugin.yml"));
			return (EventoZero) Bukkit.getPluginManager().getPlugin(pluginDescriptionFile.getName());
		}
		catch (final InvalidDescriptionException e)
		{
			return null;
		}

	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		if (command.getName().equalsIgnoreCase("eventozero"))
		{
			if (sender.hasPermission("eventozero.admin"))
			{
				if (args.length == 0)
				{
					return this.showHelpTopic(sender, 1);
				}
				else if ((args.length == 1) && Framework.tryInt(args[0], 1, 5))
				{
					return this.showHelpTopic(sender, Integer.parseInt(args[0]));
				}
				else
				{
					if (args[0].equalsIgnoreCase("iniciar"))
					{
						if (args.length == 2)
						{
							final String name = Framework.normalize(args[1].toLowerCase());
							final Optional<Event> event = EventoZero.getEventHandler().getEventByName(name);
							if (event.isPresent())
							{
								if (event.get().getEventState() == EventState.CLOSED) // TODO: verificar se esse 'CLOSED' esta dentro da minha logica
								{
									// TODO: iniciar evento
								}
							}
							else
							{
								// TODO: trocar esse 'event_annoucement_cancelled', botei so para testar o metodo do enum
								MessageHandler.EVENT_ANNOUNCEMENT_CANCELLED.send(new CommandSender[] { sender });
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean showHelpTopic(final CommandSender sender, final int page)
	{
		if (page == 1)
		{
			sender.sendMessage("§2[EventoZero] §aComandos do Sistema (Página 0" + page + "):");
			sender.sendMessage("§2• §a/ez iniciar <evento> §2= Iniciar um evento manualmente");
			sender.sendMessage("§2• §a/ez cancelar <evento> §2= Cancela um evento ocorrendo");
			sender.sendMessage("§2• §a/ez add entrada <evento> §2= Adiciona uma nova entrada");
			sender.sendMessage("§2• §a/ez del entrada <evento> <id> §2= Remove uma entrada");
			sender.sendMessage("§2• §a/ez entradas <evento> §2= Obtém as entradas e suas IDs");
			sender.sendMessage("§2• §a/ez camarote <evento> §2= Adiciona um novo camarote");
			sender.sendMessage("§2• §a/ez pvp <evento> <allow/deny> §2= Define o status de pvp");
			sender.sendMessage("§2• §a/ez mc <evento> <allow/deny> §2= Bloqueia ou permite MC");
			sender.sendMessage("§2• §a/ez tools §2= Abre a caixa de ferramentas do eventozero");
			sender.sendMessage("§2Para mais comandos utilize §a/ez help 2§2.");
		}
		else if (page == 2)
		{
			sender.sendMessage("§2[EventoZero] §aComandos do Sistema (Página 0" + page + "):");
			sender.sendMessage("§2• §a/ez cmd -b <evento> <comando> §2= Bloquear um comando");
			sender.sendMessage("§2• §a/ez cmd -r <evento> <comando> §2= Remover um comando bloqueado");
			sender.sendMessage("§2• §a/ez cmd -c <evento> §2= Limpar comandos bloqueados");
			sender.sendMessage("§2• §a/ez cmd -l <evento> §2= Ver a lista de comandos bloqueados");
			sender.sendMessage("§2• §a/ez potion -b <evento> <id> §2= Bloquear uma poção no evento");
			sender.sendMessage("§2• §a/ez potion -r <evento> <id> §2= Remover uma poção bloqueada");
			sender.sendMessage("§2• §a/ez potion -c <evento> §2= Limpar pots bloqueados");
			sender.sendMessage("§2• §a/ez potion -l <evento> §2= Ver lista de pots bloqueadas");
			sender.sendMessage("§2• §a/ez potion -ids §2= Obter os IDs das poções do jogo");
			sender.sendMessage("§2Para mais comandos utilize §a/ez help 2§3.");
		}
		else if (page == 3)
		{
			sender.sendMessage("§2[EventoZero] §aComandos do Sistema (Página 0" + page + "):");
			sender.sendMessage("§2• §a/ez kit -p <evento> <nome> §2= Permitir que um kit possa ser usado");
			sender.sendMessage("§2• §a/ez kit -b <evento> <nome> §2= Bloquear que um kit possa ser usado");
			sender.sendMessage("§2• §a/ez kit -l <evento> §2= Visualizar kits permitidos e bloqueados");
			sender.sendMessage("§2• §a/ez kit -c <evento> §2= Limpar dados de kits do evento");
			sender.sendMessage("§2• §a/ez kit -r <nome> §2= Remover um kit do plugin (apaga totalmente)");
			sender.sendMessage("§2• §a/ez kit price <valor> §2= Modifica o preço de um kit");
			sender.sendMessage("§2• §a/ez kit armor <valor> §2= Define a armadura do kit como a que você está usando");
			sender.sendMessage("§2• §a/ez kit contents <valor> §2= Define os itens do kit como os que você tem no inventário");
			sender.sendMessage("§2Para mais comandos utilize §a/ez help 2§4.");
		}
		else if (page == 4)
		{
			sender.sendMessage("§2[EventoZero] §aComandos do Sistema (Página 0" + page + "):");
			sender.sendMessage("§2• §a/ez party -e <evento> §2= Ativa as partys no evento");
			sender.sendMessage("§2• §a/ez party -d <evento> §2= Desativa partys no evento");
			sender.sendMessage("§2• §a/ez party -l <evento> <quantia> §2= Define a quantia máxima de jogadores por party");
			sender.sendMessage("§2• §a/ez max <evento> <quantia> §2= Define o número máximo de jogadores (autoinicia)");
			sender.sendMessage("§2• §a/ez min <evento> <quantia> §2= Define o número mínimo de jogadores para poder iniciar o evento");
			sender.sendMessage("§2• §a/ez autostop <evento> <tempo> §2= Define o tempo para terminar o evento se não houver ganhadores.");
			sender.sendMessage("§2• §a/ez setengine <evento> <arquivo.sc> §2= Definir uma engine para um evento");
			sender.sendMessage("§2• §a/ez placements <evento> <quantia> §2= Define o limite de colocações");
			sender.sendMessage("§2Para mais comandos utilize §a/ez help 2§5.");
		}
		else if (page == 5)
		{
			sender.sendMessage("§2[EventoZero] §aComandos do Sistema (Página 0" + page + "):");
			sender.sendMessage("§2• §a/ez points add <player> <quantia> §2= Creditar pontos à um jogador");
			sender.sendMessage("§2• §a/ez points rem <player> <quantia> §2= Debitar pontos de um jogador");
			sender.sendMessage("§2• §a/ez points set <player> <quantia> §2= Modificar pontos de um jogador");
			sender.sendMessage("§2• §a/ez points coins <quantia> §2= Define quanto 01 ponto vale em coins");
			sender.sendMessage("§2• §a/ez updates §2= Apenas verifica se há novos updats");
			sender.sendMessage("§2• §a/ez upgrade §2= Verifica e aplica o update automaticamente se existir");
			sender.sendMessage("§2• §a/ez reload §2= Faz reload completo no plugin");
			sender.sendMessage("§2• §a/ez disable §2= Desativa o plugin completamente");
		}
		else
		{
			sender.sendMessage("§cPara visualizar os comandos disponíveis, utilize §4/ez help§c.");
		}

		return true;
	}

}
