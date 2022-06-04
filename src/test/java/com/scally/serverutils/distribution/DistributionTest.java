package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistributionTest {

    private static final Material[] MATERIALS = Material.values();

    @Test
    public void pick_length1_pickFirst() {
        final Distribution distribution = distributionOf(new double[] { 1D });
        final Material material = distribution.pick(0.5D);
        assertEquals(MATERIALS[0], material);
    }

    @Test
    public void pick_length2() {
        final Distribution distribution = distributionOf(new double[] { 50D, 50D });
        final Material first = distribution.pick(25D);
        final Material second = distribution.pick(75D);

        assertEquals(MATERIALS[0], first);
        assertEquals(MATERIALS[1], second);
    }

    @Test
    public void pick_length3() {
        final Distribution distribution = distributionOf(new double[] { 2D, 1D, 1D });
        final Material first = distribution.pick(1.5D);
        final Material second = distribution.pick(2.5D);
        final Material third = distribution.pick(3.5D);

        assertEquals(MATERIALS[0], first);
        assertEquals(MATERIALS[1], second);
        assertEquals(MATERIALS[2], third);
    }

    @Test
    public void pick_length4() {
        final Distribution distribution = distributionOf(new double[] { 2D, 1D, 1D, 2D });
        final Material first = distribution.pick(1.5D);
        final Material second = distribution.pick(2.5D);
        final Material third = distribution.pick(3.5D);
        final Material fourth = distribution.pick(5.5D);

        assertEquals(MATERIALS[0], first);
        assertEquals(MATERIALS[1], second);
        assertEquals(MATERIALS[2], third);
        assertEquals(MATERIALS[3], fourth);
    }

    @Test
    public void pick_outOfRange() {
        final Distribution distribution = distributionOf(new double[] { 1D, 1D, 1D });
        final Material lessThanMin = distribution.pick(-100D);
        final Material greaterThanMax = distribution.pick(100D);

        assertEquals(MATERIALS[0], lessThanMin);
        assertEquals(MATERIALS[2], greaterThanMax);
    }

    private Distribution distributionOf(double... ratios) {
        final List<DistributionPair> pairs = new ArrayList<>();
        double sum = 0D;
        for (int i = 0; i < ratios.length; i++) {
            sum += ratios[i];
            final Material material = Material.values()[i];
            pairs.add(new DistributionPair(material, sum));
        }
        return new Distribution(pairs);
    }

}
