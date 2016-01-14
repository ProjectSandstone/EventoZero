/**
 *
 * This file ("CuboidListener.java") is part from EventoZero (Alpha) Project.
 *
 * Copyright (c) 2015 ReedLine Chambers. All rights reserved.
 * REEDLINE CORPORATION, PROPRIETARY/CONFIDENTIAL. Use its subject to license terms (You can see it in license.txt file)
 *
 */
package br.com.blackhubos.eventozero.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.blackhubos.eventozero.events.PlayerInsideCuboidEvent;

public final class CuboidListener implements Listener
{

	@EventHandler(priority = EventPriority.MONITOR)
	private void whenPlayerMove(final PlayerInsideCuboidEvent event)
	{

	}

}
