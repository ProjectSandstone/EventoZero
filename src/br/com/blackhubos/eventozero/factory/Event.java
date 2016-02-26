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
package br.com.blackhubos.eventozero.factory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.ability.Ability;
import br.com.blackhubos.eventozero.kit.Kit;
import br.com.blackhubos.eventozero.party.Party;
import br.com.blackhubos.eventozero.rewards.ChestReward;
import br.com.blackhubos.eventozero.storage.Storage;
import br.com.blackhubos.eventozero.util.Framework;
import br.com.blackhubos.eventozero.util.Framework.*;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: arrumar index do setSign (todos 0) TODO: adicionar logs
 * (getLoggerService()) nos backups e afins para deixar registrado TODO:
 * adicionar as mensagens e seus replaces aos respectivos voids necessários
 * TODO: documentar (javadoc) todos os métodos e construtores em Português BR.
 *
 * Falta algo? documente aqui com um TODO: mensagem TODO = To Do (a fazer) TODO:
 * no modo espectador, desativar comandos exceto do eventozero
 */
public class Event {

    private final String name;
    private final EventData data;

    private final List<Player> joineds;
    private final List<Player> spectators;
    private final List<Party> partys;
    private final List<Ability> abilitys;
    private final List<Cuboid> cuboids;
    private final List<ChestReward> chestRewards;
    private final List<Location> locations;
    private final Map<String, Location> camarotes;

    private String displayName;
    private String description;
    private EventState state;
    private EventFlags flags;
    private EventAnnouncement announcement;
    private Configuration config;

    public Event(final String name) {
    	this.flags = new EventFlags();
        this.name = name;
        this.joineds = new Vector<>();
        this.spectators = new Vector<>();
        this.partys = new Vector<>();
        this.abilitys = new Vector<>();
        this.data = new EventData();
        this.cuboids = new Vector<>();
        this.chestRewards = new Vector<>();
        this.locations = new Vector<>();
        this.camarotes = new HashMap<>();
        this.announcement = new EventAnnouncement(this, 10, 10);
    }

    public Event(final String name, final Configuration config) {
        this(name);
        this.config = config;
    }

    /**
     *
     * @return Retorna o nome do evento de forma como configurado (em casos
     * importantes use lower-case!)
     */
    public String getName() {
        return this.name;
    }

    public EventFlags getFlags() {
	return this.flags;
    }
    
    public List<ChestReward> getChestRewards() {
        return this.chestRewards;
    }
    
    /**
     *
     * @return Retorna a lista de cuboids definidos neste evento.
     */
    public List<Cuboid> getCuboids() {
        return this.cuboids;
    }

    /**
     *
     * @return Retorna a descrição do evento.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     *
     * @return Retorna o nome customizado do evento.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     *
     * @return Retorna o {@link EventData} do evento, que contém várias
     * informações.
     */
    public EventData getData() {
        return this.data;
    }

    /**
     * Os eventos tem vários estados, com este método, é possível identificar o
     * estado atual do evento. Leia mais na classe EventState.
     *
     * @return Retorna o estado do evento.
     */
    public EventState getState() {
        return this.state;
    }
    
    public EventAnnouncement getAnnouncement() {
        return this.announcement;
    }

    /**
     *
     * @return Retorna {@link Ability} fixada no evento, no caso todos
     * participantes teriam esta habilidade
     */
    public Ability getFixedAbility() {
        return getData().getData("options.ability.fixed_ability");
    }

    /**
     * Atualiza a descrição do evento
     *
     * @param desc Nova descrição
     * @return Retorna a instância do {@link Event} modificada.
     */
    public Event updateDescription(final String desc) {
        this.description = desc;
        return this;
    }

    /**
     *
     * @param displayname
     * @return Retorna a instância do {@link Event} modificada.
     */
    public Event updateDisplayName(final String displayname) {
        this.displayName = displayname;
        return this;
    }

    public Configuration getConfig() {
        return this.config;
    }
    
    /**
     *
     * @return Retorna a lista de todas as partys em ação do evento.
     */
    public List<Party> getPartys() {
        return this.partys;
    }

    /**
     *
     * @return Retorna a lista de todos os participantes do evento.
     */
    public List<Player> getPlayers() {
        return this.joineds;
    }

    /**
     *
     * @return Retorna a lista de todos os jogadores restantes no evento.
     */
    public Vector<Player> getPlayersRemaining() {
        return getPlayers()
                .stream()
                .filter(spec -> !getSpectators().contains(spec))
                .collect(Vector::new, Vector::add, Vector::addAll);
    }

    /**
     *
     * @return Retorna a lista de todos os es espectadores ativos no evento.
     */
    public List<Player> getSpectators() {
        return this.spectators;
    }

    /**
     *
     * @return Retorna as habilidades (Abilitys) do evento.
     */
    public List<Ability> getAbilitys() {
        return this.abilitys;
    }

    /**
     *
     * @return Retorna lista de entradas do camarote
     */
    public List<Location> getEntradas() {
        return locations;
    }

    /**
     *
     * @param index
     * @return Retorna entrada
     */
    public Location getEntrada(int index) {
        return this.locations.get(index);
    }

    /**
     *
     * @param name
     * @return Location do camarote definido pelo parametro name
     */
    public Location getCamaroteByName(String name) {
        return this.camarotes.get(name);
    }

    /**
     *
     * @return Retorna todos camarote
     */
    public Map<String, Location> getCamarotes() {
        return this.camarotes;
    }

    /**
     *
     * @param player
     * @return
     */
    public Event playerJoin(final Player player) {
        if ((player == null) || !player.isOnline())
            throw new NullArgumentException("Player is null");
        if (!this.hasPlayerJoined(player)) {
            this.joineds.add(player);
            this.updateSigns();
            
            if (safeInventory()) {
                this.playerBackup(player);
                player.getInventory().clear();
                player.getInventory().setArmorContents(new ItemStack[4]);
            }
            
            //TODO: getRandomTeleport pode retornar nulo, temos que tratar caso aconteça
            player.teleport(getRandomTeleport("lobby"));
        }
        return this;
    }

    /**
     *
     * @param player
     * @return
     */
    public Event playerQuit(final Player player) {
        if ((player == null) || !player.isOnline())
            throw new NullArgumentException("Player is null");
        if (this.hasPlayerJoined(player)) {
            this.joineds.remove(player);
            this.spectatorQuit(player);
            this.updateSigns();
            if (safeInventory())
                this.playerRestore(player);
        }
        return this;
    }

    /**
     * Define um jogador como espectador. TODO: (o jogador deveria ser
     * teleportado para o lugar do evento por aqui ou pelo comando?)
     *
     * @param player Jogador em questão a virar espectador
     * @return Retorna a instância do {@link Event} modificada.
     */
    public Event spectatorJoin(final Player player) {
        if ((player == null) || !player.isOnline())
            throw new NullArgumentException("Player is null");
        if (!this.spectators.contains(player)) {
        	getPlayers().forEach( p -> p.hidePlayer(player) );
            player.setAllowFlight(true);
            player.setFlying(true);
            this.spectators.add(player);
        }
        return this;
    }

    /**
     * Remove um jogador do modo espectador.
     *
     * @param player Jogador que será removido do modo espectador.
     * @return Retorna a instância do {@link Event} modificada.
     */
    public Event spectatorQuit(final Player player) {
        if ((player == null) || player.isOnline())
            throw new NullArgumentException("Player is null");
        if (this.spectators.contains(player)) {
            for (final Player obj : this.getPlayers()) {
                obj.showPlayer(player);
            }
            player.setAllowFlight(false);
            player.setFlying(false);
            this.spectators.remove(player);
        }
        return this;
    }

    /**
     *
     * @param player
     * @return {@code true} se o jagodor já entrou no evento.
     */
    public boolean hasPlayerJoined(final Player player) {
        return this.joineds.contains(player);
    }

    /**
     * 
     * @return True se protege o inventário
     */
    public boolean safeInventory() {
        return this.getData().getData("options.enables.safe_inventory");
    }
    
    /**
     * 
     * @return {@code true} se as party estão ativado
     */
    public boolean enablePartys() {
        return this.getData().getData("options.enables.party");
    }

    /**
     *
     * @param location
     */
    public void addEntrada(Location location) {
        this.locations.add(location);
    }

    /**
     *
     * @param name
     * @param location
     */
    public void setCamarote(String name, Location location) {
        this.camarotes.put(name, location);
    }

    /**
     *
     */
    public void stop() {
        // TODO: STOP EVENT, GET WINNERS, ALIVES
        this.forceStop();
        this.updateSigns();
    }

    public void start() {
        if (this.getState() == EventState.CLOSED) {
            this.updateSigns();
            EventoZero.startAnnouncementHandler();
        }
    }

    public void forceStop() {
        // TODO: STOP FORCE EVENT
        for (final Player player : this.joineds) {
            this.playerQuit(player);
        }
        this.updateSigns();
        
        if(EventoZero.getEventHandler().getEventsSizeClosed() == 0) {
            EventoZero.closeAnnouncementHandler();
        }
    }

    public void forceStart() {
        // TODO: START EVENT
        if (this.getPlayers().size() < this.getData().<Integer>getData("event.min"))
            // TODO: STOP
            // TODO: MESSAGE CANCELED MIN PLAYER
            this.forceStop();
        
        final EventData data = this.getData();
        
        getPlayers().forEach( player -> {
        	final Kit kit = data.getData(player.getName() + ".kit");
        	
        	if (kit != null ) {
        		kit.giveTo(player);
        		if (kit.hasAbility()) {
        			data.updateData(player.getName() + ".ability", kit.getAbility());
        		}
        	}
        	//TODO: getRandomTeleport pode retornar nulo, temos que tratar caso aconteça
        	player.teleport(getRandomTeleport("spawn"));
        });
        
        // TODO: CODE START
        this.updateSigns();
    }

    /**
     * Este método irá criar um backup no banco de dados do EventoZero com dados
     * importantes sobre o jogador, tais como, vida, comida, itens, xp,
     * localização, armadura, etc. Você poderá restaurar esse backup ao jogador
     * quando quiser, pois fica salvo em backup. Note que os backups não são
     * retirados do banco de dados após restaurar, são apenas 'trancados' e não
     * podem mais ser usados.
     *
     * @param player O jogador que deverá ter um novo backup criado para o
     * evento em questão.
     */
    public void playerBackup(final Player player) {
        this.getData().updateData(player.getName() + ".inventory.contents", player.getInventory().getContents());
        this.getData().updateData(player.getName() + ".inventory.armorContents", player.getInventory().getArmorContents());
        this.getData().updateData(player.getName() + ".ability", this.getData().getData("options.ability.fixed_ability"));
        EventoZero.getStorage().backupPlayer(player, this.name.toLowerCase());
    }

    /**
     * Este método irá restaurar um backup do jogador, salvo no banco de dados
     * do EventoZero com dados importantes sobre o jogador, tais como, vida,
     * comida, itens, xp, localização, armadura, etc. Você poderá restaurar esse
     * backup ao jogador quando quiser, pois fica salvo em backup. Note que os
     * backups não são retirados do banco de dados após restaurar, são apenas
     * 'trancados' e não podem mais ser usados.
     *
     * @param player
     */
    public void playerRestore(final Player player) {
        if ((player == null) || ((player != null) && !player.isOnline()))
            throw new NullArgumentException("Player is null");
        final ResultSet rs = EventoZero.getStorage().search("SELECT * FROM `" + Storage.Module.BACKUP.getTable() + "` WHERE `jogador`='" + player.getName().toLowerCase() + "' AND `devolvido`='0' AND `evento`='" + this.name.toLowerCase() + "';");
        try {
            if (rs.next()) {
                // TODO: isso é para ter compatibilidade de 1.5.2 (health por int) e 1.7+ (health por double)
                try {
                    player.getClass().getMethod("setHealth", double.class).invoke(player, (double) rs.getInt("vida"));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
                    try {
                        player.getClass().getMethod("setHealth", int.class).invoke(player, rs.getInt("vida"));
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e2) {
                    }
                }
                player.setFoodLevel(rs.getInt("comida"));
                player.setExp(rs.getFloat("xp"));
                player.setLevel(rs.getInt("level"));
                player.teleport(Framework.toLocation(rs.getString("localizacao")));
                player.getInventory().setContents((ItemStack[]) this.getData().getData(player.getName() + ".inventory.contents"));
                player.getInventory().setArmorContents((ItemStack[]) this.getData().getData(player.getName() + ".inventory.armorContents"));
                this.getData().removeKeyStartWith(player.getName());
            }
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Atualiza todas placas
     */
    public void updateSigns() {
        if (this.getData().containsKey("options.signs.locations") && (this.getData().getData("options.signs.locations") != null)) {
            final Vector<Location> signs = this.getData().getData("options.signs.locations");
            for (final Location location : signs) {
                final Block block = location.getWorld().getBlockAt(location);
                if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
                    final String string = String.valueOf(this.getData().getData("options.message." + this.getState().getPath()));
                    final Sign sign = (Sign) block.getState();
                    sign.setLine(0, String.valueOf(this.getData().getData("options.signs.line.1")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(this.getData().getData("options.player_max"))).replace("{name}", this.getName()).replaceAll("&", "§"));
                    sign.setLine(1, String.valueOf(this.getData().getData("options.signs.line.2")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(this.getData().getData("options.player_max"))).replace("{name}", this.getName()).replaceAll("&", "§"));
                    sign.setLine(2, String.valueOf(this.getData().getData("options.signs.line.3")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(this.getData().getData("options.player_max"))).replace("{name}", this.getName()).replaceAll("&", "§"));
                    sign.setLine(3, String.valueOf(this.getData().getData("options.signs.line.4")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(this.getData().getData("options.player_max"))).replace("{name}", this.getName()).replaceAll("&", "§"));
                    sign.update();
                }
            }
        }
    }
    
	/**
	 * Pega um teleporte aleatorio de uma lista de teleportes.
	 * 
	 * @param teleport Nome do teleport, por exemplo {@code lobby} ou {@code spawn}
	 * @return Uma localização caso encontre, null caso não existe a key
	 *         'teleport.{@code teleport}' nos {@link #getData() dados} do
	 *         evento, ou o numero de Locations seja 0
	 */
    private Location getRandomTeleport(final String teleport) {
    	final Random random = new Random();
    	final EventData data = getData();
    	
    	if (!data.containsKey("teleport." + teleport)) {
    		return null;
    	}
    	
    	final List<Location> locations = getData().getData("teleport." + teleport);
    	
    	if (locations.size() == 0 ) {
    		return null;
    	}
    	
    	return locations.get(random.nextInt(locations.size() - 1));
    }
}
