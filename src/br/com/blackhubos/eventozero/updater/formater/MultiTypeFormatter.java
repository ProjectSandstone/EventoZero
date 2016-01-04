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

import com.google.common.base.Optional;

import java.util.HashSet;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Formatador anonimo (ou multiplo dependendo da forma de seu uso)
 */
public class MultiTypeFormatter {

    private Set<TypeFormatter<?>> formatterSet = new HashSet<>();

    /**
     * Registra um formatador
     * @param formatter Formatador para registrar
     */
    public void registerFormatter(TypeFormatter<?> formatter) {
        formatterSet.add(formatter);
    }

    /**
     * Desregistra um formatador
     * @param formatter Formatador para desregistrar
     */
    public void unregisterFormatter(TypeFormatter<?> formatter) {
        formatterSet.remove(formatter);
    }

    /**
     * Desregistra um formatador baseado na sua classe
     * @param formatterClass Classe do formatador
     */
    public void unregisterFormatter(Class<? extends TypeFormatter> formatterClass) {
        for (TypeFormatter<?> typeFormatter : formatterSet) {
            if (typeFormatter.getClass() == formatterClass) {
                formatterSet.remove(typeFormatter);
            }
        }
    }

    /**
     * Desregistra um formatador baseado na classe que ele pode formatar
     * @param formatClass Classe que o formatador pode formatar
     */
    public void unregisterFormatterType(Class<?> formatClass) {
        for (TypeFormatter<?> typeFormatter : formatterSet) {
            if (typeFormatter.getFormatClass() == formatClass) {
                formatterSet.remove(typeFormatter);
            }
        }
    }

    /**
     * Determina se o formatador pode formatar a classe
     * @param classToFormat Classe para verificar se é possivel formatar a partir dos formatadores registrados
     * @return True se puder formatar, ou false caso contrario.
     */
    public boolean canFormat(Class<?> classToFormat) {
        for (TypeFormatter<?> typeFormatter : formatterSet) {
            if (typeFormatter.getFormatClass() == classToFormat) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tenta formatar baseado na classe do tipo (não precisa ser necessariamente a classe exata)
     * Para algo mais preciso use {@link #formatSpecific(Object, Class)}
     * @see #formatSpecific(Object, Class)
     * @param value Valor a formatar
     * @param target Tipo para retorno
     * @param <T> Tipo
     * @return Valor formatado
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> format(Object value, Class<? extends T> target) {
        for (TypeFormatter<?> typeFormatter : formatterSet) {
            if (typeFormatter.getFormatClass().isAssignableFrom(target)) {
                return (Optional<T>) typeFormatter.formatType(value);
            }
        }
        return Optional.absent();
    }

    /**
     * Tenta formatar com o formatador especifico para o valor
     * Para um formatador menos exato use {@link #format(Object, Class)}
     * @see #format(Object, Class)
     * @param value Valor a formatar
     * @param target Tipo para retorno
     * @param <T> Tipo
     * @return Valor formatado
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> formatSpecific(Object value, Class<? extends T> target) {
        for (TypeFormatter<?> typeFormatter : formatterSet) {
            if (typeFormatter.getFormatClass() == target) {
                return (Optional<T>) typeFormatter.formatType(value);
            }
        }
        return Optional.absent();
    }

    /**
     * Tenta formatar com todos formatadores
     * @see #format(Object, Class)
     * @see #formatSpecific(Object, Class)
     * @param value Valor a formatar
     * @param <T> Tipo
     * @return Valor formatado
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> tryFormatAll(Object value) {
        for (TypeFormatter<?> typeFormatter : formatterSet) {
            try {
                return (Optional<T>) typeFormatter.tryFormatType(value);
            } catch (CannotFormatTypeException ignored) {
            }
        }
        return Optional.absent();
    }
}
