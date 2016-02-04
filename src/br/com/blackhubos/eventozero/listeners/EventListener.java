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
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    }

    /**
     * Evento chamado quando um jogador entra no servidor
     *
     * TODO: verificar se tem eventos abertos, e avisar ele se tiver.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void whenPlayerJoin(final PlayerJoinEvent event) {

        final Player sender = event.getPlayer();
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
        final Optional<Event> event1;
        final Entity damaged = event.getEntity();
        final Entity damager = event.getDamager();
        if (damager instanceof Player) {
            event1 = EventoZero.getEventHandler().getEventByPlayer((Player) damager);
            if (event1.isPresent() && event1.get().hasPlayerJoined((Player) damaged)) {

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
        final Optional<Event> event1 = EventoZero.getEventHandler().getEventByPlayer(sender);
        if (event1.isPresent())
            event1.get().playerQuit(sender);
    }

}
