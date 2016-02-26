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
package br.com.blackhubos.eventozero.factory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class EventHandler {

    private final Set<Event> events;

    public EventHandler() {
        this.events = new HashSet<>();
    }

    public Optional<Event> getEventByName(final String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name),
                "name cannot be null or empty");

        return getEvents()
                .parallelStream()
                .filter(e -> e.getName().equals(name))
                .findAny();
    }

    public Optional<Event> getEventByPlayer(final Player player) {
        return getEvents()
                .parallelStream()
                .filter(e -> e.hasPlayerJoined(player))
                .findAny();
    }

    public Set<Event> getEvents() {
        return this.events;

    }

    public List<Event> getEventsByState(EventState state) {
        List<Event> a = new ArrayList<>();
        for (Event e : events) {
            if (e.getState().equals(state))
                a.add(e);
        }
        return a;
    }

    public int getEventsSizeOpening() {
        return getEventsByState(EventState.OPENED).size();
    }

    public int getEventsSizeClosed() {
        return getEventsByState(EventState.CLOSED).size();
    }

    public int getEventsSizeOccuring() {
        return getEventsByState(EventState.OCCURRING).size();
    }

    public int getEventsSizeEnding() {
        return getEventsByState(EventState.ENDING).size();
    }

    public int getEventsSizePreStarted() {
        return getEventsByState(EventState.PRESTARTED).size();
    }

    public void loadEvent(final Event event) {
        Preconditions.checkNotNull(event, "Event is null");
        this.events.add(event);
    }
}
