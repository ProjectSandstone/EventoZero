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
package br.com.blackhubos.eventozero.chat.interpreter;

import java.util.Arrays;

import br.com.blackhubos.eventozero.chat.interpreter.pattern.Patterns;
import static org.junit.Assert.assertEquals;

public class Test {

    @org.junit.Test
    public void testPatternAll() {
        // Patterns Test

        assertEquals("Pattern ALL Check 'home' must be true", true, Patterns.ALL.check("home"));
        assertEquals("Pattern Integer Check '-5987' must be true", true, Patterns.Integer.check("-5987"));
        assertEquals("Pattern Boolean Check 'false' must be true", true, Patterns.Boolean.check("false"));
        assertEquals("Pattern Date Check '01/01/2000' must be true", true, Patterns.Date.check("01/01/2000"));
        assertEquals("Pattern List Check 'A,B' must be true", true, Patterns.StringList.check("A,B"));
        assertEquals("Pattern Double Check '1.7' must be true", true, Patterns.Double.check("1.7"));
    }

    @org.junit.Test
    public void testTransformer() {
        // Transformer test

        assertEquals("Transform List check", Arrays.asList("A", "B", "C"), Patterns.StringList.getTransformer().transform("A, B, C"));

    }
}
