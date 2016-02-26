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
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.blackhubos.eventozero.chat.interpreter.game.register.InterpreterRegister;
import br.com.blackhubos.eventozero.exceptions.CuboidParsingException;
import br.com.blackhubos.eventozero.factory.Event;
import br.com.blackhubos.eventozero.factory.EventFactory;
import br.com.blackhubos.eventozero.factory.EventHandler;
import br.com.blackhubos.eventozero.factory.EventState;
import br.com.blackhubos.eventozero.handlers.AnnouncementHandler;
import br.com.blackhubos.eventozero.handlers.KitHandler;
import br.com.blackhubos.eventozero.handlers.MessageHandler;
import br.com.blackhubos.eventozero.handlers.ShopHandler;
import br.com.blackhubos.eventozero.hook.Hooks;
import br.com.blackhubos.eventozero.listeners.CuboidListener;
import br.com.blackhubos.eventozero.listeners.EventListener;
import br.com.blackhubos.eventozero.listeners.RankingListener;
import br.com.blackhubos.eventozero.storage.Storage;
import br.com.blackhubos.eventozero.updater.Updater;
import br.com.blackhubos.eventozero.util.Framework;
import br.com.blackhubos.eventozero.util.Framework.Configuration;
import br.com.blackhubos.eventozero.util.Framework.LoggerManager;
import br.com.blackhubos.eventozero.util.ThreadUtils;
import io.github.bktlib.command.CommandManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EventoZero extends JavaPlugin
{

	// 'regex_as' testado, funciona!
	private static final Pattern regex_as = Pattern.compile("^\\s*([0-6]{1,1}|\\*{1,1})\\s+(?:([0-9]{1,2})\\s*:\\s*([0-9]{1,2}))\\s*$");
	private static LoggerManager<EventoZero> logger;
	private static Configuration config ;
	private static Configuration rankingsConfig;
	private static Configuration pointsConfig;
	private static Configuration bansConfig;
	private static Configuration updaterConfig;
	private static Configuration messagesConfig;
	private static Storage storage;
	private static Updater updater;
	private static ShopHandler shopHandler;
	private static KitHandler kitHandler;
	private static EventHandler eventHandler;
        private static AnnouncementHandler announcementHandler;
	private static EventoZero instance;
	private static CommandManager commandManager;

	@Override
	public void onEnable()
	{
		instance = this;
		
		new Framework(); // Apenas carrega o WorldGuard e WorldEdit
		
		config = new Configuration(this, new File(this.getDataFolder(), "config.yml"));
		rankingsConfig = new Configuration(this, new File(this.getDataFolder(), "ranking.yml"));
		pointsConfig = new Configuration(this, new File(this.getDataFolder(), "points.yml"));
		bansConfig = new Configuration(this, new File(this.getDataFolder(), "bans.yml"));
		updaterConfig = new Configuration(this, new File(this.getDataFolder(), "updater.yml"));
		messagesConfig = new Configuration(this, new File(this.getDataFolder(), "mensagens.yml"));
		logger = new LoggerManager<EventoZero>(this, new File(this.getDataFolder(), "logs")).init(EventoZero.config.getString("tasks.savelogs"));
		commandManager = CommandManager.newInstance( this );
		
		Hooks.hookAll();
		
		for (final Configuration c : new Configuration[] { EventoZero.config, EventoZero.rankingsConfig, EventoZero.pointsConfig, EventoZero.bansConfig, EventoZero.updaterConfig })
		{
			if (c.copied())
			{
				this.getLogger().info(c.getFile() + " padrão copiada com sucesso..");
			}
		}

		MessageHandler.loadMessages(EventoZero.messagesConfig);
		InterpreterRegister.registerAll(this); // Registra tudo relacionado ao interpreter
		shopHandler = new ShopHandler();
		kitHandler = new KitHandler();
		eventHandler = new EventHandler();
		updater = new Updater(this, EventoZero.updaterConfig);
		
		/*
		 * por enquanto só tem essa classe de comando, caso adicionei outros
		 * comandos é só mudar pra registerAll
		 */
		commandManager.register( EZCommand.class );
		
		try
		{
			// TODO: falta testar novo sistema de carregar cuboids
			EventFactory.loadEvents(this);
		}
		catch (final CuboidParsingException e)
		{
			// CuboidParsingException: chamado se houver falhas ao processar cuboids definidos
			this.getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
			return;
		}

		kitHandler.loadKits(this);
		shopHandler.loadShops(this);
		this.getServer().getPluginManager().registerEvents(new RankingListener(), this);
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new CuboidListener(), this);
		// this.getServer().getPluginManager().registerEvents(new PlayerInsideCuboidEvent(), this); TODO: PlayerInsideCuboidEvent#93
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> this.checkEventsToStart(), 5 * 20L, this.getConfig().getInt("tasks.autostart", 55) * 20L);
	}

	public void checkEventsToStart()
	{
		final TimeZone time = TimeZone.getTimeZone(this.getConfig().getString("timezone", "America/Sao_Paulo"));
		final Calendar c = Calendar.getInstance(time);
		final String path = "times";
		EventoZero.getEventHandler().getEvents().parallelStream().forEach((evento) ->
		{
			evento.getConfig().getConfigurationSection(path).getKeys(false).parallelStream().forEach((linha) ->
			{
				final Matcher m = EventoZero.regex_as.matcher(linha);
				if (m.matches())
				{
					final int dia = Integer.parseInt(m.group(1));
					final int hora = Integer.parseInt(m.group(2));
					final int mins = Integer.parseInt(m.group(3));
					if (c.get(Calendar.DAY_OF_MONTH) == dia)
					{
						if ((c.get(Calendar.HOUR_OF_DAY) == hora) && (c.get(Calendar.MINUTE) == mins))
						{
							if (evento.getState() == EventState.CLOSED)
							{
								evento.start();
							}
						}
					}
				}
			});
		});
	}

	@Override
	public void onDisable()
	{
            for(Event e :eventHandler.getEvents()) {
                if(e.getState().equals(EventState.OCCURRING) || e.getState().equals(EventState.PRESTARTED)) {
                    e.forceStop();
                }
                e.getConfig().set("signs.location", Framework.fromLocation(e.getSignsLocation()));
                try {
                    e.getConfig().save(e.getConfig().getFile());
                } catch (IOException ex) {
                    Logger.getLogger(EventoZero.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
	 * Dentro da classe EventoZero há uma variável estática carregada no
	 * <code>onEnable()</code> que representa a configuração do plugin,
	 * aceitando e usando o formato/charset utf8.
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
	 * Dentro da classe EventoZero há uma variável estática carregada no
	 * <code>onEnable()</code> que representa a configuração do plugin,
	 * aceitando e usando o formato/charset utf8.
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
		return EventoZero.pointsConfig;
	}

	/**
	 * @see {@link #getConfiguration()}
	 * @return Retorna uma {@link Configuration} vinda do arquivo points.yml
	 */
	public static Configuration getRankingConfiguration()
	{
		return EventoZero.rankingsConfig;
	}

	/**
	 * @see {@link #getConfiguration()}
	 * @return Retorna uma {@link Configuration} vinda do arquivo bans.yml
	 */
	public static Configuration getBanConfiguration()
	{
		return EventoZero.bansConfig;
	}

	/**
	 * Os logs ficam salvos em <code>EventoZero/logs/{data aqui}.txt</code>. Com
	 * esta variável, você pode adicionar novos informações aos logs.
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
		return instance;
	}
        
        public static boolean startAnnouncementHandler() {
            if(announcementHandler == null) {
                announcementHandler = new AnnouncementHandler();
                return true;
            }
            return false;
        }
        
        public static boolean closeAnnouncementHandler() {
            if(announcementHandler != null) {
                announcementHandler.destroy();
                announcementHandler = null;
                return true;
            }
            return false;
        }

}
