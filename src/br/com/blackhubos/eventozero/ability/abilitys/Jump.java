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
package br.com.blackhubos.eventozero.ability.abilitys;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.ability.Ability;
import br.com.blackhubos.eventozero.factory.Event;
import br.com.blackhubos.eventozero.handlers.AbilityHandler;

public final class Jump extends Ability implements Listener {

    public Jump(final long cooldown) {
        super("DoubleJump", cooldown);
        final EventoZero plugin = (EventoZero) Bukkit.getPluginManager().getPlugin("EventoZero");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean tryUse(final Player player) {
        Event event = EventoZero.getEventHandler().getEventByPlayer(player);
        if (this.canUse(player.getName()) && (event != null && (event.getAbilitys().contains(this) || (event.getPlayersRemaining().contains(player) && event.getEventData().getData(player.getName() + ".ability").equals(this))))) {
            this.forceUse(player);
            this.updateTime(player.getName());
            return true;
        }
        return false;
    }

    @Override
    public void forceUse(final Player player) {
        player.setVelocity(new Vector(0, 0.5, 0));
        player.setFallDistance(-10);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onToggle(PlayerMoveEvent event) {
        Location locationTo = event.getTo();
        Location locationFrom = event.getFrom();
        if (locationTo.getBlockY() > locationFrom.getBlockY() && (locationTo.getBlock().getType() == Material.AIR)) {
            tryUse(event.getPlayer());
        }
    }
}
