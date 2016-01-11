package br.com.blackhubos.eventozero.shop;

import br.com.blackhubos.eventozero.EventoZero;
import org.bukkit.inventory.ItemStack;

import br.com.blackhubos.eventozero.factory.Event;
import org.bukkit.entity.Player;

public class ShopItem {

    private final String name;
    private final ItemStack itemIcon;

    private int pricePoints;

    public ShopItem(String name, ItemStack icon) {
        this.name = name;
        this.itemIcon = icon;
        this.pricePoints = 0;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getIcon() {
        return this.itemIcon;
    }

    public int getPrice() {
        return this.pricePoints;
    }

    public boolean equals(ItemStack stack) {
        return (itemIcon.equals(stack));
    }

    public ShopItem updatePrice(final int v) {
        this.pricePoints = v;
        return this;
    }

    public ShopItem onBuyed(Event event, Player player) {
        EventoZero.getStorage().withdrawPlayerRankingPoints(player.getName(), event.getEventName(), "vitorias", 1);
        return this;
    }

}
