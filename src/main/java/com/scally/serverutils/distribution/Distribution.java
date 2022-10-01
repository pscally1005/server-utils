package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Distribution {

    private final List<DistributionMaterial> pairs = new ArrayList<>();
    private final double max;

    public Distribution(List<DistributionMaterial> pairs) {
        this.pairs.addAll(pairs);
        this.max = this.pairs.get(this.pairs.size() - 1).getMaxRange();
    }

    public Distribution(Set<Material> materials) {
        double sum = 0D;
        double previous;
        for (Material material : materials) {
            previous = sum;
            sum += 1D;
            pairs.add(new DistributionMaterial(material, previous, sum));
        }
        max = sum;
    }

    /**
     * @return randomly-picked Material
     */
    public Material pick() {
        final double randomDouble = Math.random() * max;
        return pick(randomDouble);
    }

    /**
     * @param threshold to use for picking
     * @return Material for the given threshold
     */
    public Material pick(double threshold) {
        if (threshold >= max) {
            return pairs.get(pairs.size() - 1).getMaterial();
        } else if (threshold <= 0) {
            return pairs.get(0).getMaterial();
        }

        int start = 0;
        int end = pairs.size() - 1;
        int mid = (start + end) / 2;

        DistributionMaterial current = pairs.get(mid);
        DistributionMaterial previous = current;

        while (start <= end) {
            mid = (start + end) / 2;
            current = pairs.get(mid);

            if (current.inRange(threshold)) {
                return current.getMaterial();
            } else if (threshold <= current.getMinRange()) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
            previous = current;
        }
        return previous.getMaterial();
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
    public List<DistributionMaterial> getPairs() {
        return new ArrayList<>(pairs);
    }

    public boolean hasMaterial(Material m) {
        for(DistributionMaterial distPair : pairs) {
            final Material mat = distPair.getMaterial();
            if(mat == m) {
                return true;
            }
        }
        return false;
    }

    public <T extends BlockData> boolean isDistributionOf(Class<T> type) {
        for (DistributionMaterial pair : pairs) {
            final BlockData blockData = pair.getMaterial().createBlockData();
            if (!type.isInstance(blockData)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Distribution{" +
                "pairs=" + pairs +
                ", max=" + max +
                '}';
    }

}
