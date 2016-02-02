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

import java.lang.reflect.Method;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public interface MethodAccessor<T>
{
	/**
	 * Chama o método com os parametros informados.
	 * 
	 * @param params
	 *            Parametros a ser passado para o método
	 * @return Um {@link Optional} contendo o retorno do método, caso ele tenha
	 *         um.
	 */
	Optional<T> invoke( Object... params );

	/**
	 * @return O {@link Method método} que está sendo acessado.
	 */
	Method getMethod();

	/**
	 * Acessa um determinado método.
	 * 
	 * @param obj
	 *            Objeto que contem o método, ou a classe caso o método seja
	 *            estatico.
	 * @param methodName
	 *            Nome do método a ser acessado.
	 * @param params
	 *            Lista de parametros que o método recebe.
	 * @param <T>
	 *            O tipo de retorno do método.
	 *
	 * @return Nova instancia de {@link MethodAccessor}
	 */
	static <T> MethodAccessor<T> access( final Object obj, final String methodName,
			Class<?>... params )
	{
		Preconditions.checkNotNull( obj, "obj cannot be null" );
		Preconditions.checkArgument( !Strings.isNullOrEmpty( methodName ),
				"methodName cannot be null or empty" );

		return new MethodAccessorImpl<>( obj, methodName );
	}
}
