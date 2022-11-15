package com.scally.serverutils.distribution;

import org.bukkit.Material;

public final class DistributionMaterial {

    private Material material;
    private double minRange;
    private double maxRange;

    public DistributionMaterial(Material material, double minRange, double maxRange) {
        this.material = material;
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public Material getMaterial() {
        return material;
    }

    public double getMinRange() {
        return minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public boolean inRange(double threshold) {
        return threshold > minRange && threshold <= maxRange;
    }

    @Override
    public String toString() {
        return "DistributionMaterial{" +
                "material=" + material +
                ", minRange=" + minRange +
                ", maxRange=" + maxRange +
                '}';
    }
}
