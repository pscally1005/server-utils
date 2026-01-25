package com.scally.serverutils.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.data.BlockDataMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlocksChangeTest {

    private ServerMock serverMock;
    private World world;
    private Location location;
    private BlockData blockData;
    private BlocksChange change;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 10, 20, 30);

        blockData = new BlockDataMock(Material.ICE);

        world.setBlockData(location, blockData);

        change = new BlocksChange(
                location,
                Material.COBBLED_DEEPSLATE,
                Material.ICE
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
        assertInstanceOf(BlockData.class, blockDataAfterUndo);

        final BlockData bd = blockDataAfterUndo;

        final Block block = world.getBlockAt(location);
        assertEquals(bd, block.getBlockData());
    }

}