package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DistributionTest {

    @Test
    public void pick_length1_pickFirst() {
        final Distribution distribution = Distribution.parse("1%air");
        assertNotNull(distribution);

        final Material material = distribution.pick(0.5D);
        assertEquals(Material.AIR, material);
    }

    @ParameterizedTest
    @CsvSource(value = {"25D,STONE", "75D,COBBLESTONE"})
    public void pick_length2(double threshold, String expectedMaterial) {
        final Distribution distribution = Distribution.parse("50%stone,50%cobblestone");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @ParameterizedTest
    @CsvSource(value = {"1.5D,BIRCH_PLANKS", "2.5D,OAK_PLANKS", "3.5D,JUNGLE_PLANKS"})
    public void pick_length3(double threshold, String expectedMaterial) {
        final Distribution distribution = Distribution.parse("2%birch_planks,1%oak_planks,1%jungle_planks");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @ParameterizedTest
    @CsvSource(value = {"1.5D,BRICKS", "2.5D,POLISHED_ANDESITE", "3.5D,POLISHED_GRANITE", "5.5D,AIR"})
    public void pick_length4(double threshold, String expectedMaterial) {
        final Distribution distribution = Distribution.parse("2%bricks,1%polished_andesite,1%polished_granite,2%air");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @ParameterizedTest
    @CsvSource(value = {"-100D,STRIPPED_BIRCH_LOG", "100D,STRIPPED_ACACIA_LOG"})
    public void pick_outOfRange(double threshold, String expectedMaterial) {
        final Distribution distribution = Distribution.parse("1%stripped_birch_log,1%stripped_oak_log,1%stripped_acacia_log");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @Test
    public void parse_oneMaterial() {
        final String str = "birch_stairs";
        final Distribution distribution = Distribution.parse(str);
        assertNotNull(distribution);

        final List<DistributionMaterial> pairs = distribution.getPairs();
        assertEquals(1, pairs.size());
        assertEquals(Material.BIRCH_STAIRS, pairs.get(0).getMaterial());
        assertEquals(1D, pairs.get(0).getMaxRange());
    }

    @Test
    public void parse_oneMaterialWithRatio() {
        final String str = "33%oak_log";
        final Distribution distribution = Distribution.parse(str);
        assertNotNull(distribution);

        final List<DistributionMaterial> pairs = distribution.getPairs();
        assertEquals(1, pairs.size());
        assertEquals(Material.OAK_LOG, pairs.get(0).getMaterial());
        assertEquals(33D, pairs.get(0).getMaxRange());
    }

    @Test
    public void parse_twoMaterials() {
        final String str = "50%cobblestone,50%oak_planks";
        final Distribution distribution = Distribution.parse(str);
        assertNotNull(distribution);

        final List<DistributionMaterial> pairs = distribution.getPairs();
        assertEquals(2, pairs.size());

        assertEquals(Material.COBBLESTONE, pairs.get(0).getMaterial());
        assertEquals(50D, pairs.get(0).getMaxRange());

        assertEquals(Material.OAK_PLANKS, pairs.get(1).getMaterial());
        assertEquals(100D, pairs.get(1).getMaxRange());
    }

    @Test
    public void parse_threeMaterialsNoRatio() {
        final String str = "birch_planks,oak_planks,spruce_planks";
        final Distribution distribution = Distribution.parse(str);
        assertNotNull(distribution);

        final List<DistributionMaterial> pairs = distribution.getPairs();
        assertEquals(3, pairs.size());

        boolean hasBirch = false;
        boolean hasOak = false;
        boolean hasSpruce = false;

        for (int i = 0; i < pairs.size(); i++) {
            assertEquals(i + 1D, pairs.get(i).getMaxRange());
            switch (pairs.get(i).getMaterial()) {
                case BIRCH_PLANKS -> hasBirch = true;
                case OAK_PLANKS -> hasOak = true;
                case SPRUCE_PLANKS -> hasSpruce = true;
            }
        }

        assertTrue(hasBirch);
        assertTrue(hasOak);
        assertTrue(hasSpruce);
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

    // TODO: hasMaterial tests
    // TODO: isDistributionOf tests
}
