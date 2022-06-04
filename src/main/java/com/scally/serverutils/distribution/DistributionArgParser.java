package com.scally.serverutils.distribution;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class DistributionArgParser {

    private DistributionArgParser() {}

    // TODO: unit tests
    public static Distribution parse(String distributionStr) {

        final List<DistributionPair> pairs = new ArrayList<>();
        final String[] materials = distributionStr.split(",");
        if (materials.length == 1) {
            final Material material = Material.matchMaterial(materials[0]);
            if (material == null) {
                return null;
            }
            return new Distribution(material);
        }

        double sum = 0D;
        for (String materialStr : materials) {
            final String[] parts = materialStr.split("%");
            if (parts.length != 2) {
                return null;
            }

            double ratio;
            try {
                ratio = Double.valueOf(parts[0]);
            } catch (NumberFormatException exception) {
                return null;
            }

            final Material material = Material.matchMaterial(parts[1]);
            if (material == null) {
                return null;
            }

            sum += ratio;
            pairs.add(new DistributionPair(material, sum));
        }

        return new Distribution(pairs);
    }

}
