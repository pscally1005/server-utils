package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Distribution {

    private final List<DistributionPair> pairs = new ArrayList<>();
    private final double max;
    private final Set<Material> materials = new HashSet<>();

    public Distribution(List<DistributionPair> pairs) {
        for (int i = 0 ; i < pairs.size(); i++) {
            this.pairs.add(pairs.get(i));
            this.materials.add(pairs.get(i).getMaterial());
        }
        this.max = this.pairs.get(this.pairs.size() - 1).getRatio();
    }

    public Distribution(Material material) {
        pairs.add(new DistributionPair(material, 100D));
        max = 100D;
        materials.add(material);
    }

    public Material pick(double d) {
        if (d > max) {
            return pairs.get(pairs.size() - 1).getMaterial();
        }

        int start = 0;
        int end = pairs.size() - 1;

        while (start < end) {
            int mid = (start + end) / 2;
            if (d == pairs.get(mid).getRatio()) {
                // holy crap you basically won the lottery
                return pairs.get(mid).getMaterial();
            } else if (d < pairs.get(mid).getRatio()) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        // TODO: clean up this logic
        if (start == end) {
            final double ratio = pairs.get(start).getRatio();
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

        if (pairs.get(start).getRatio() > d) {
            return pairs.get(start).getMaterial();
        } else {
            return pairs.get(end).getMaterial();
        }
    }

    public void fill(Inventory inventory) {
        final ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            final double randomDouble = Math.random() * max;
            items[i] = new ItemStack(pick(randomDouble));
        }
        inventory.setContents(items);
    }

    public Set<Material> getMaterials() {
        return new HashSet<>(materials);
    }
}
