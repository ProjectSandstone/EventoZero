package br.com.blackhubos.eventozero.chat.interpreter;

import java.util.Arrays;

import br.com.blackhubos.eventozero.chat.interpreter.pattern.Patterns;
import static org.junit.Assert.assertEquals;

/**
 * Created by jonathan on 12/01/16.
 */
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
