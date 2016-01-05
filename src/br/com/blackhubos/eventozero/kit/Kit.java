package br.com.blackhubos.eventozero.kit;

import org.bukkit.inventory.ItemStack;

import br.com.blackhubos.eventozero.ability.Ability;
import br.com.blackhubos.eventozero.shop.ShopItem;
import org.bukkit.entity.Player;

public class Kit extends ShopItem implements Cloneable {

    private final ItemStack icon;

    private ItemStack[] contents;
    private ItemStack[] armorContents;

    private Ability ability;

    public Kit(String name, ItemStack icon) {
        super(name, icon);
        this.icon = icon;
        this.contents = new ItemStack[36];
        this.armorContents = new ItemStack[4];
    }

    public Ability getAbility() {
        return this.ability;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }

    public Kit setContents(int index, ItemStack is) {
        this.contents[index] = is;
        return this;
    }

    public Kit setArmorContents(int index, ItemStack is) {
        this.armorContents[index] = is;
        return this;
    }

    public Kit updateAbility(Ability ability) {
        this.ability = ability;
        return this;
    }

    public Kit updateContents(ItemStack[] contents) {
        this.contents = contents;
        return this;
    }

    public Kit updateArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
        return this;
    }
    
    public Kit giveKit(Player player){
        player.getInventory().setArmorContents(armorContents);
        for(ItemStack is : contents){
            int empty = player.getInventory().firstEmpty();
            if(empty != -1){
                player.getInventory().setItem(empty, is);
            }
        }
        return this;
    }

    @Override
    protected Kit clone() {
        Kit kit = new Kit(getName(), icon);
        kit.updatePrice(getPrice());
        kit.updateContents(contents)
                .updateArmorContents(armorContents)
                .updateAbility(ability);
        return kit;
    }

}
