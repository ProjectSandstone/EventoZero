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
package br.com.blackhubos.eventozero.updater;

/**
 * CallBack são métodos/funções que são informadas para serem chamadas de volta. Como o java não
 * permite informar MÉTODOS como parametro (Java não é 100% Orientado a Objeto), então foi
 * necessário criar uma interface para isto
 *
 * @param <T> Tipo do objeto
 */
public interface CallBack<T> {
    /**
     * Método que será chamado de volta
     */
    void callBack(T object);
}
