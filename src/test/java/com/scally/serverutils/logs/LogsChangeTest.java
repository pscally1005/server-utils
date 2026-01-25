package com.scally.serverutils.logs;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.data.BlockDataMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogsChangeTest {

    private ServerMock serverMock;
    private World world;
    private Location location;
    private Orientable blockData;
    private LogsChange change;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 10, 20, 30);

        blockData = (Orientable) BlockDataMock.mock(Material.JUNGLE_LOG);
        blockData.setAxis(Axis.Y);

        world.setBlockData(location, (BlockData) blockData);
        
        change = new LogsChange(
                location,
                Material.BIRCH_LOG,
                Material.JUNGLE_LOG,
                Axis.Y
        );
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    public void undo_happyPath() {
        final boolean result = change.undo();
        assertTrue(result);

        final BlockData blockDataAfterUndo = world.getBlockData(location);
        assertInstanceOf(Orientable.class, blockDataAfterUndo);

        final Orientable orientable = (Orientable) blockDataAfterUndo;
        assertEquals(Material.BIRCH_LOG, orientable.getMaterial());
        assertEquals(Axis.Y, orientable.getAxis());

        final Block block = world.getBlockAt(location);
        assertEquals(orientable, block.getBlockData());
    }

}
