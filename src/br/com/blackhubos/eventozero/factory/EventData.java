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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EventData {	
	
	private final ConcurrentMap<String, Object> data;
	
	public EventData(){
		this.data = new ConcurrentHashMap<>();
	}
	
	/**
	 * @param key
	 * @return Retorna {@link Object}.
	 */
	
	public <T> T getData(String key){
		if(!data.containsKey(key)){
			throw new IllegalArgumentException("Key is not valid");
		}
		return (T) this.data.get(key);
	}
	
	/**
	 * @param key
	 * @return Retorna {@link Boolean}
	 */
	public boolean containsKey(String key){
		return this.data.containsKey(key);
	}
	
	/**
	 * @param key
	 * @param data
	 */
	public void updateData(String key, Object data){
		this.data.putIfAbsent(key, data);
	}

}
