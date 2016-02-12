/**
 *
 * This file ("PlayerInsideCuboidEvent.java") is part from EventoZero (Alpha) Project.
 *
 * Copyright (c) 2015 ReedLine Chambers. All rights reserved.
 * REEDLINE CORPORATION, PROPRIETARY/CONFIDENTIAL. Use its subject to license terms (You can see it in license.txt file)
 *
 */
package br.com.blackhubos.eventozero.events;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.util.Framework.Cuboid;

/**
 *
 * @author <a href="https://github.com/ReedFlake/">ReedFlake (reedflake@gmail.com)</a>
 *
 */
public final class PlayerInsideCuboidEvent extends PlayerEvent implements Cancellable, Listener
{

	private final static Map< br.com.blackhubos.eventozero.factory.Event, List< Entry< Player, Cuboid > > > bypass = new ConcurrentHashMap< br.com.blackhubos.eventozero.factory.Event, List< Entry< Player, Cuboid > > >();
	private final static HandlerList handler = new HandlerList();
	private boolean cancelled = false;
	private Optional< Cuboid > cuboid = Optional.empty();
	private Optional< Location > exact = Optional.empty();
	private Optional< br.com.blackhubos.eventozero.factory.Event > event = Optional.empty();

	public PlayerInsideCuboidEvent ( final Player player, final Cuboid cuboid, final br.com.blackhubos.eventozero.factory.Event event )
	{
		super( player );
		this.cuboid = Optional.of( cuboid );
		this.exact = Optional.of( player.getLocation().clone() );
		this.event = Optional.of( event );
	}

	public Optional< Cuboid > getCuboid ()
	{
		return this.cuboid;
	}

	public Optional< Location > getExactLocation ()
	{
		return this.exact;
	}

	public Optional< br.com.blackhubos.eventozero.factory.Event > getEvent ()
	{
		return this.event;
	}

	@Override
	public boolean isCancelled ()
	{
		return this.cancelled;
	}

	@Override
	public void setCancelled ( final boolean cancel )
	{
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers ()
	{
		return PlayerInsideCuboidEvent.handler;
	}

	public static HandlerList getHandlerList ()
	{
		return PlayerInsideCuboidEvent.handler;
	}

	/**
	 * TODO: remover isso daqui, colocar em uma classe separada de Listeners
	 *
	 * TODO: verificar se o jogador entrou ou se moveu dentro da cuboid; se apenas se moveu, ignorar.
	 * TODO: se entrou, chamar o insidecuboudevent
	 * TODO: verificar se ele está de fato participando
	 *
	 * @param event
	 */
	@EventHandler ( priority = EventPriority.MONITOR )
	private void whenPlayerInsideCuboid ( final PlayerMoveEvent event )
	{
		final Player sender = event.getPlayer();
		final Location at = event.getTo();
		final Location from = event.getFrom();
		final Optional< br.com.blackhubos.eventozero.factory.Event > evento = EventoZero.getEventHandler().getEventByPlayer( sender );
		if ( evento.isPresent() )
		{
			final Optional< Entry< br.com.blackhubos.eventozero.factory.Event, List< Entry< Player, Cuboid > > > > alerts = PlayerInsideCuboidEvent.bypass.entrySet().stream().filter( ( e ) -> e.getKey().getName().equalsIgnoreCase( evento.get().getName() ) ).findFirst();
			if ( alerts.isPresent() )
			{
				final List< Entry< Player, Cuboid > > players = alerts.get().getValue();
				final br.com.blackhubos.eventozero.factory.Event game = evento.get();
				final Cuboid cubo = this.cuboid.get();
				if ( cubo.containsLocation( at ) && ! cubo.containsLocation( from ) )
				{
					// O JOGADOR ENTROU
				}
				else if ( cubo.containsLocation( from ) && ! cubo.containsLocation( at ) )
				{
					// O JOGADOR SAIU
				}
			}
		}
	}

}
