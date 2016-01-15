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
package br.com.blackhubos.eventozero.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public final class Party {

    private final List<Player> members;
    private final List<Player> invited;
    private final int maxPlayers;

    public Party(final int maxPlayers) {
        this.members = new ArrayList<>();
        this.invited = new ArrayList<>();
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public Player getOwner() {
        return this.members.get(0);
    }

    public List<Player> getMembers() {
        return this.members;
    }

    public List<Player> getInvited() {
        return this.invited;
    }

    public Party addMember(final Player player) {
        this.members.add(player);
        cancelInvite(player);
        return this;
    }

    public Party removeMember(final Player player) {
        this.members.remove(player);
        return this;
    }

    public Party invite(final Player player) {
        this.invited.add(player);
        return this;
    }

    public Party cancelInvite(final Player player) {
        this.invited.remove(player);
        return this;
    }
}
