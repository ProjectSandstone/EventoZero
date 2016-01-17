package br.com.blackhubos.eventozero.shop;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.factory.Event;

public class Shop implements Listener {

    private final String name;
    private final ItemStack icon;
    private final List<ShopItem> items;

    private String title;
    private int size;

    public Shop(String name, ItemStack icon) {
        this.name = name;
        this.title = name;
        this.icon = icon;
        this.size = 9;
        this.items = new LinkedList<>();
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Shop addItem(ShopItem item) {
        items.add(item);
        return this;
    }

    public Shop updateSize(int size) {
        this.size = size;
        return this;
    }

    public Shop updateTitle(String title) {
        this.title = title;
        return this;
    }

    public Shop openShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        items.stream().filter(item -> inventory.firstEmpty() != -1).forEach(item -> {
            inventory.setItem(inventory.firstEmpty(), item.getIcon());
        });

        return this;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShop(InventoryClickEvent e) {
        // FALTA TERMINAR
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        if (inventory.getTitle().equals(title) && (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR)) {
            e.setCancelled(true);
            for (ShopItem item : items) {
                if (item.equals(e.getCurrentItem())) {
                    Optional<Event> event = EventoZero.getEventHandler().getEventByPlayer(player);
                    if (!event.isPresent()) {
                        return;
                    }
                    item.onBuyed(event.get(), player);
                }
            }
        }
    }

}
