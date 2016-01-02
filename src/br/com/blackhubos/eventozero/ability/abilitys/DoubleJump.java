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
package br.com.blackhubos.eventozero.ability.abilitys;

import org.bukkit.entity.Player;

import br.com.blackhubos.eventozero.ability.Ability;

public final class DoubleJump extends Ability
{

	public DoubleJump(final long cooldown)
	{
		super(cooldown);
	}

	@Override
	public boolean tryUse(final Player player)
	{
		if (this.canUse())
		{
			this.foceUse(player);
			return true;
		}
		return false;
	}

	@Override
	public void foceUse(final Player player)
	{
		player.setVelocity(player.getLocation().getDirection().multiply(0.5).setY(1));
	}

}
