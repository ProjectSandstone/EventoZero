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
import java.util.Set;
import java.util.Vector;

import org.bukkit.Location;

import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

/**
 * Implementação default do {@link EventBuilder}
 */
class EventBuilderImpl implements EventBuilder {

	/*
	 * Chaves que iniciem com 'eventdata.' são para o EventData.
	 */
	private Map<String, Object> data;

	public EventBuilderImpl() {
		data = Maps.newHashMap();
	}

	@Override
	public EventBuilder name(String name) {
		checkArgument(!isNullOrEmpty(name), "name cannot be null or empty");

		data.put("name", name);

		return this;
	}

	@Override
	public EventBuilder partyEnabled(boolean enabled) {
		data.put("partyEnabled", enabled);

		return this;
	}

	@Override
	public EventBuilder maxPlayers(int maxPlayers) {
		checkArgument(maxPlayers > 0, "maxPlayers must be greater than zero.");

		data.put("maxPlayers", maxPlayers);

		return this;
	}

	@Override
	public EventBuilder minPlayers(int minPlayers) {
		checkArgument(minPlayers > 0, "minPlayers must be greater than zero.");

		data.put("minPlayers", minPlayers);

		return this;
	}

	@Override
	public EventBuilder partySize(int partySize) {
		checkArgument(partySize > 0, "partySize must be greater than zero.");

		data.put("partySize", partySize);

		return this;
	}

	@Override
	public EventBuilder displayName(String displayName) {
		checkArgument(!isNullOrEmpty(displayName), "displayName cannot be null or empty");

		data.put("displayName", displayName);

		return this;
	}

	@Override
	public EventBuilder description(String desc) {
		checkArgument(!isNullOrEmpty(desc), "desc cannot be null or empty");

		data.put("description", desc);

		return this;
	}

	@Override
	public EventBuilder timeToStop(int seconds) {
		checkArgument(seconds > 0, "seconds must be greater than zero.");

		data.put("timeToStop", seconds);

		return this;
	}

	@Override
	public EventBuilder points(Map<Integer, Integer> points) {
		checkNotNull(points, "points cannot be null");

		data.put("points", points);

		return this;
	}

	@Override
	public EventBuilder money(Map<Integer, Integer> money) {
		checkNotNull(money, "money cannot be null");

		data.put("money", money);

		return this;
	}

	@Override
	public EventBuilder placements(int placements) {
		checkArgument(placements > 0, "placements must be greater than zero.");

		data.put("placements", placements);

		return this;
	}

	@Override
	public EventBuilder abilitys(Set<String> abilitys) {
		checkNotNull(abilitys, "abilitys cannot be null.");

		data.put("abilitys", abilitys);

		return this;
	}

	@Override
	public EventBuilder shops(Set<String> shops) {
		checkNotNull(shops, "shops cannot be null.");

		data.put("shops", shops);

		return this;
	}

	@Override
	public EventBuilder lobbyLocations(Set<Location> locations) {
		checkNotNull(locations, "locations cannot be null.");

		data.put("eventdata.teleport.lobby", new Vector<>(locations));

		return this;
	}

	@Override
	public EventBuilder spawnLocations(Set<Location> locations) {
		checkNotNull(locations, "locations cannot be null.");

		data.put("eventdata.teleport.spawn", new Vector<>(locations));

		return this;
	}

	@Override
	public EventBuilder safeInventory(boolean safeInventory) {
		data.put("eventdata.options.enables.safe_inventory", safeInventory);

		return this;
	}

	@Override
	public Event build() {
		final Event ret = new Event(this.<String>getDataOrDefault("key", "undefinedName"));
		final EventData eventData = ret.getData();

		ret.updateDescription(this.<String>getDataOrDefault("description", ""));
		ret.updateDisplayName(this.<String>getDataOrDefault("displayName", ret.getName()));

		data.forEach((k, v) -> {
			if (k.startsWith("eventdata.")) {
				eventData.updateData(k.substring(10), v);
			}
		});

		return ret;
	}

	@SuppressWarnings("unchecked")
	private <T> T getData(String key) {
		return (T) data.get("key");
	}

	@SuppressWarnings("unchecked")
	private <T> T getDataOrDefault(String key, T defaultValue) {
		return (T) data.getOrDefault(key, defaultValue);
	}
}
