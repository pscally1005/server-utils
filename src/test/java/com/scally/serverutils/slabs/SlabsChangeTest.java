package com.scally.serverutils.slabs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.data.SlabMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SlabsChangeTest {

    private ServerMock serverMock;
    private World world;
    private Location location;
    private Slab blockData;
    private SlabsChange change;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 10, 20, 30);

        blockData = new SlabMock(Material.JUNGLE_SLAB);
        blockData.setType(Slab.Type.TOP);
        blockData.setWaterlogged(true);

        world.setBlockData(location, blockData);

        change = new SlabsChange(
                location,
                Material.BLACKSTONE_SLAB,
                Material.JUNGLE_SLAB,
                Slab.Type.TOP,
                true
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
        assertInstanceOf(Slab.class, blockDataAfterUndo);

        final Slab slab = (Slab) blockDataAfterUndo;
//        assertEquals(Material.BLACKSTONE_SLAB, slab.getMaterial());
        assertEquals(Slab.Type.TOP, slab.getType());
        assertTrue(slab.isWaterlogged());

        final Block block = world.getBlockAt(location);
        assertEquals(slab, block.getBlockData());
    }

}
