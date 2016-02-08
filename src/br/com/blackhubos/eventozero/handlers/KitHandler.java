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
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.ability.Ability;
import br.com.blackhubos.eventozero.factory.ItemFactory;
import br.com.blackhubos.eventozero.kit.Kit;
import br.com.blackhubos.eventozero.util.Framework;

public final class KitHandler {
    
    private final List<Kit> kits;
    
    public KitHandler() {
        this.kits = new Vector<>();
    }
    
    public Optional<Kit> getKitByName(final String name) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(name), 
				"name cannot be null or empty");
		
		return kits.parallelStream()
				.filter(a -> a.getName().equals(name))
				.findAny();
    }
    
    public List<Kit> getKits() {
        return this.kits;
    }
    
    public void loadKits(final Plugin plugin) {
    	final String SEPARATOR = File.separator;
    	
    	final File kitFolder = new File(plugin.getDataFolder() + SEPARATOR + "kit");
        final File file = new File(kitFolder, "kits.yml");
        
		if (!file.exists()) 
		{
			try 
			{
				kitFolder.mkdir();
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return;
		}
        
        final Framework.Configuration configuration = new Framework.Configuration(file);
        
        if (!configuration.contains("kits")) return;
        
        for (String key : configuration.getConfigurationSection("kits").getKeys(false)) {
            final Kit kit = new Kit(key, new ItemFactory(configuration.getString("kits." + key + ".icon"), null).getPreparedItem());
            final Optional<Ability> optAbility = AbilityHandler.getAbilityByName(configuration.getString("kits." + key + ".ability"));
            
            optAbility.ifPresent( ability -> {
            	kit.updateAbility(ability).
	                setArmorContents(3, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.helmet"), null).getPreparedItem()).
	                setArmorContents(2, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.armor"), null).getPreparedItem()).
	                setArmorContents(1, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.leggings"), null).getPreparedItem()).
	                setArmorContents(0, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.boots"), null).getPreparedItem());
            });
            
            int count = 0;
            for (String otherKey : configuration.getStringList("kits." + key + ".inventory.contents")) {
                kit.setContents(count, new ItemFactory(otherKey, null).getPreparedItem());
                count++;
            }
            kits.add(kit);
        }
        
        EventoZero.consoleMessage("Formam carregado(s) " + kits.size() + " kit(s)");
    }
    
}
