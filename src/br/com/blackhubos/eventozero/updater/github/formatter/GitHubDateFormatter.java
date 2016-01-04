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
package br.com.blackhubos.eventozero.updater.github.formatter;

import com.google.common.base.Optional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import br.com.blackhubos.eventozero.updater.formater.TypeFormatter;
import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Formatador de datas do GitHub
 */
public class GitHubDateFormatter implements TypeFormatter<Date> {

    private static final String[] GITHUB_DATE_FORMATS = {"yyyy/MM/dd HH:mm:ss ZZZZ", "yyyy-MM-dd'T'HH:mm:ss'Z'"};

    /**
     * Formata a data
     * @param dateString Representação em texto
     * @return Data
     */
    private static Date parseDate(String dateString) {
        for (String format : GITHUB_DATE_FORMATS) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                return simpleDateFormat.parse(dateString);
            } catch (java.text.ParseException ignored) {
            }
        }
        throw new IllegalStateException("Unable to parse github date timestamp: " + dateString);
    }

    @Override
    public Date unsecuredFormatType(Object objectToFormat) {
        if (objectToFormat instanceof Date) {
            return (Date) objectToFormat;
        }
        String tryDate = String.valueOf(objectToFormat);

        try {
            return parseDate(tryDate);
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    @Override
    public Date tryFormatType(Object objectToFormat) throws CannotFormatTypeException {
        Date result = unsecuredFormatType(objectToFormat);
        if (result == null) {
            throw new CannotFormatTypeException(String.format("Cannot format type: 'Class[%s]'", objectToFormat.getClass().getCanonicalName()));
        }
        return result;
    }

    @Override
    public Optional<Date> formatType(Object objectToFormat) {
        return Optional.fromNullable(unsecuredFormatType(objectToFormat));
    }

    @Override
    public Class<? extends Date> getFormatClass() {
        return Date.class;
    }

}
