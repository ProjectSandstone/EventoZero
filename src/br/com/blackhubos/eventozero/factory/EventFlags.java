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

public class EventFlags {
	private int flags = Flag.DISABLE_PVP.val;

	public int getFlags() {
		return flags;
	}

	public boolean hasFlag(Flag flag) {
		return (flags & flag.val) != 0;
	}

	public void addFlag(Flag flag) {
		flags |= flag.val;
	}

	public void removeFlag(Flag flag) {
		flags &= ~flag.val;
	}

	public void setFlags(int newFlags) {
		this.flags = newFlags;
	}

	public static enum Flag {
		NONE					(1 << 0), 
		DISABLE_PVP				(1 << 1), 
		DISABLE_DAMAGE			(1 << 2), 
		DISABLE_MC				(1 << 3),
		DISABLE_BLOCK_BREAK		(1 << 4),
		DISABLE_BLOCK_PLACE		(1 << 5);

		private int val;

		private Flag(int val) {
			this.val = val;
		}
	}
}
