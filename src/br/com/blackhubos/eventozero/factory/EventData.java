/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright � 2016 BlackHub OS and contributors.
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
package br.com.blackhubos.eventozero.factory;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ForwardingMap;

public class EventData extends ForwardingMap<String, Object> {

	/**
	 * @param key Chave na qual o dado está associado.
	 * @return Retorna um dado salvo anteriormente.
	 * @throws NoSuchElementException
	 *             Caso não exista um dado salvo com a {@code key}
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(final String key) {
		if (!containsKey(key)) {
			throw new NoSuchElementException("No such element for " + key);
		}
		return (T) get(key);
	}

	/**
	 * @param key
	 * @return Retorna {@link Boolean}
	 */
	public boolean containsKey(final String key) {
		return containsKey(key);
	}

	/**
	 * @param key
	 * @param data
	 * @return
	 */
	public EventData updateData(final String key, final Object data) {
		putIfAbsent(key, data);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public EventData removeKeyStartWith(final String name) {
		for (final ConcurrentMap.Entry<String, Object> entry : entrySet()) {
			if (entry.getKey().startsWith(name)) {
				remove(entry.getKey());
			}
		}
		return this;
	}

	@Override
	protected Map<String, Object> delegate() {
		return new ConcurrentHashMap<>();
	}
	
}
