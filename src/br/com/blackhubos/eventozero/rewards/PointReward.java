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

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.factory.Event;
import java.util.Optional;
import org.bukkit.entity.Player;

/**
 *
 * @author Hugo
 */
public class PointReward implements Reward {

    private final int points;

    public PointReward(int points) {
        this.points = points;
    }

    @Deprecated
    @Override
    public PointReward give() {
        
        return this;
    }

    @Override
    public PointReward giveTo(Player player) {
        Optional<Event> optional = EventoZero.getEventHandler().getEventByPlayer(player);
        if (optional.isPresent()) {
            Event event = optional.get();
            EventoZero.getStorage().depositPlayerRankingPoints(player.getName(), event.getName(), "nao sei sei", points);
            
        }
        
        return this;
    }

}
