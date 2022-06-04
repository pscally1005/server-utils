package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class Distribution {

    private final List<DistributionPair> pairs = new ArrayList<>();
    private final double max;

    private Distribution(List<DistributionPair> pairs) {
        for (int i = 0 ; i < pairs.size(); i++) {
            this.pairs.add(pairs.get(i));
        }
        this.max = this.pairs.get(this.pairs.size() - 1).getThreshold();
    }

    private Distribution(Material material) {
        pairs.add(new DistributionPair(material, 100D));
        max = 100D;
    }

    /**
     * @param d double threshold to use for picking
     * @return Material for the given threshold
     */
    public Material pick(double d) {
        if (d > max) {
            return pairs.get(pairs.size() - 1).getMaterial();
        }

        int start = 0;
        int end = pairs.size() - 1;

        while (start < end) {
            int mid = (start + end) / 2;
            if (d == pairs.get(mid).getThreshold()) {
                // holy crap you basically won the lottery
                return pairs.get(mid).getMaterial();
            } else if (d < pairs.get(mid).getThreshold()) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        // TODO: clean up this logic
        if (start == end) {
            final double ratio = pairs.get(start).getThreshold();
            if (d > ratio) {
                return pairs.get(start + 1).getMaterial();
            } else {
                return pairs.get(start).getMaterial();
            }
        } else if (end < 0) {
            return pairs.get(start).getMaterial();
        } else if (start > pairs.size() - 1) {
            return pairs.get(end).getMaterial();
        }

        if (pairs.get(start).getThreshold() > d) {
            return pairs.get(start).getMaterial();
        } else {
            return pairs.get(end).getMaterial();
        }
    }

    /**
     * @return randomly-picked Material
     */
    public Material pick() {
        final double randomDouble = Math.random() * max;
        return pick(randomDouble);
    }

    /**
     * @param inventory to fill randomly
     */
    public void fill(Inventory inventory) {
        final ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            final double randomDouble = Math.random() * max;
            items[i] = new ItemStack(pick(randomDouble));
        }
        inventory.setContents(items);
    }

    /**
     * @return copy of the List of DistributionPairs
     */
    public List<DistributionPair> getPairs() {
        return new ArrayList<>(pairs);
    }

    /**
     * @param distributionStr arg from command
     * @return Distribution object if valid, null if invalid
     */
    public static Distribution parse(String distributionStr) {

        final List<DistributionPair> pairs = new ArrayList<>();
        final String[] materials = distributionStr.split(",");

        // Handle case where the String is just a single Material
        if (materials.length == 1 && !distributionStr.contains("%")) {
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

    @Override
    public String toString() {
        return "Distribution{" +
                "pairs=" + pairs +
                ", max=" + max +
                '}';
    }
}
