package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistributionArgParserTest {

    // TODO: need better way of testing the interior of the parsing

    @Test
    public void parse_twoMaterials() {
        final String str = "50%cobblestone,50%oak_planks";
        final Distribution distribution = DistributionArgParser.parse(str);

        final Set<Material> materials = distribution.getMaterials();
        assertEquals(2, materials.size());
        assertTrue(materials.contains(Material.COBBLESTONE));
        assertTrue(materials.contains(Material.OAK_PLANKS));
    }
}
