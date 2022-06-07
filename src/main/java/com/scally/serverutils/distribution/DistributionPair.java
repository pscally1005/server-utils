package com.scally.serverutils.distribution;

import org.bukkit.Material;

public final class DistributionPair {

    private Material material;
    private double threshold;

    /**
     * @param material Material of the pair
     * @param threshold cumulative threshold
     */
    public DistributionPair(Material material, double threshold) {
        this.material = material;
        this.threshold = threshold;
    }

    /**
     * @return Material of this pair
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return threshold of this pair
     */
    public double getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "DistributionPair{" +
                "material=" + material +
                ", threshold=" + threshold +
                '}';
    }

}
