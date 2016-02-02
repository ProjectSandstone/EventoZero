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

import io.github.bktlib.common.Strings;

public class MethodRef
{
	private Object owner;
	private Method method;
	
	private MethodRef( Object instance, Method method )
	{
		this.owner = instance;
		this.method = method;
	}
	
	public Object getOwner()
	{
		return owner;
	}
	
	public Method getMethod()
	{
		return method;
	}
	
	public static MethodRef of( Object instance, Method method )
	{
		return new MethodRef( instance, method );
	}
	
	@Override
	public String toString()
	{
		return Strings.of( owner, "#", method == null ? "null" : method.getName() );
	}
}
