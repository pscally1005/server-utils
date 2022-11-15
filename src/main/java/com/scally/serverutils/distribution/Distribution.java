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

    private final List<DistributionMaterial> materials = new ArrayList<>();
    private final double max;

    public Distribution(List<DistributionMaterial> materials) {
        this.materials.addAll(materials);
        this.max = this.materials.get(this.materials.size() - 1).getMaxRange();
    }

    public Distribution(Set<Material> materials) {
        double sum = 0D;
        double previous;
        for (Material material : materials) {
            previous = sum;
            sum += 1D;
            this.materials.add(new DistributionMaterial(material, previous, sum));
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
            return materials.get(materials.size() - 1).getMaterial();
        } else if (threshold <= 0) {
            return materials.get(0).getMaterial();
        }

        int start = 0;
        int end = materials.size() - 1;
        int mid = (start + end) / 2;

        DistributionMaterial current = materials.get(mid);
        DistributionMaterial previous = current;

        while (start <= end) {
            mid = (start + end) / 2;
            current = materials.get(mid);

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
     * @return copy of the List of DistributionMaterials
     */
    @VisibleForTesting
    public List<DistributionMaterial> getMaterials() {
        return new ArrayList<>(materials);
    }

    public boolean hasMaterial(Material m) {
        for (DistributionMaterial distMaterial : materials) {
            final Material mat = distMaterial.getMaterial();
            if (mat == m) {
                return true;
            }
        }
        return false;
    }

    public boolean isDistributionOf(Tag<Material> tag) {
        return materials.stream().allMatch(m -> tag.getValues().contains(m.getMaterial()));
    }

    @Override
    public String toString() {
        return "Distribution{" +
                "materials=" + materials +
                ", max=" + max +
                '}';
    }
}
