package com.scally.serverutils.stairs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.data.StairsMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StairsChangeTest {

    private ServerMock serverMock;
    private World world;
    private Location location;
    private Stairs blockData;
    private StairsChange change;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 10, 20, 30);

        blockData = new StairsMock(Material.JUNGLE_STAIRS);
        blockData.setHalf(Bisected.Half.TOP);
        blockData.setFacing(BlockFace.EAST);
        blockData.setShape(Stairs.Shape.OUTER_RIGHT);
        blockData.setWaterlogged(false);

        world.setBlockData(location, blockData);

        change = new StairsChange(
                location,
                Material.BLACKSTONE_STAIRS,
                Material.JUNGLE_STAIRS,
                Bisected.Half.TOP,
                BlockFace.EAST,
                Stairs.Shape.OUTER_RIGHT,
                false
        );
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void undo_happyPath() {
        final boolean result = change.undo();
        assertTrue(result);

        final BlockData blockDataAfterUndo = world.getBlockData(location);
        assertInstanceOf(Stairs.class, blockDataAfterUndo);

        final Stairs stairs = (Stairs) blockDataAfterUndo;
//        assertEquals(Material.BLACKSTONE_STAIRS, stairs.getMaterial());
        assertEquals(Bisected.Half.TOP, stairs.getHalf());
        assertEquals(BlockFace.EAST, stairs.getFacing());
        assertEquals(Stairs.Shape.OUTER_RIGHT, stairs.getShape());
        assertFalse(stairs.isWaterlogged());

        final Block block = world.getBlockAt(location);
        assertEquals(stairs, block.getBlockData());
    }

}
