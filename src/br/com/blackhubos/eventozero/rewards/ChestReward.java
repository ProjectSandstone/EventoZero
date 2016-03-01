/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.blackhubos.eventozero.rewards;

import br.com.blackhubos.eventozero.factory.ItemFactory;
import br.com.blackhubos.eventozero.util.Framework;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hugo
 */
public class ChestReward {

    private final Location location;
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

    public ChestReward updateChest() {
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
