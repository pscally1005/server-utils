package com.scally.serverutils.shelves;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;
import org.bukkit.block.data.type.Shelf;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShelvesChangeTest {

    private ServerMock serverMock;
    private World world;
    private Location location;
    private ShelvesChange change;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 10, 20, 30);

        change = new ShelvesChange(
                location,
                Material.ACACIA_SHELF,
                Material.BIRCH_SHELF,
                BlockFace.WEST,
                SideChaining.ChainPart.RIGHT,
                true,
                false,
                new ItemStack[0]
        );
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void undo_happyPath() {
        location.getBlock().setType(Material.BIRCH_SHELF, false);

        final boolean result = change.undo();
        assertTrue(result);

        final BlockData blockDataAfterUndo = world.getBlockData(location);
        assertInstanceOf(Shelf.class, blockDataAfterUndo);

        final Shelf shelf = (Shelf) blockDataAfterUndo;
        assertEquals(Material.ACACIA_SHELF, shelf.getMaterial());
        assertEquals(BlockFace.WEST, shelf.getFacing());
        assertEquals(SideChaining.ChainPart.RIGHT, shelf.getSideChain());
        assertTrue(shelf.isPowered());
        assertFalse(shelf.isWaterlogged());

        final Block block = world.getBlockAt(location);
        assertEquals(shelf, block.getBlockData());
    }

}
