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

import br.com.blackhubos.eventozero.rewards.ChestReward;
import br.com.blackhubos.eventozero.rewards.MoneyReward;
import br.com.blackhubos.eventozero.rewards.PointReward;
import org.bukkit.entity.Player;

/**
 *
 * @author Hugo
 */
public class EventPlacements {

    private Player[] players;

    private int placements;
    private MoneyReward[] moneyRewards;
    private PointReward[] pointRewards;
    private ChestReward[] chestRewards;

    public EventPlacements(int placements) {
        this.placements = placements;
        this.players = new Player[placements];
        this.moneyRewards = new MoneyReward[placements];
        this.pointRewards = new PointReward[placements];
        this.chestRewards = new ChestReward[placements];

    }

    public Player[] getPlaced() {
        return this.players;
    }

    public int getPlacements() {
        return this.placements;
    }

    public PointReward[] getPoints() {
        return this.pointRewards;
    }

    public MoneyReward[] getMoney() {
        return this.moneyRewards;
    }

    public ChestReward[] getChestReward() {
        return this.chestRewards;
    }

    public EventPlacements setPlayer(int index, Player player) {
        this.players[index] = player;
        return this;
    }

    public EventPlacements setPoints(int index, PointReward point) {
        this.pointRewards[index] = point;
        return this;
    }

    public EventPlacements setMoney(int index, MoneyReward money) {
        this.moneyRewards[index] = money;
        return this;
    }

    public EventPlacements setChestReward(int index, ChestReward chestReward) {
        this.chestRewards[index] = chestReward;
        return this;
    }

    public EventPlacements clearPlayers() {
        this.players = new Player[placements];
        return this;
    }

    public EventPlacements giveReward(Player player) {
        for (int i = 0; i < players.length; i++) {
            Player plac = players[i];
            if ((plac != null) && plac.equals(player))
                giveReward(i, plac);
        }
        return this;
    }

    public EventPlacements giveReward(int i, Player plac) {
        if (plac != null) {
            pointRewards[i].giveTo(plac);
            moneyRewards[i].giveTo(plac);
            chestRewards[i].giveTo(plac);
        }
        return this;
    }

    public EventPlacements giveRewards() {
        for (int i = 0; i < players.length; i++) {
            Player plac = players[i];
            if (plac != null)
                giveReward(i, plac);
        }
        clearPlayers();
        return this;
    }
    
    /**
     * 
     * @return {@code true} caso tenha slot vazio. false caso não
     */
    public boolean hasPlacementSlotFree() {
        return getPlacemetFirstSlot() != -1;
    }
    
    /**
     * 
     * @return Retorna slot livre, caso esteja todos ocupado retorna -1
     */
    public int getPlacemetFirstSlot() {
        for(int i = 0; i < players.length; i++) {
            if(players[i] == null) {
                return i;
            }
        }
        return -1;
    }

}
