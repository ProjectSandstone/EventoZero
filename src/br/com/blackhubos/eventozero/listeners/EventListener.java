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
package br.com.blackhubos.eventozero.listeners;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.factory.Event;
import br.com.blackhubos.eventozero.factory.EventFlags;
import br.com.blackhubos.eventozero.factory.EventState;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public final class EventListener implements Listener {

    /**
     * Evento chamado quando um jogador desloga ou sua conexão cai
     *
     * TODO: se o jogador estava em evento, teleportá-lo, verificar se o evento
     * tem punição por dc, e aplicar as punições. TODO: se o jogador estava em
     * evento, verificar se quando ele saiu, já há vencedores TODO: atualizar
     * clãs no evento, se o hook estiver ativado
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void whenPlayerLeft(final PlayerQuitEvent event) {
        final Player sender = event.getPlayer();
        quitPlayer(sender);
    }

    /**
     * Evento chamado quando um jogador entra no servidor
     *
     * TODO: verificar se tem eventos abertos, e avisar ele se tiver.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenPlayerJoin(final PlayerJoinEvent event) {
        final Player sender = event.getPlayer();
        final br.com.blackhubos.eventozero.factory.EventHandler eventHandler = EventoZero.getEventHandler();
        for(Event e : eventHandler.getEventsByState(EventState.OPENED)) {
            // COLOCAR NO MESSAGEHANDLER A MENSAGEM DE AVISAR O EVENTO
        }
    }
    
    /**
     * Evento chamado quando alguem interage com uma placa do evento.
     *
     * TODO: Envia description do evento ao clicar na placa com botão esquerdo
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenInteract(final PlayerInteractEvent event) {
        final Player sender = event.getPlayer();
        final Action action = event.getAction();
        final Block block = event.getClickedBlock();
        if(action.equals(Action.LEFT_CLICK_BLOCK) && (block.getType().equals(Material.SIGN) || block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN))) {
            final Sign sign = (Sign) block.getState();
            final br.com.blackhubos.eventozero.factory.EventHandler eventHandler = EventoZero.getEventHandler();
            
            Optional<Event> optional = eventHandler.getEventByName(sign.getLine(0));
            if(optional.isPresent()) {
                Event e = optional.get();
                sender.sendMessage(e.getDescription());
            }
        }
    }

    /**
     * Evento chamado quando um bloco é quebrado
     *
     * TODO: verificar se o event.getPlayer() é != null TODO: verificar se o
     * jogador está no evento, e se tiver, se o evento permrite quebrar blocos
     * TODO: se o evento for de quebrar blocos, adicionar o bloco quebrado ao
     * score do jogador
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenBlockBreaked(final BlockBreakEvent event) {
        final Player sender = event.getPlayer();
        final br.com.blackhubos.eventozero.factory.EventHandler eventHandler = EventoZero.getEventHandler();
        final Optional<Event> optional = eventHandler.getEventByPlayer(sender);
        if(optional.isPresent()) {
            Event eventz = optional.get();
            if(eventz.getFlags().hasFlag(EventFlags.Flag.DISABLE_BLOCK_BREAK)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Evento chamado quando um bloco é colocado
     *
     * TODO: verificar se o event.getPlayer() é != null TODO: verificar se o
     * jogador está no evento, e se tiver, se o evento permrite colocar blocos
     * TODO: se o evento for de colocar blocos, adicionar o bloco colocado ao
     * score do jogador
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenBlockPlaced(final BlockPlaceEvent event) {
        final Player sender = event.getPlayer();
        final br.com.blackhubos.eventozero.factory.EventHandler eventHandler = EventoZero.getEventHandler();
        final Optional<Event> optional = eventHandler.getEventByPlayer(sender);
        if(optional.isPresent()) {
            Event eventz = optional.get();
            if(eventz.getFlags().hasFlag(EventFlags.Flag.DISABLE_BLOCK_PLACE)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Evento chamado quando uma placa é colocada OU modificada
     *
     * TODO: verificar se se trata de uma placa de evento TODO: verificar se o
     * jogador tem permissão para criar placa de evento TODO: verificar se o
     * evento existe
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenSignPlaced(final SignChangeEvent event) {
        final Player sender = event.getPlayer();
        final br.com.blackhubos.eventozero.factory.EventHandler eventHandler = EventoZero.getEventHandler();
        if(sender.isOp()) {
            Optional<Event> optional = eventHandler.getEventByName(event.getLine(0));
            if(optional.isPresent()) {
                Event eventz = optional.get();
                eventz.getSignsLocation().add(event.getBlock().getLocation());
                Bukkit.getScheduler().runTaskLater(EventoZero.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        eventz.updateSigns();
                    }
                }, 20);
            }
        }
    }

    /**
     * Evento chamado quando uma entidade dá dano em outra (NOTA: ENTIDADE, NÃO
     * EXCLUSIVAMENTE PLAYERS)
     *
     * TODO: verificar se são jogadores que estão dando danos TODO: verificar se
     * eles estão participando TODO: verificar se não é um jogador em modo
     * spectate dando dano em um jogador participante ou vice-versa TODO:
     * verificar se o evento permite danos TODO: verificar o motivo do dano e
     * ver se o evento suporta tal motivo ou se é bloqueado
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void whenDamaged(final EntityDamageByEntityEvent event) {
        final Entity damaged = event.getEntity();
        final Entity damager = event.getDamager();
        if ((event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) && (damager instanceof Player)) {
            Optional<Event> optional = EventoZero.getEventHandler().getEventByPlayer((Player) damaged);
            if (optional.isPresent() && optional.get().hasPlayerJoined((Player) damager)) {
                Event eventz = optional.get();
                if(eventz.getFlags().hasFlag(EventFlags.Flag.DISABLE_PVP)) {
                    event.setCancelled(true);
                }
            }
        }
        if(!(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) && !(damager instanceof Player)) {
            Optional<Event> optional = EventoZero.getEventHandler().getEventByPlayer((Player) damaged);
            if (optional.isPresent()) {
                Event eventz = optional.get();
                if(eventz.getFlags().hasFlag(EventFlags.Flag.DISABLE_DAMAGE)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Evento chamado quando um jogador usa um comando (ex: "/spawn")
     *
     * TODO: verificar se o jogador está participando do evento TODO: verificar
     * se o comando está permitido ou bloqueado no evento
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void whenCommand(final PlayerCommandPreprocessEvent event) {
        final Player sender = event.getPlayer();
    }

    /**
     * Evento chamado quando um jogador fala no chat
     *
     * TODO: verificar se o local que o jogador está, é um evento, e se permite
     * falar no chat ou receber mensagens TODO: verificar se o evento permite
     * falar no chat ou receber mensagens
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void whenChat(final AsyncPlayerChatEvent event) {
        final Player sender = event.getPlayer();
    }

    /**
     * Evento chamado quando um jogador é kickado do servidor
     *
     * TODO: tratar igual ao PlayerQuitEvent (exceto pela cláusula que diz
     * respeito de punições!)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenKicked(final PlayerKickEvent event) {
        final Player sender = event.getPlayer();
        quitPlayer(sender);
    }
    
    private void quitPlayer(Player sender) {
        final br.com.blackhubos.eventozero.factory.EventHandler eventHandler = EventoZero.getEventHandler();
        final Optional<Event> optional = eventHandler.getEventByPlayer(sender);
        if (optional.isPresent()) {
            final Event eventz = optional.get();
            if (eventz.getPlayersRemaining().size() > 2) {
                eventz.playerQuit(sender);
                for (Player player : eventz.getPlayersRemaining()) {

                    // CRAZY: PREMIAR, porém saber qual será o premio, por causa das colocações.. ESTOU CONFUSO
                    eventz.playerQuit(player);
                }
            } else {
                eventz.playerQuit(sender);
            }
        }
    }

}
