package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Distribution {

    private final List<DistributionItem> items = new ArrayList<>();
    private final double max;

    public Distribution(List<DistributionItem> items) {
        this.items.addAll(items);
        this.max = this.items.getLast().probRangeMax();
    }

    public Distribution(Set<Material> materials) {
        double sum = 0D;
        double previous;
        for (Material material : materials) {
            previous = sum;
            sum += 1D;
            this.items.add(new DistributionItem(material, previous, sum, 1, 1));
        }
        max = sum;
    }

    /**
     * @return randomly-picked Material
     */
    public Material pick() {
        final double randomDouble = Math.random() * max;
        return pick(randomDouble).material();
    }

    /**
     * @param threshold to use for picking
     * @return ItemStack for the given threshold
     */
    public DistributionItem pick(double threshold) {
        if (threshold >= max) {
            return items.getLast();
        } else if (threshold <= 0) {
            return items.getFirst();
        }

        int start = 0;
        int end = items.size() - 1;
        int mid = (start + end) / 2;

        DistributionItem current = items.get(mid);
        DistributionItem previous = current;

        while (start <= end) {
            mid = (start + end) / 2;
            current = items.get(mid);

            if (current.inRange(threshold)) {
                return current;
            } else if (threshold <= current.probRangeMin()) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
            previous = current;
        }
        return previous;
    }

    /**
     * @param inventory to fill randomly
     */
    public void fill(Inventory inventory) {
        final ItemStack[] inventoryContents = inventory.getContents();
        for (int i = 0; i < inventoryContents.length; i++) {
            final double randomDouble = Math.random() * max;
            final DistributionItem distItem = pick(randomDouble);
            inventoryContents[i] = distItem.pickStack();
        }
        inventory.setContents(inventoryContents);
    }

    /**
     * @return copy of the List of DistributionMaterials
     */
    @VisibleForTesting
    public List<DistributionItem> getMaterials() {
        return new ArrayList<>(items);
    }

    public boolean hasMaterial(Material m) {
        for (DistributionItem distItem : items) {
            final Material mat = distItem.material();
            if (mat == m) {
                return true;
            }
        }
        return false;
    }

    public boolean isDistributionOf(Tag<Material> tag) {
        return items.stream().allMatch(i -> tag.getValues().contains(i.material()));
    }

    @Override
    public String toString() {
        return "Distribution{" +
                "items=" + items +
                ", max=" + max +
                '}';
    }
}
