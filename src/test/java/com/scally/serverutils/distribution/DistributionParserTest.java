package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistributionParserTest {

    @Test
    void parse_oneMaterial() {
        final String str = "birch_stairs";
        final Distribution distribution = DistributionParser.parse(str);
        assertNotNull(distribution);

        final List<DistributionItem> pairs = distribution.getMaterials();
        assertEquals(1, pairs.size());
        assertEquals(Material.BIRCH_STAIRS, pairs.getFirst().material());
        assertEquals(1D, pairs.getFirst().probRangeMax());
    }

    @Test
    void parse_oneMaterialWithRatio() {
        final String str = "33%oak_log";
        final Distribution distribution = DistributionParser.parse(str);
        assertNotNull(distribution);

        final List<DistributionItem> pairs = distribution.getMaterials();
        assertEquals(1, pairs.size());
        assertEquals(Material.OAK_LOG, pairs.getFirst().material());
        assertEquals(33D, pairs.getFirst().probRangeMax());
    }

    @Test
    void parse_twoMaterials() {
        final String str = "50%cobblestone,50%oak_planks";
        final Distribution distribution = DistributionParser.parse(str);
        assertNotNull(distribution);

        final List<DistributionItem> pairs = distribution.getMaterials();
        assertEquals(2, pairs.size());

        assertEquals(Material.COBBLESTONE, pairs.get(0).material());
        assertEquals(50D, pairs.get(0).probRangeMax());

        assertEquals(Material.OAK_PLANKS, pairs.get(1).material());
        assertEquals(100D, pairs.get(1).probRangeMax());
    }

    @Test
    void parse_threeMaterialsNoRatio() {
        final String str = "birch_planks,oak_planks,spruce_planks";
        final Distribution distribution = DistributionParser.parse(str);
        assertNotNull(distribution);

        final List<DistributionItem> pairs = distribution.getMaterials();
        assertEquals(3, pairs.size());

        boolean hasBirch = false;
        boolean hasOak = false;
        boolean hasSpruce = false;

        for (int i = 0; i < pairs.size(); i++) {
            assertEquals(i + 1D, pairs.get(i).probRangeMax());
            switch (pairs.get(i).material()) {
                case BIRCH_PLANKS -> hasBirch = true;
                case OAK_PLANKS -> hasOak = true;
                case SPRUCE_PLANKS -> hasSpruce = true;
            }
        }

        assertTrue(hasBirch);
        assertTrue(hasOak);
        assertTrue(hasSpruce);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "50%cobblestone,50%pine_log",       // invalid material
            "25%cobbled_deepslate,deepslate",   // mix percentages + non-percentages
            "25%oak_planks,35%%oak_slabs",      // multiple percent signs in material
            "sponge,sponge"                     // material found twice
    })
    void parse_invalidInput(String arg) {
        assertThrows(InvalidDistributionException.class, () -> DistributionParser.parse(arg));
    }
}
