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

import br.com.blackhubos.eventozero.handlers.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EventAnnouncement {

    private final Event event;
    private int quantity;
    private int seconds;
    private int step;
    private long time;

    public EventAnnouncement(final Event event, final int quantity, final int seconds) {
        this.event = event;
        this.seconds = seconds;
        this.quantity = quantity;
        this.step = 0;
        this.time = 0;
    }

    public EventAnnouncement tryAnnouncement() {
        if (event.getState() == EventState.OPENED && step <= quantity && (time + (seconds * 1000) < System.currentTimeMillis())) {
            forceAnnouncement();
            updateTime();
            updateStep();
        }
        return this;
    }

    public EventAnnouncement forceAnnouncement() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MessageHandler.ANUNCIOS_ABERTO.send(player); // COLOCAR OS REPLACEMENTS
        }
        if(step >= quantity) {
            event.forceStart();
            step = 0;
            // INICIAR OU APENAS FAZER UMA CONTAGEM ANTES DE INICIAR
        }
        return this;
    }

    public EventAnnouncement updateTime() {
        this.time = System.currentTimeMillis();
        return this;
    }

    public EventAnnouncement updateStep() {
        step = (step >= quantity ? 0 : step++);
        return this;
    }

    public EventAnnouncement setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public EventAnnouncement setSeconds(int seconds) {
        this.seconds = seconds;
        return this;
    }

}
