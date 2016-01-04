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

import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Formatador de tipos
 *
 * @param <T> Tipo suportado
 */
public interface TypeFormatter<T> {

    /**
     * Formata o objectToFormat e retorna o tipo T Pode retornar um valor nulo, para evitar isto use
     * {@link #tryFormatType(Object)} ou {@link #formatType(Object)} OBS Para Implementação: é
     * altamente recomendado a implementação utilizar este método para formatar os tipos e os demais
     * para Fazer a chamada deste método
     *
     * @param objectToFormat Objeto para formatar
     * @return Valor formatado
     * @see BooleanFormatter Para ter um modelo de implementação
     * @see #tryFormatType(Object)
     * @see #formatType(Object)
     * @deprecated Este método pode retornar valores nulos, provavelmente você não quer receber um
     * NullExceptionPointer né?
     */
    T unsecuredFormatType(Object objectToFormat);

    /**
     * Tem a mesma função que o {@link #formatType(Object)} Este método pode gerar uma exceção do
     * tipo {@link br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException}
     * Você pode usar o {@link #formatType(Object)} para evitar exceções e valores nulos!
     *
     * @param objectToFormat Objecto para formatar
     * @return Valor formatado
     * @see #formatType(Object)
     */
    T tryFormatType(Object objectToFormat) throws CannotFormatTypeException;

    /**
     * Melhor forma para formatar tipos, impede valores nulos e evita exceções
     *
     * @param objectToFormat Objeto para formatar
     * @return Valor formatado
     */
    Optional<T> formatType(Object objectToFormat);

    /**
     * Retorna as classe que ele pode formatar Motivo deste método: Genéricos não funciona muito bem
     * com reflection para obter esta classe.
     *
     * @return Classe que ele pode formatar
     */
    Class<? extends T> getFormatClass();
}
