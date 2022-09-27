package com.scally.serverutils.validation;

public class Coordinates {

    private int x1;
    private int y1;
    private int z1;

    private int x2;
    private int y2;
    private int z2;

    public Coordinates(int[] coords) {
        assert coords.length == 6;

        x1 = coords[0];
        y1 = coords[1];
        z1 = coords[2];

        x2 = coords[3];
        y2 = coords[4];
        z2 = coords[5];
    }

    public int minX() {
        return Math.min(x1, x2);
    }

    public int maxX() {
        return Math.max(x1, x2);
    }

    public int minY() {
        return Math.min(y1, y2);
    }

    public int maxY() {
        return Math.max(y1, y2);
    }

    public int minZ() {
        return Math.min(z1, z2);
    }

    public int maxZ() {
        return Math.max(z1, z2);
    }
    
    public int volume() {
        return (Math.abs(x2 - x1) + 1)
                * (Math.abs(y2 - y1) + 1)
                * (Math.abs(z2 - z1) + 1);
    }

}
