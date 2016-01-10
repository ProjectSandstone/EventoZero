/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright � 2016 BlackHub OS and contributors.
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
package br.com.blackhubos.eventozero.factory;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.ability.Ability;
import br.com.blackhubos.eventozero.handlers.AbilityHandler;
import br.com.blackhubos.eventozero.util.Framework;
import java.io.File;
import java.util.Vector;

import org.bukkit.plugin.Plugin;

import br.com.blackhubos.eventozero.util.Framework.Configuration;
import java.util.List;
import org.bukkit.Location;

public final class EventFactory {

    public static Event createMyEvent(final String name) {
        return new Event(name).updateDescription("");
    }

    public static void loadEvents(final Plugin plugin) {
        final File folder = new File(plugin.getDataFolder() + File.separator + "eventos" + File.separator);

        File[] fileArray = folder.listFiles();
        // TODO fazer algo menos artifical caso listFiles retorne nulo, esta é a solução por enquanto!
        if(fileArray == null) {
            EventoZero.consoleMessage("Não foi encontrado nenhum evento para carregar!");
            return;
        }

        for (final File file : fileArray) {
            if (file.getName().endsWith(".yml")) {
                final Configuration configuration = new Configuration(file); // Já carrega automaticamente
                final Event event = new Event(configuration.getString("name"))
                        .updateDescription(configuration.getString("description"))
                        .updateDisplayName(configuration.getString("display_name"));

                event.getEventData()
                        .updateData("options.signs.line.1", configuration.getString("signs.lines.1"))
                        .updateData("options.signs.line.2", configuration.getString("signs.lines.2"))
                        .updateData("options.signs.line.3", configuration.getString("signs.lines.3"))
                        .updateData("options.signs.line.4", configuration.getString("signs.lines.4"))

                        .updateData("options.message.opened", configuration.getString("options.message.opened"))
                        .updateData("options.message.prestarted", configuration.getString("options.message.prestarted"))
                        .updateData("options.message.occurring", configuration.getString("options.message.occurring"))
                        .updateData("options.message.ending", configuration.getString("options.message.ending"))
                        .updateData("options.message.closed", configuration.getString("options.message.closed"))

                        .updateData("options.player_max", configuration.getInt("options.player_max"))
                        .updateData("options.player_min", configuration.getInt("options.player_min"))
                        .updateData("options.party_size", configuration.getInt("options.party_size"))
                        
                        .updateData("options.enables.party", configuration.getBoolean("options.enables.party"))
                        .updateData("options.enables.safe_inventory", configuration.getBoolean("options.enables.safe_inventory"))
                        
                        .updateData("options.seconds_to_stop", configuration.getInt("options.seconds_to_stop"))
                        .updateData("options.ability.fixed_ability", AbilityHandler.getAbilityByName(configuration.getString("options.ability.fixed_ability")))
                        .updateData("options.ability.abilitys", parseAbilitys(configuration.getStringList("options.ability.abilitys")))
                        .updateData("options.shop.shops", configuration.getStringList("options..shop.shops"))
                        
                        .updateData("teleport.lobby", parseList(configuration.getStringList("teleport.lobby")))
                        .updateData("teleport.spawn", parseList(configuration.getStringList("teleport.spawn")));

                EventoZero.getEventHandler().loadEvent(event);
            }
        }
        EventoZero.consoleMessage("Foram carregado(s) " + EventoZero.getEventHandler().getEvents().size() + " evento(s).");
    }
    
    private static Vector<Ability> parseAbilitys(final List<String> list){
        final Vector<Ability> vector = new Vector<>();
        for(String loop: list){
            if(AbilityHandler.hasAbilityByName(loop)){
                vector.add(AbilityHandler.getAbilityByName(loop).get());
            }
        }
        return vector;
    }
    
    private static Vector<Location> parseList(final List<String> list){
        final Vector<Location> vector = new Vector<>();
        for(String loop : list){
            if(loop != null && !loop.isEmpty())
                vector.add(Framework.toLocation(loop));
        }
        return vector;
    }
    

}
