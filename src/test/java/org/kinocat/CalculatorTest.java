package org.kinocat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class CalculatorTest {

    @Test
    public void calculate() {
        assertEquals(6, Calculator.calculate("2 + 2 * 2"), 0);
    }

    @Test
    public void calculateWithBrackets() {
        assertEquals(8, Calculator.calculate("(2 + 2) * 2"), 0);
    }

    @Test
    public void calcNumbers() {
        assertEquals(3, Calculator.calcNumbers("1. 1.3 .1"));
    }
}