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
package br.com.blackhubos.eventozero.handlers;

import java.io.File;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import br.com.blackhubos.eventozero.util.Framework.Configuration;

public final class MessageHandler
{

	private static Configuration flatfile = null;
	public static MessageRecipient SEM_PERMISSAO = new MessageRecipient("SEM_PERMISSÃO").defaultValue("&7Você não está autorizado a fazer isto.").build();

	/**
	 * Recarrega todas as mensagens da classe MessageHandler, carregando todas as keys encontradas pelo arquivo fornecido na configuração.
	 *
	 * @param flatfile Uma {@link Configuration} para fazer o carregamento em UTF-8 das mensagens.
	 * @since EventoZero v1.0.1-ALPHA
	 */
	public static void setFlatfile(final File file)
	{
		MessageHandler.flatfile = new Configuration(file);
	}

	public final static class MessageRecipient implements Serializable
	{

		private static final long serialVersionUID = 2951277980455897571L;
		private String key;
		private String string;
		private ConcurrentHashMap<String, String> replacements;

		/**
		 * Constrói primariamente o recipiente da mensagem.
		 *
		 * @param key A key é a usada na config. Se não existe, coloque null.
		 */
		public MessageRecipient(final String key)
		{
			this.key = key;
			this.string = "";
		}

		/**
		 * Envia a mensagem para um jogador e/ou console. CommandSender é compatível com Console.
		 *
		 * @param player O jogador e/ou console para enviar a mensagem.
		 * @param replacements Caso não tenha usado o método replacements(String...), poderá usar aqui. É opcional.
		 */
		public void sendMessage(final CommandSender player, final String... replacements)
		{
			this.replacements(replacements);
			final Vector<String> prepared = this.getText();
			for (final String line : prepared)
			{
				player.sendMessage(line);
			}
		}

		/**
		 * Caso alguma variável esteja nula ou incorreta, este método corrige para evitar possíveis casos errôneos.
		 *
		 * @return Retorna o {@link MessageRecipient} preparado.
		 */
		public MessageRecipient build()
		{
			if ((this.string == null) || this.string.isEmpty())
			{
				this.string = "Default message not defined";
			}

			if ((this.key == null) || this.key.isEmpty())
			{
				this.key = "null";
			}

			if (this.replacements == null)
			{
				this.replacements = new ConcurrentHashMap<String, String>();
			}

			return this;
		}

		/**
		 * Prepara o texto e colore ele (substituindo os códigos '&'), além de fazer replaces pelos objetos informados anteriormente ou pelo
		 * <code>sendMessage(Player, String... replaces)</code> por seus respectivos valores.
		 *
		 * @return Retorna uma {@link String} pronta para ser enviada.
		 */
		public Vector<String> getText()
		{
			final boolean exists = (((this.key != null) && !this.key.isEmpty() && (MessageHandler.flatfile != null) && MessageHandler.flatfile.contains(this.key)));
			final Vector<String> messages = new Vector<String>();
			if (exists)
			{
				if (MessageHandler.flatfile.isList(this.key))
				{
					for (final String line : MessageHandler.flatfile.getStringList(this.key))
					{
						messages.add(line);
					}
				}
				else
				{
					messages.add(MessageHandler.flatfile.getString(this.key));
				}
			}
			else
			{
				messages.add(this.string);
			}

			for (int i = 0; i < messages.size(); i++)
			{
				String text = messages.get(i);
				for (final Entry<String, String> kv : this.replacements.entrySet())
				{
					text = text.replace(kv.getKey(), kv.getValue());
				}
				messages.set(i, ChatColor.translateAlternateColorCodes('&', text));
			}

			return messages;
		}

		/**
		 * Prepara a mensagem padrão definida pelo sistema para essa mensagem, caso não haja na config, essa geralmente é usada. Esse processo colore a mensagem.
		 *
		 * @return Retorna uma {@link String} que representa uma mensagem padrão para a key.
		 */
		public String defaultValue()
		{
			return ChatColor.translateAlternateColorCodes('&', this.string);
		}

		/**
		 * Este método serve para por os replacements PADRÕES da mensagem. O formato é <code>key:valor</code>, note que você pode e tem liberdade para usar : durante a
		 * formatação. Nós apenas capturamos o PRIMEIRO :, os demais são ignorados.
		 *
		 * @param replaces As {@link String}s para fazer o replace, no formato citado acima.
		 * @return Retorna a {@link MessageRecipient} agora com os replaces escolhidos.
		 */
		public MessageRecipient replacements(final String... replaces)
		{
			this.replacements.clear();
			for (final String index : replaces)
			{
				final int i = index.indexOf(":");
				final String key = index.substring(0, i - 1);
				final String value = index.substring(i + 1, index.length());
				this.replacements.put(key, value);
			}

			return this;
		}

		/**
		 * Define o valor padrão para esta key. Caso haja um problema, este texto será usado.
		 *
		 * @param texto Valor padrão
		 * @return Retorna o {@link MessageRecipient} agora com o valor padrão definido.
		 */
		public MessageRecipient defaultValue(final String texto)
		{
			this.string = texto;
			return this;
		}
	}

}
