package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DistributionTest {

    @Test
    public void pick_length1_pickFirst() {
        final Distribution distribution = DistributionParser.parse("1%air");
        assertNotNull(distribution);

        final Material material = distribution.pick(0.5D);
        assertEquals(Material.AIR, material);
    }

    @ParameterizedTest
    @CsvSource(value = {"25D,STONE", "75D,COBBLESTONE"})
    public void pick_length2(double threshold, String expectedMaterial) {
        final Distribution distribution = DistributionParser.parse("50%stone,50%cobblestone");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @ParameterizedTest
    @CsvSource(value = {"1.5D,BIRCH_PLANKS", "2.5D,OAK_PLANKS", "3.5D,JUNGLE_PLANKS"})
    public void pick_length3(double threshold, String expectedMaterial) {
        final Distribution distribution = DistributionParser.parse(
                "2%birch_planks,1%oak_planks,1%jungle_planks");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @ParameterizedTest
    @CsvSource(value = {"1.5D,BRICKS", "2.5D,POLISHED_ANDESITE", "3.5D,POLISHED_GRANITE", "5.5D,AIR"})
    public void pick_length4(double threshold, String expectedMaterial) {
        final Distribution distribution = DistributionParser.parse(
                "2%bricks,1%polished_andesite,1%polished_granite,2%air");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    @ParameterizedTest
    @CsvSource(value = {"-100D,STRIPPED_BIRCH_LOG", "100D,STRIPPED_ACACIA_LOG"})
    public void pick_outOfRange(double threshold, String expectedMaterial) {
        final Distribution distribution = DistributionParser.parse(
                "1%stripped_birch_log,1%stripped_oak_log,1%stripped_acacia_log");
        assertNotNull(distribution);

        final Material material = distribution.pick(threshold);
        assertEquals(Material.getMaterial(expectedMaterial), material);
    }

    // TODO: hasMaterial tests
    // TODO: isDistributionOf tests
}
