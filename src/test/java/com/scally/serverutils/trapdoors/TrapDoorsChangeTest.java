package com.scally.serverutils.trapdoors;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.data.TrapDoorMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrapDoorsChangeTest {

    private ServerMock serverMock;
    private World world;
    private Location location;
    private TrapDoor blockData;
    private TrapDoorsChange change;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 10, 20, 30);

        blockData = new TrapDoorMock(Material.BIRCH_TRAPDOOR);
        blockData.setHalf(Bisected.Half.BOTTOM);
        blockData.setFacing(BlockFace.WEST);
        blockData.setOpen(true);
        blockData.setPowered(true);
        blockData.setWaterlogged(false);

        world.setBlockData(location, blockData);

        change = new TrapDoorsChange(
                location,
                Material.ACACIA_TRAPDOOR,
                Material.BIRCH_TRAPDOOR,
                Bisected.Half.BOTTOM,
                BlockFace.WEST,
                true,
                true,
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
        assertInstanceOf(TrapDoor.class, blockDataAfterUndo);

        final TrapDoor trapDoor = (TrapDoor) blockDataAfterUndo;
        assertEquals(Material.ACACIA_TRAPDOOR, trapDoor.getMaterial());
        assertEquals(Bisected.Half.BOTTOM, trapDoor.getHalf());
        assertEquals(BlockFace.WEST, trapDoor.getFacing());
        assertTrue(trapDoor.isOpen());
        assertTrue(trapDoor.isPowered());
        assertFalse(trapDoor.isWaterlogged());

        final Block block = world.getBlockAt(location);
        assertEquals(trapDoor, block.getBlockData());
    }

}
