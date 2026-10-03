package com.github.lunatrius.schematica.client.printer.source;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlaceableTest {
    @Test
    public void storedMinusKept() {
        assertEquals(199, Placeable.count(200, 1));
        assertEquals(10, Placeable.count(10, 0));
    }

    @Test
    public void neverNegative() {
        assertEquals(0, Placeable.count(5, 16));
        assertEquals(0, Placeable.count(0, 0));
    }

    @Test
    public void keepAllMeansNothingPlaceable() {
        assertEquals(0, Placeable.count(200, Integer.MAX_VALUE));
    }
}
