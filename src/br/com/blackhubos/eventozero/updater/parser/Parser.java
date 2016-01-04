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
package br.com.blackhubos.eventozero.updater.parser;

import com.google.common.base.Optional;

import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;

/**
 *
 * @param <T> Tipo para fazer "parse"
 * @param <E> Tipo de retorno
 */
public interface Parser<T, E> {

    /**
     * Um simples método para fazer parse de valores (foi utilizado em classes para fazer parse do JSON)
     * @param object Objeto para fazer parse
     * @param typeFormatter Os formatadores de objeto
     * @return O valor se conseguir fazer parse com os formatadores
     */
    Optional<E> parseObject(T object, MultiTypeFormatter typeFormatter);

}
