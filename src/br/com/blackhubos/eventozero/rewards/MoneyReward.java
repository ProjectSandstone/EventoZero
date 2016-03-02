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
package br.com.blackhubos.eventozero.rewards;

import org.bukkit.entity.Player;

/**
 *
 * @author Hugo
 */
public class MoneyReward implements Reward {
    
    // NECESSITO DO HOOK DO VAULT OU DE ALGUMA ECONOMIA.
    // SUGESTÃO REALIZAR O HOOK DE ECONOMIA CENTRAL, EXEMPLO ECONOMYHOOK.giveMoney(Player player, Double money)
    
    private final int money;
    
    public MoneyReward(int money) {
        this.money = money;
    }

    @Deprecated
    @Override
    public MoneyReward give() {
        
        return this;
    }

    @Override
    public MoneyReward giveTo(Player player) {
        return this;
    }
    
}
