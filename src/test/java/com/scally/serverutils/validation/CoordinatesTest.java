package com.scally.serverutils.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinatesTest {

    private final int[] coords = {1, 2, 3, 4, 5, 6};
    private final Coordinates coordinates = new Coordinates(coords);

    @Test
    public void Coordinates_minX() {
        assertEquals(1, coordinates.minX());
    }

    @Test
    public void Coordinates_minY() {
        assertEquals(2, coordinates.minY());
    }

    @Test
    public void Coordinates_minZ() {
        assertEquals(3, coordinates.minZ());
    }

    @Test
    public void Coordinates_maxX() {
        assertEquals(4, coordinates.maxX());
    }

    @Test
    public void Coordinates_maxY() {
        assertEquals(5, coordinates.maxY());
    }

    @Test
    public void Coordinates_maxZ() {
        assertEquals(6, coordinates.maxZ());
    }

    @Test
    public void Coordinates_volume() {
        int vol = (4-1 + 1) * (5-2 + 1) * (6-3 + 1);
        assertEquals(vol, coordinates.volume());
    }

}
