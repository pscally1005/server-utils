package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Distribution {

    private final List<DistributionPair> pairs = new ArrayList<>();
    private final double max;

    private Distribution(List<DistributionPair> pairs) {
        for (int i = 0 ; i < pairs.size(); i++) {
            this.pairs.add(pairs.get(i));
        }
        this.max = this.pairs.get(this.pairs.size() - 1).getThreshold();
    }

    private Distribution(Set<Material> materials) {
        double sum = 0D;
        for (Material material : materials) {
            sum += 1D;
            pairs.add(new DistributionPair(material, sum));
        }
        max = sum;
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

        // Check that all materials either have a % or don't
        boolean allHavePercentage = true;
        boolean noneHavePercentage = true;

        final Set<Material> materialSet = new HashSet<>();
        for (String materialStr : materials) {
            final String[] parts = materialStr.split("%");
            if (parts.length == 1) {
                allHavePercentage = false;
            } else if (parts.length == 2) {
                noneHavePercentage = false;
            } else {
                return null;
            }

            if (!allHavePercentage && !noneHavePercentage) {
                return null;
            }

            final Material material = Material.matchMaterial(parts[parts.length - 1]);
            if (material == null || materialSet.contains(material)) {
                return null;
            }
            materialSet.add(material);
        }

        if (noneHavePercentage) {
            return new Distribution(materialSet);
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

    public boolean hasMaterial(Material m) {

        for(DistributionPair distPair : pairs) {
            final Material mat = distPair.getMaterial();
            if(mat == m) {
                return true;
            }
        }
        return false;

    }

    @Override
    public String toString() {
        return "Distribution{" +
                "pairs=" + pairs +
                ", max=" + max +
                '}';
    }

}
