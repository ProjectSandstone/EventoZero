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

import br.com.blackhubos.eventozero.factory.ItemFactory;
import br.com.blackhubos.eventozero.util.Framework;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hugo
 */
public class ChestReward implements Reward {

    private Location location;
    private final ItemStack[] stacks;
    private final boolean replaceOtherItems;

    public ChestReward(String loc, List<String> l, boolean replaceOtherItems) {
        this.stacks = new ItemStack[54];
        this.location = Framework.toLocation(loc);
        this.replaceOtherItems = replaceOtherItems;
        for (int i = 0; i < l.size(); i++) {
            stacks[i] = new ItemFactory(l.get(i), null).getPreparedItem();
        }
    }

    @Override
    public ChestReward giveTo(Player player) {
        this.location = player.getLocation();

        give();
        return this;
    }

    @Override
    public ChestReward give() {
        Block block = location.getWorld().getBlockAt(location);
        setChest(block);

        Inventory inventory = ((Chest) block).getBlockInventory();
        if (replaceOtherItems)
            inventory.setContents(stacks);
        else
            inventory.addItem(stacks);
        return this;
    }

    public ChestReward clearChest() {
        Block block = location.getWorld().getBlockAt(location);
        setChest(block);

        Chest chest = (Chest) block;
        chest.getBlockInventory().clear();
        return this;
    }

    public ChestReward setChest(Block block) {
        if (block.getType() != Material.CHEST && !(block instanceof ChestReward))
            block.setType(Material.CHEST);
        return this;
    }

}
