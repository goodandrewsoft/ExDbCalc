package org.kinocat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void calculate() {
        assertEquals(6, Calculator.calculate("2 + 2 * 2"));
    }

    @Test
    void calculateWithBrackets() {
        assertEquals(8, Calculator.calculate("(2 + 2) * 2"));
    }
}