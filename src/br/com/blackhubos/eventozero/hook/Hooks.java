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

package br.com.blackhubos.eventozero.hook;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import br.com.blackhubos.eventozero.EventoZero;
import io.github.bktlib.reflect.MethodAccessor;

public class Hooks 
{
	private static final ClassLoader classLoader;
	private static final Logger logger = EventoZero.getInstance().getLogger();
	private static final ClassToInstanceMap<Hook> hooks = MutableClassToInstanceMap.create();
	
	static {
		MethodAccessor<ClassLoader> getClassLoaderMethod = 
				MethodAccessor.access(EventoZero.getInstance(), "getClassLoader");
		classLoader = getClassLoaderMethod.invoke().get();
	}
	
	/**
	 * Pega um hook a partir de uma classe.
	 * 
	 * @param hookClass
	 *            Classe do hook
	 * @return A instancia do hook associado a classe informada.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getHook(Class<T> hookClass)
	{
		return (T) hooks.get(hookClass);
	}
	
	/**
	 * @return Uma {@ ImmutableSet} contendo todos os hooks carregados.
	 */
	public static ImmutableSet<Hook> getHooks()
	{
		return ImmutableSet.copyOf(hooks.values());
	}
	
	@SuppressWarnings("unchecked")
	public static void hookAll() {
		
		try {
			ImmutableSet<ClassInfo> classes = ClassPath.from(classLoader)
					.getTopLevelClasses("br.com.blackhubos.eventozero.hook.hooks");
			
			classes.forEach( cls -> {
				try {
					Class<?> hookClass = Class.forName(cls.getName());
					
					if (!Hook.class.isAssignableFrom(hookClass)) 
					{
						return;
					}
					
					Hook hookInst = (Hook) hookClass.newInstance();
					
					if ( hookInst.canBeHooked() ) {
						hookInst.hook();
						hooks.put( (Class<Hook>) hookClass, hookInst );
						logger.info(String.format("[Hooks] %s carregado com sucesso.", cls.getSimpleName()));
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not load hook " + cls.getName());
					logger.log(Level.SEVERE, "See the stacktrace: ");
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
