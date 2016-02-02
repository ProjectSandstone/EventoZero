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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

class MethodAccessorImpl<T> extends AbstractAccessor implements MethodAccessor<T>
{
	private Method method;

	MethodAccessorImpl(final Object owner, final String methodName, final Class<?> ... params)
	{
		super( owner );

		Class<?> klass = owner instanceof Class ? (Class<?>) owner : owner.getClass();

		final Method ret = findMethodRecursive( klass, methodName, params );

		if ( ret == null )
			throw new RuntimeException(
					String.format( "could not find method %s.%s", klass, methodName ) );

		this.method = ret;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> invoke( Object... params )
	{
		if ( !method.isAccessible() )
			method.setAccessible( true );

		try
		{
			return (Optional<T>) Optional.ofNullable( method.invoke( owner, params ) );
		}
		catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
		{
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Method getMethod()
	{
		return method;
	}
	
	private static Method findMethodRecursive( final Class<?> klass, final String methodName,
			final Class<?>... params )
	{
		if ( klass == null )
			return null;

		try
		{
			return klass.getDeclaredMethod( methodName, params );
		}
		catch ( NoSuchMethodException e )
		{
			return findMethodRecursive( klass.getSuperclass(), methodName, params );
		}
		catch ( SecurityException e )
		{
			e.printStackTrace();
		}

		return null;
	}
}
