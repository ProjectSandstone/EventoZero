/*
 *  Copyright (C) 2016 Leonardosc
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package io.github.bktlib.command;

import java.util.Optional;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

/**
 * Classe usada internamente para "adaptar" a classe {@link CommandBase} para
 * {@link org.bukkit.command.Command}
 */
final class CommandAdapter extends org.bukkit.command.Command
{
	CommandBase base;

	CommandAdapter(final CommandBase base)
	{
		super( base.getName() );

		this.base = base;

		Optional<String> perm = base.getPermission();
		Optional<String> usage = base.getUsage();
		Optional<String> desc = base.getDescription();
		Set<String> aliases = base.getAliases();

		desc.ifPresent( this::setDescription );
		perm.ifPresent( this::setPermission );
		usage.ifPresent( this::setUsage );

		if ( aliases.size() > 0 )
			setAliases( Lists.newArrayList( aliases ) );
	}

	@Override
	public boolean execute( final CommandSender sender, final String s, final String[] rawArgs )
	{
		base.execute( sender, rawArgs );
		return true;
	}
}
