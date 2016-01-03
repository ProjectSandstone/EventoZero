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

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.blackhubos.eventozero.storage.Storage;
import br.com.blackhubos.eventozero.util.Framework;
import br.com.blackhubos.eventozero.util.Framework.Configuration;
import br.com.blackhubos.eventozero.util.Framework.LoggerManager;

public final class EventoZero extends JavaPlugin
{

	private static Configuration config = null;
	private static LoggerManager<EventoZero> logger = null;
	private static Storage storage = null;

	@Override
	public void onEnable()
	{
		new Framework(); // Apenas carrega o WorldGuard e WorldEdit
		EventoZero.config = new Configuration(this, new File(this.getDataFolder(), "config.yml"));

		// TODO: atenção aqui, preciso mudar o '5m'. Os tempo dos tasks devem ser configuráveis!
		EventoZero.logger = new LoggerManager<EventoZero>(this, new File(this.getDataFolder(), "logs")).init("5m");

		// Isso verifica se a configuração não existia e foi copiada do jar para a pasta
		if (EventoZero.config.copied())
		{
			this.getLogger().info("Configuração padrão copiada com sucesso!");
		}
	}

	@Override
	public void onDisable()
	{
		// TODO: cancelar eventos ocorrendo
		// TODO: salvar scores se em flatfile; pois é necessário fazer flush do(s) arquivo(s)

		// Remove os listeners do plugin para ter melhor funcionamento com PluginManagers.
		HandlerList.unregisterAll(this);
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
		EventoZero.getStorage().depositPlayerRankingPoints("Atom", "Spleef", 1, 1);
		EventoZero.getStorage().withdrawPlayerRankingPoints("atoM", "spleef", 1, 1);
		// NOTA: spleef = Spleef, tal para Atom = atoM. Caso insensitive.
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

}
