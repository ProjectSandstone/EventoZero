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
package br.com.blackhubos.eventozero.updater.formater;

import java.util.Optional;

import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Formata Booleanos
 */
public class BooleanFormatter implements TypeFormatter<Boolean> {

    @Override
    public Boolean unsecuredFormatType(Object objectToFormat) {
        if (objectToFormat instanceof Boolean) {
            return (Boolean) objectToFormat;
        } else {
            if (objectToFormat.equals("true")) {
                return true;
            } else if (objectToFormat.equals("false")) {
                return false;
            }
        }
        return null;
    }

    @Override
    public Boolean tryFormatType(Object objectToFormat) throws CannotFormatTypeException {
        Boolean result = unsecuredFormatType(objectToFormat);
        if (result == null) {
            throw new CannotFormatTypeException(String.format("Cannot format type: 'Class[%s]'", objectToFormat.getClass().getCanonicalName()));
        }
        return result;
    }

    @Override
    public Optional<Boolean> formatType(Object objectToFormat) {
        return Optional.ofNullable(unsecuredFormatType(objectToFormat));
    }

    @Override
    public Class<? extends Boolean> getFormatClass() {
        return Boolean.class;
    }
}
