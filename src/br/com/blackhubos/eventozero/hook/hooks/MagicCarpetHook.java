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

package br.com.blackhubos.eventozero.hook.hooks;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import br.com.blackhubos.eventozero.hook.Hook;
import io.github.bktlib.reflect.FieldAccessor;
import io.github.bktlib.reflect.MethodAccessor;

public class MagicCarpetHook extends Hook {

	private static final String MAGICCARPET_CLASS = "net.digiex.magiccarpet.MagicCarpet";

	private MethodAccessor<Void> removeCarpetMethod;
	private MethodAccessor<Object> getCarpetMethod;

	public MagicCarpetHook() {
		super("MagicCarpet");
	}

	@Override
	public boolean canBeHooked() {
		boolean b = true;

		try {
			Class.forName(MAGICCARPET_CLASS);
		} catch (ClassNotFoundException e) {
			b = false;
		}

		return b;
	}

	public void disableCarpet(final Player player) {
		Preconditions.checkNotNull(player, "player cannot be null");
		removeCarpetMethod.invoke(player);
	}
	
	public boolean isCarpetEnabled(final Player player)
	{
		Preconditions.checkNotNull(player, "player cannot be null");
		Optional<Object> optCarpet = getCarpetMethod.invoke(player);
		
		if (!optCarpet.isPresent())
			return false;
		
		Object carpet = optCarpet.get();
		return MethodAccessor.<Boolean>access(carpet, "isVisible").invoke().get();
	}

	@Override
	public void hook() throws Exception {
		Class<?> magicCarpetCls = Class.forName(MAGICCARPET_CLASS);
		FieldAccessor<Object> carpetsField = FieldAccessor.access(magicCarpetCls, "carpets");

		Object storageInstance = carpetsField.getValue().get();
		
		removeCarpetMethod = MethodAccessor.access(storageInstance, "remove", Player.class);
		getCarpetMethod = MethodAccessor.access(storageInstance, "getCarpet", Player.class);
	}
}
