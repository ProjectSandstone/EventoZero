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

import java.sql.ResultSet;
import java.sql.SQLException;
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
import br.com.blackhubos.eventozero.handlers.MessageHandler;
import br.com.blackhubos.eventozero.kit.Kit;
import br.com.blackhubos.eventozero.party.Party;
import br.com.blackhubos.eventozero.storage.Storage;
import br.com.blackhubos.eventozero.util.Framework;
import java.util.Random;

/**
 * TODO: arrumar index do setSign (todos 0) TODO: adicionar logs
 * (getLoggerService()) nos backups e afins para deixar registrado TODO:
 * adicionar as mensagens e seus replaces aos respectivos voids necessários
 * TODO: documentar (javadoc) todos os métodos e construtores em Português BR.
 * Falta algo? documente aqui com um TODO: mensagem TODO = To Do (a fazer) TODO:
 * no modo espectador, desativar comandos exceto do eventozero
 *
 */
public class Event {

    private final String eventName;
    private final EventData eventData;

    private final Vector<Player> joineds;
    private final Vector<Player> spectators;
    private final Vector<Party> partys;
    private final Vector<Ability> abilitys;

    private String eventDisplayname;
    private String eventDescription;
    private EventState eventoState;

    public Event(final String name) {
        this.eventName = name;
        this.joineds = new Vector<>();
        this.spectators = new Vector<>();
        this.partys = new Vector<>();
        this.abilitys = new Vector<>();
        this.eventData = new EventData();
    }

    /**
     *
     * @return Retorna o nome do evento de forma como configurado (em casos
     * importantes use lower-case!)
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     *
     * @return Retorna a descrição do evento.
     */
    public String getEventDescription() {
        return this.eventDescription;
    }

    /**
     *
     * @return Retorna o nome customizado do evento.
     */
    public String getEventDisplayName() {
        return this.eventDisplayname;
    }

    /**
     *
     * @return Retorna o {@link EventData} do evento, que contém várias
     * informações.
     */
    public EventData getEventData() {
        return this.eventData;
    }

    /**
     * Os eventos tem vários estados, com este método, é possível identificar o
     * estado atual do evento. Leia mais na classe EventState.
     *
     * @return Retorna o estado do evento.
     */
    public EventState getEventState() {
        return this.eventoState;
    }

    /**
     * Atualiza a descrição do evento
     *
     * @param desc Nova descrição
     * @return Retorna a instância do {@link Event} modificada.
     */
    public Event updateDescription(final String desc) {
        this.eventDescription = desc;
        return this;
    }

    /**
     *
     * @param displayname
     * @return Retorna a instância do {@link Event} modificada.
     */
    public Event updateDisplayName(final String displayname) {
        this.eventDisplayname = displayname;
        return this;
    }

    /**
     *
     * @return Retorna a lista de todas as partys em ação do evento.
     */
    public Vector<Party> getPartys() {
        return this.partys;
    }

    /**
     *
     * @return Retorna a lista de todos os participantes do evento.
     */
    public Vector<Player> getPlayers() {
        return this.joineds;
    }

    /**
     *
     * @return Retorna a lista de todos os es espectadores ativos no evento.
     */
    public Vector<Player> getSpectators() {
        return this.spectators;
    }

    /**
     *
     * @return Retorna as habilidades (Abilitys) do evento.
     */
    public Vector<Ability> getAbilitys() {
        return this.abilitys;
    }

    public Event playerJoin(final Player player) {
        if ((player == null) || ((player != null) && !player.isOnline())) {
            throw new NullArgumentException("Player is null");
        }
        if (!this.hasPlayerJoined(player)) {
            this.joineds.add(player);
            this.playerBackup(player);
            this.updateSigns();
        }
        return this;
    }

    public Event playerQuit(final Player player) {
        if ((player == null) || ((player != null) && !player.isOnline())) {
            throw new NullArgumentException("Player is null");
        }
        if (this.hasPlayerJoined(player)) {
            this.joineds.remove(player);
            this.spectatorQuit(player);
            this.playerRestore(player);
            this.updateSigns();
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
        if ((player == null) || ((player != null) && !player.isOnline())) {
            throw new NullArgumentException("Player is null");
        }
        if (!this.spectators.contains(player)) {
            for (final Player obj : this.getPlayers()) {
                obj.hidePlayer(player);
            }
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
        if ((player == null) || ((player != null) && player.isOnline())) {
            throw new NullArgumentException("Player is null");
        }
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

    public boolean hasPlayerJoined(final Player player) {
        return this.joineds.contains(player);
    }

    public void stop() {
        // TODO: STOP EVENT, GET WINNERS, ALIVES
        this.updateSigns();
    }

    public void start() {
        // TODO: START THE COUNTDOWN
        if (this.getEventState() == EventState.OPENED) {
            new EventCountdown(this, (Integer) this.getEventData().getData("options.countdown.seconds"));
            this.updateSigns();
        }
    }

    public void forceStop() {
        // TODO: STOP FORCE EVENT
        for (final Player player : this.joineds) {
            this.playerQuit(player);
        }
        this.updateSigns();
    }

    public void forceStart() {
        // TODO: START EVENT
        if (this.getPlayers().size() < (Integer) this.getEventData().getData("event.min")) {
            // TODO: STOP
            // TODO: MESSAGE CANCELED MIN PLAYER
            this.forceStop();
        }
        for (Player player : getPlayers()) {
            Kit kit = getEventData().getData(player.getName() + ".kit");
            if (kit != null) {
                kit.giveKit(player);
                if(kit.getAbility() != null){
                    getEventData().updateData(player.getName() + ".ability", kit.getAbility());
                }
            }
            Random r = new Random();
            Vector<Location> spawns = getEventData().getData("teleport.spawn");
            player.teleport(spawns.get(r.nextInt(spawns.size())));
        }
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
        /*
		 * this.getEventData().updateData(player.getName() + ".health", player.getHealth());
		 * this.getEventData().updateData(player.getName() + ".food", player.getFoodLevel());
		 * this.getEventData().updateData(player.getName() + ".exp", player.getExp());
		 * this.getEventData().updateData(player.getName() + ".expLevel", player.getLevel());
		 * this.getEventData().updateData(player.getName() + ".location", player.getLocation());
		 * this.getEventData().updateData(player.getName() + ".inventory.contents", player.getInventory().getContents());
		 * this.getEventData().updateData(player.getName() + ".inventory.armorContents", player.getInventory().getArmorContents());
         */
        this.getEventData().updateData(player.getName() + ".ability", getEventData().getData("options.ability.fixed_ability"));
        EventoZero.getStorage().backupPlayer(player, this.eventName.toLowerCase());
    }

    public void playerRestore(final Player player) {
        final ResultSet rs = EventoZero.getStorage().search("SELECT * FROM `" + Storage.Module.BACKUP.getTable() + "` WHERE `jogador`='" + player.getName().toLowerCase() + "' AND `devolvido`='0' AND `evento`='" + this.eventName.toLowerCase() + "';");
        try {
            if (rs.next()) {
                player.setHealth(rs.getInt("vida"));
                player.setFoodLevel(rs.getInt("comida"));
                player.setExp(rs.getFloat("xp"));
                player.setLevel(rs.getInt("level"));
                player.teleport(Framework.toLocation(rs.getString("localizacao")));
                // TODO: pensar como vai ser o sistema que salva e carrega itens via mysql..
                // TODO: itemstack.serializer ou criar método que suporta livros
                player.getInventory().setContents((ItemStack[]) this.getEventData().getData(player.getName() + ".inventory.contents"));
                player.getInventory().setArmorContents((ItemStack[]) this.getEventData().getData(player.getName() + ".inventory.armorContents"));
            }
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void updateSigns() {
        if (this.getEventData().containsKey("options.signs.locations") && (this.getEventData().getData("options.signs.locations") != null)) {
            final Vector<Location> locations = (Vector<Location>) this.getEventData().getData("options.signs.locations");
            for (final Location location : locations) {
                final Block block = location.getWorld().getBlockAt(location);
                if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
                    final String string = String.valueOf(this.getEventData().getData("options.message." + this.getEventState().getPath()));
                    final Sign sign = (Sign) block.getState();
                    sign.setLine(0, String.valueOf(this.getEventData().getData("options.signs.line.1")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(getEventData().getData("options.player_max"))).replace("{name}", this.getEventName()).replaceAll("&", "§"));
                    sign.setLine(1, String.valueOf(this.getEventData().getData("options.signs.line.2")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(getEventData().getData("options.player_max"))).replace("{name}", this.getEventName()).replaceAll("&", "§"));
                    sign.setLine(2, String.valueOf(this.getEventData().getData("options.signs.line.3")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(getEventData().getData("options.player_max"))).replace("{name}", this.getEventName()).replaceAll("&", "§"));
                    sign.setLine(3, String.valueOf(this.getEventData().getData("options.signs.line.4")).replace("{state]", string).replace("{playersize}", String.valueOf(this.getPlayers().size())).replace("{playermax}", String.valueOf(getEventData().getData("options.player_max"))).replace("{name}", this.getEventName()).replaceAll("&", "§"));
                    sign.update();
                }
            }
        }
    }

}
