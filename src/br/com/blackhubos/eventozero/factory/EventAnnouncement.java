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

public class EventAnnouncement {

    private final Event event;
    private int seconds;
    private long time;

    public EventAnnouncement(final Event event, final int seconds) {
        this.event = event;
        this.seconds = seconds;
        this.time = 0;
    }

    public EventAnnouncement tryAnnouncement() {
        if (event.getState() == EventState.OPENED && (time + (seconds * 1000) < System.currentTimeMillis())) {
            forceAnnouncement();
            updateTime();
        }
        return this;
    }

    public EventAnnouncement forceAnnouncement() {
        
        return this;
    }

    public EventAnnouncement updateTime() {
        this.time = System.currentTimeMillis();
        return this;
    }

}
