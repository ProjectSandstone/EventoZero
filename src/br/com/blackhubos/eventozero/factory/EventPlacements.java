/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.blackhubos.eventozero.factory;

import br.com.blackhubos.eventozero.rewards.ChestReward;
import org.bukkit.entity.Player;

/**
 *
 * @author Hugo
 */
public class EventPlacements {

    private Player[] players;

    private int placements;
    private int[] money;
    private int[] points;
    private ChestReward[] chestRewards;

    public EventPlacements(int placements) {
        this.placements = placements;
        this.players = new Player[placements];
        this.money = new int[placements];
        this.points = new int[placements];
        this.chestRewards = new ChestReward[placements];

    }

    public Player[] getPlaced() {
        return this.players;
    }
    
    public int getPlacements() {
        return this.placements;
    }

    public int[] getPoints() {
        return this.points;
    }

    public int[] getMoney() {
        return this.points;
    }

    public ChestReward[] getChestReward() {
        return this.chestRewards;
    }

    public EventPlacements setPlayer(int index, Player player) {
        this.players[index] = player;
        return this;
    }

    public EventPlacements setPoints(int index, int point) {
        this.points[index] = point;
        return this;
    }

    public EventPlacements setMoney(int index, int money) {
        this.money[index] = money;
        return this;
    }

    public EventPlacements setChestReward(int index, ChestReward chestReward) {
        this.chestRewards[index] = chestReward;
        return this;
    }

    public EventPlacements giveRewards() {
        
        return this;
    }

}
