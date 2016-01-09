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

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.ability.Ability;
import br.com.blackhubos.eventozero.factory.ItemFactory;
import java.util.Vector;

import org.bukkit.plugin.Plugin;

import com.google.common.base.Optional;

import br.com.blackhubos.eventozero.kit.Kit;
import br.com.blackhubos.eventozero.util.Framework;
import java.io.File;

public final class KitHandler {
    
    private final Vector<Kit> kits;
    
    public KitHandler() {
        this.kits = new Vector<>();
    }
    
    public Optional<Kit> getKitByName(final String name) {
        for (final Kit kit : this.getKits()) {
            if (kit.getName().equals(name)) {
                return Optional.of(kit);
            }
        }
        return Optional.absent();
    }
    
    public Vector<Kit> getKits() {
        return this.kits;
    }
    
    public void loadKits(final Plugin plugin) {
        final File file = new File(plugin.getDataFolder() + File.separator + "kit" + File.separator + "kits.yml");
        final Framework.Configuration configuration = new Framework.Configuration(file);
        for (String key : configuration.getConfigurationSection("kits").getKeys(false)) {
            Kit kit = new Kit(key, new ItemFactory(configuration.getString("kits." + key + ".icon"), null).getPreparedItem());
            
            Optional<Ability> ability = AbilityHandler.getAbilityByName(configuration.getString("kits." + key + ".ability"));
            
            if (ability.isPresent())
            {
                kit.updateAbility(ability.get()).
                setArmorContents(3, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.helmet"), null).getPreparedItem()).
                setArmorContents(2, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.armor"), null).getPreparedItem()).
                setArmorContents(1, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.leggings"), null).getPreparedItem()).
                setArmorContents(0, new ItemFactory(configuration.getString("kits." + key + ".inventory.armor_contents.boots"), null).getPreparedItem());
            }
            
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
