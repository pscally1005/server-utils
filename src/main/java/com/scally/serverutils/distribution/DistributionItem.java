package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public record DistributionItem(Material material, double probRangeMin, double probRangeMax, int stackSizeMin, int stackSizeMax) {

    public boolean inRange(double threshold) {
        return threshold > probRangeMin && threshold <= probRangeMax;
    }

    public ItemStack pickStack() {
        final ItemStack itemStack = new ItemStack(material);
        final int amount = ThreadLocalRandom.current().nextInt(stackSizeMin, stackSizeMax + 1);
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public String toString() {
        return "DistributionItem{" +
                "material=" + material +
                ", probRangeMin=" + probRangeMin +
                ", probRangeMax=" + probRangeMax +
                ", stackSizeMin=" + stackSizeMin +
                ", stackSizeMax=" + stackSizeMax +
                '}';
    }
}
