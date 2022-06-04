package com.scally.serverutils.distribution;

import org.bukkit.Material;

public class DistributionPair {

    private Material material;
    private double ratio;

    public DistributionPair(Material material, double ratio) {
        this.material = material;
        this.ratio = ratio;
    }

    public Material getMaterial() {
        return material;
    }

    public double getRatio() {
        return ratio;
    }

}
