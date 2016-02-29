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
package br.com.blackhubos.eventozero.ability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import br.com.blackhubos.eventozero.handlers.AbilityHandler;

public abstract class Ability {

    private final String name;
    private final String SEPARATOR;
    private final long cooldown;

    private final Map<String, Long> cooldowns;

    public Ability(final String name, final long cooldown) {
        this.name = name;
        this.SEPARATOR = ".";
        this.cooldown = cooldown;
        this.cooldowns = new ConcurrentHashMap<>();
        
        AbilityHandler.loadAbility(this);
    }

    public String getName() {
        return this.name;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public long getLastTime(final String name) {
        return (cooldowns.containsKey(name) ? cooldowns.get(name) : 0);
    }

    public long getRemainingTime(final String name) {
        return (cooldowns.containsKey(name) 
        		? getLastTime(name) - (getCooldown() * 1000) : 0);
    }

    public long getRemainingTimePositive(final String name) {
        return (this.getRemainingTime(name) >= 0 ? this.getRemainingTime(name) : 0);
    }

    public boolean canUse(final String name) {
        return (this.getRemainingTimePositive(name) == 0);
    }

    public Ability updateTime(final String name) {
        cooldowns.put(name, System.currentTimeMillis());
        return this;
    }

    public Ability updateTime(final String username, final long lastTime) {
        cooldowns.put(this.name + this.SEPARATOR + username , lastTime);
        return this;
    }

    public abstract boolean tryUse(final Player player);

    public abstract void forceUse(final Player player);
}
