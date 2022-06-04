package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DistributionTest {

    @Test
    public void pick_length1_pickFirst() {
        final Distribution distribution = Distribution.parse("1%air");
        final Material material = distribution.pick(0.5D);
        assertEquals(Material.AIR, material);
    }

    @Test
    public void pick_length2() {
        final Distribution distribution = Distribution.parse("50%stone,50%cobblestone");
        final Material first = distribution.pick(25D);
        final Material second = distribution.pick(75D);

        assertEquals(Material.STONE, first);
        assertEquals(Material.COBBLESTONE, second);
    }

    @Test
    public void pick_length3() {
        final Distribution distribution = Distribution.parse("2%birch_planks,1%oak_planks,1%jungle_planks");
        final Material first = distribution.pick(1.5D);
        final Material second = distribution.pick(2.5D);
        final Material third = distribution.pick(3.5D);

        assertEquals(Material.BIRCH_PLANKS, first);
        assertEquals(Material.OAK_PLANKS, second);
        assertEquals(Material.JUNGLE_PLANKS, third);
    }

    @Test
    public void pick_length4() {
        final Distribution distribution = Distribution.parse("2%bricks,1%polished_andesite,1%polished_granite,2%air");
        final Material first = distribution.pick(1.5D);
        final Material second = distribution.pick(2.5D);
        final Material third = distribution.pick(3.5D);
        final Material fourth = distribution.pick(5.5D);

        assertEquals(Material.BRICKS, first);
        assertEquals(Material.POLISHED_ANDESITE, second);
        assertEquals(Material.POLISHED_GRANITE, third);
        assertEquals(Material.AIR, fourth);
    }

    @Test
    public void pick_outOfRange() {
        final Distribution distribution = Distribution.parse("1%stripped_birch_log,1%stripped_oak_log,1%stripped_acacia_log");
        final Material lessThanMin = distribution.pick(-100D);
        final Material greaterThanMax = distribution.pick(100D);

        assertEquals(Material.STRIPPED_BIRCH_LOG, lessThanMin);
        assertEquals(Material.STRIPPED_ACACIA_LOG, greaterThanMax);
    }

    @Test
    public void parse_oneMaterial() {
        final String str = "birch_stairs";
        final Distribution distribution = Distribution.parse(str);

        final List<DistributionPair> pairs = distribution.getPairs();
        assertEquals(1, pairs.size());
        assertEquals(Material.BIRCH_STAIRS, pairs.get(0).getMaterial());
        assertEquals(100D, pairs.get(0).getThreshold());
    }

    @Test
    public void parse_oneMaterialWithRatio() {
        final String str = "33%oak_log";
        final Distribution distribution = Distribution.parse(str);

        final List<DistributionPair> pairs = distribution.getPairs();
        assertEquals(1, pairs.size());
        assertEquals(Material.OAK_LOG, pairs.get(0).getMaterial());
        assertEquals(33D, pairs.get(0).getThreshold());
    }

    @Test
    public void parse_twoMaterials() {
        final String str = "50%cobblestone,50%oak_planks";
        final Distribution distribution = Distribution.parse(str);

        final List<DistributionPair> pairs = distribution.getPairs();
        assertEquals(2, pairs.size());

        assertEquals(Material.COBBLESTONE, pairs.get(0).getMaterial());
        assertEquals(50D, pairs.get(0).getThreshold());

        assertEquals(Material.OAK_PLANKS, pairs.get(1).getMaterial());
        assertEquals(100D, pairs.get(1).getThreshold());
    }

    @Test
    public void parse_invalidMaterial() {
        final String str = "50%cobblestone,50%pine_log";
        final Distribution distribution = Distribution.parse(str);
        assertNull(distribution);
    }

    @Test
    public void parse_invalidFormat() {
        final String str = "25%cobbled_deepslate,deepslate";
        final Distribution distribution = Distribution.parse(str);
        assertNull(distribution);
    }
}
