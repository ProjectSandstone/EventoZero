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
package br.com.blackhubos.eventozero.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.bukkit.plugin.Plugin;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.factory.ItemFactory;
import br.com.blackhubos.eventozero.shop.Shop;
import br.com.blackhubos.eventozero.shop.ShopItem;
import br.com.blackhubos.eventozero.util.Framework.Configuration;

public class ShopHandler {

    private final Vector<Shop> shops;

    public ShopHandler() {
        this.shops = new Vector<>();
    }

    public void loadShops(final Plugin plugin) {
    	final String SEPARATOR = File.separator;
    	
    	final File shopFolder = new File(plugin.getDataFolder() + SEPARATOR + "shop");
        final File file = new File(shopFolder, "shops.yml");
        
		if (!file.exists()) 
		{
			try 
			{
				shopFolder.mkdir();
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return;
		}
        
        final Configuration configuration = new Configuration(file);

        if (!configuration.contains("shops")) return;
        
        for (final String key : configuration.getConfigurationSection("shops").getKeys(false)) {
            final Shop shop = new Shop(configuration.getString("shops." + key + ".name"), new ItemFactory(configuration.getString("shops." + key + ".icon"), null).getPreparedItem());
            for (final String s : configuration.getStringList("shops." + key + ".items")) {
                if (EventoZero.getKitHandler().getKitByName(s.replace("{|}", "")).isPresent()) {
                    shop.addItem(EventoZero.getKitHandler().getKitByName(s.replace("{|}", "")).get());
                } else {
                    shop.addItem(new ShopItem(s.replace("{|}", ""), new ItemFactory(configuration.getString("shops." + key + ".items_shop." + s.replace("{|}", "") + ".icon"), null).getPreparedItem()).updatePrice(configuration.getInt("shops." + key + ".items_shop." + s.replace("{|}", "") + ".price")));
                }
            }
        }
        EventoZero.consoleMessage("Foram carregado(s) " + shops.size() + " shop(s).");
    }

}
