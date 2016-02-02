/*
 *  Copyright (C) 2016 Leonardosc
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package io.github.bktlib.reflect;

import java.lang.reflect.Field;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public interface FieldAccessor<T>
{
	/**
	 * @return O valor do {@link Field}.
	 */
	Optional<T> getValue();

	/**
	 * Define o valor do {@link Field} para {@code newValue}
	 * 
	 * @param newValue
	 *            Valor a ser definido
	 *
	 * @throws IllegalStateException
	 *             Se o {@code field} for estatico e o objeto passado no
	 *             {@link #access(Object, String)} nao é uma instancia de um
	 *             objeto.
	 */
	void setValue( T newValue );

	/**
	 * @return Retorna o {@link Field} que está sendo acessado.
	 */
	Field getField();

	/**
	 * Acessa um determinado {@link Field}.
	 * 
	 * @param obj
	 *            Objeto que contem o {@link Field}, ou a classe caso o
	 *            {@link Field} seja estatico.
	 * @param fieldName
	 *            Nome do {@link Field} a ser acessado.
	 * @param <T>
	 *            Tipo do campo.
	 * 
	 * @return Nova instancia de {@link FieldAccessor}
	 */
	static <T> FieldAccessor<T> access( final Object obj, final String fieldName )
	{
		Preconditions.checkNotNull( obj, "obj cannot be null" );
		Preconditions.checkArgument( !Strings.isNullOrEmpty( fieldName ),
				"fieldName cannot be null or empty" );

		return new FieldAccessorImpl<>( obj, fieldName );
	}
}
