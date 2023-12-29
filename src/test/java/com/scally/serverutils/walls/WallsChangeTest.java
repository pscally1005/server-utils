//package com.scally.serverutils.walls;
//
//import be.seeseemelk.mockbukkit.MockBukkit;
//import be.seeseemelk.mockbukkit.ServerMock;
//import be.seeseemelk.mockbukkit.block.data.WallMock;
//import com.scally.serverutils.walls.WallsChange;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.World;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.block.data.BlockData;
//import org.bukkit.block.data.type.Wall;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class WallsChangeTest {
//
//    private ServerMock serverMock;
//    private World world;
//    private Location location;
//    private Wall blockData;
//    private WallsChange change;
//
//    @BeforeEach
//    public void before() {
//        serverMock = MockBukkit.mock();
//        world = serverMock.addSimpleWorld("test");
//        location = new Location(world, 10, 20, 30);
//
//        blockData = new WallMock(Material.COBBLESTONE_WALL);
//        blockData.setHeight(BlockFace.EAST, Wall.Height.LOW);
//        blockData.setHeight(BlockFace.WEST, Wall.Height.TALL);
//        blockData.setHeight(BlockFace.NORTH, Wall.Height.NONE);
//        blockData.setHeight(BlockFace.SOUTH, Wall.Height.LOW);
//        blockData.setUp(false);
//        blockData.setWaterlogged(true);
//
//        world.setBlockData(location, blockData);
//
//        change = new WallsChange(
//                location,
//                Material.STONE_BRICK_WALL,
//                Material.COBBLESTONE_WALL,
//                Wall.Height.LOW,
//                Wall.Height.TALL,
//                Wall.Height.NONE,
//                Wall.Height.LOW,
//                false,
//                true
//        );
//    }
//
//    @AfterEach
//    public void after() {
//        MockBukkit.unmock();
//    }
//
//    @Test
//    @Disabled
//    public void undo_happyPath() {
//        final boolean result = change.undo();
//        assertTrue(result);
//
//        final BlockData blockDataAfterUndo = world.getBlockData(location);
//        assertInstanceOf(Wall.class, blockDataAfterUndo);
//
//        final Wall wall = (Wall) blockDataAfterUndo;
//        assertEquals(Material.STONE_BRICK_WALL, wall.getMaterial());
//        assertEquals(Wall.Height.LOW, wall.getHeight(BlockFace.EAST));
//        assertEquals(Wall.Height.TALL, wall.getHeight(BlockFace.WEST));
//        assertEquals(Wall.Height.NONE, wall.getHeight(BlockFace.NORTH));
//        assertEquals(Wall.Height.LOW, wall.getHeight(BlockFace.SOUTH));
//        assertFalse(wall.isUp());
//        assertTrue(wall.isWaterlogged());
//
//        final Block block = world.getBlockAt(location);
//        assertEquals(wall, block.getBlockData());
//    }
//
//}
