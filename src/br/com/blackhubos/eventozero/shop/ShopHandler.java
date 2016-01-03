package br.com.blackhubos.eventozero.shop;

import java.io.File;
import java.util.Vector;

import org.bukkit.plugin.Plugin;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.util.Framework.Configuration;
import br.com.blackhubos.eventozero.util.Framework.ItemFactory;

public class ShopHandler {
	
	private final Vector<Shop> shops;
	
	public ShopHandler(){
		this.shops = new Vector<>();
	}
	
	public void loadShops(Plugin plugin){
		final File file = new File(plugin.getDataFolder() + File.separator + "shop" + File.separator + "shops.yml");
		final Configuration configuration = new Configuration(file);
		for(String key : configuration.getConfigurationSection("shops").getKeys(false)){
			Shop shop = new Shop(configuration.getString("shops." + key + ".name"), new ItemFactory(configuration.getString("shops." + key + ".icon")).getItem());
			for(String s : configuration.getStringList("shops." + key + ".items")){
				if(EventoZero.getKitHandler().getKitByName(s.replace("{|}", "")) != null){
					shop.addItem(EventoZero.getKitHandler().getKitByName(s.replace("{|}", "")));
				} else {
					
				}
			}
		}
	}

}
