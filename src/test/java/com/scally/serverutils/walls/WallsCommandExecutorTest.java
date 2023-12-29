//package com.scally.serverutils.walls;
//
//import be.seeseemelk.mockbukkit.MockBukkit;
//import be.seeseemelk.mockbukkit.ServerMock;
//import be.seeseemelk.mockbukkit.WorldMock;
//import be.seeseemelk.mockbukkit.block.data.BlockDataMock;
//import be.seeseemelk.mockbukkit.block.data.TrapDoorMock;
//import com.scally.serverutils.distribution.Distribution;
//import com.scally.serverutils.trapdoors.TrapDoorsChange;
//import com.scally.serverutils.trapdoors.TrapDoorsCommandExecutor;
//import com.scally.serverutils.undo.UndoManager;
//import com.scally.serverutils.validation.Coordinates;
//import com.scally.serverutils.validation.ValidationResult;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.BlockFace;
//import org.bukkit.block.data.Bisected;
//import org.bukkit.block.data.BlockData;
//import org.bukkit.block.data.type.TrapDoor;
//import org.bukkit.block.data.type.Wall;
//import org.bukkit.command.Command;
//import org.bukkit.entity.Player;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class WallsCommandExecutorTest {
//
//    @Mock
//    private Command command;
//
//    @Mock
//    private Player player;
//
//    @Mock
//    private UndoManager undoManager;
//
//    private WorldMock world;
//
//    private ServerMock server;
//
//    private WallsCommandExecutor wallsCommandExecutor;
//
//    private Location location;
//
//    private int[] coords = {0, 0, 0, 5, 5, 5};
//
//    private Coordinates coordinates = new Coordinates(coords);
//
//    @BeforeEach
//    public void before() {
//        server = MockBukkit.mock();
//        world = server.addSimpleWorld("test");
//        location = new Location(world, 0.0, 0.0, 0.0);
//        wallsCommandExecutor = new WallsCommandExecutor(undoManager);
//    }
//
//    @AfterEach
//    public void after() {
//        MockBukkit.unmock();
//    }
//
//    @Test
//    @Disabled
//    public void changeAtLocation_happyPath() {
//
//        Material beforeMat = Material.STONE_BRICK_WALL;
//        Material afterMat = Material.COBBLESTONE_WALL;
//        Wall.Height beforeEastHeight = Wall.Height.LOW;
//        Wall.Height beforeWestHeight = Wall.Height.TALL;
//        Wall.Height beforeNorthHeight = Wall.Height.NONE;
//        Wall.Height beforeSouthHeight = Wall.Height.LOW;
//        boolean beforeUp = false;
//        boolean beforeWaterlogged = true;
//
//        location.getBlock().setType(beforeMat, false);
//        WallMock beforeWall = (WallMock) BlockDataMock.mock(beforeMat);
//        beforeWall.setHeight(BlockFace.EAST, beforeEastHeight);
//        beforeWall.setHeight(BlockFace.WEST, beforeWestHeight);
//        beforeWall.setHeight(BlockFace.NORTH, beforeNorthHeight);
//        beforeWall.setHeight(BlockFace.SOUTH, beforeSouthHeight);
//        beforeWall.setUp(beforeUp);
//        beforeWall.setWaterlogged(beforeWaterlogged);
//        world.setBlockData(0, 0, 0, beforeWall);
//
//        Set<Material> fromMaterial = Set.of(beforeMat);
//        Set<Material> toMaterial = Set.of(afterMat);
//        Distribution fromDistribution = new Distribution(fromMaterial);
//        Distribution toDistribution = new Distribution(toMaterial);
//        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);
//
//        WallsChange wallsChange = wallsCommandExecutor.changeAtLocation(location, validationResult);
//        assertNotNull(wallsChange);
//
//        BlockData afterBlockData = location.getBlock().getBlockData();
//        Wall afterWall = (Wall) afterBlockData;
//        Wall.Height afterEastHeight = afterWall.getHeight(BlockFace.EAST);
//        Wall.Height afterWestHeight = afterWall.getHeight(BlockFace.WEST);
//        Wall.Height afterNorthHeight = afterWall.getHeight(BlockFace.NORTH);
//        Wall.Height afterSouthHeight = afterWall.getHeight(BlockFace.SOUTH);
//        boolean afterUp = afterWall.isUp();
//        boolean afterWaterlogged = afterWall.isWaterlogged();
//        Material checkAfter = afterBlockData.getMaterial();
//
////        assertEquals(checkAfter, afterMat);
//        assertEquals(beforeEastHeight, afterEastHeight);
//        assertEquals(beforeWestHeight, afterWestHeight);
//        assertEquals(beforeNorthHeight, afterNorthHeight);
//        assertEquals(beforeSouthHeight, afterSouthHeight);
//        assertEquals(beforeUp, afterUp);
//        assertEquals(beforeWaterlogged, afterWaterlogged);
//
//    }
//
//    @Test
//    public void changeAtLocation_returnNull() {
//        Material beforeMat = Material.GRANITE_WALL;
//        Material afterMat = Material.ANDESITE_WALL;
//        Wall.Height beforeEastHeight = Wall.Height.TALL;
//        Wall.Height beforeWestHeight = Wall.Height.NONE;
//        Wall.Height beforeNorthHeight = Wall.Height.TALL;
//        Wall.Height beforeSouthHeight = Wall.Height.NONE;
//        boolean beforeUp = true;
//        boolean beforeWaterlogged = false;
//
//        location.getBlock().setType(beforeMat, false);
//        // Use different material (diorite instead of granite) to return null instead
//        WallMock beforeWall = (WallMock) BlockDataMock.mock(Material.DIORITE_WALL;
//        beforeWall.setHeight(BlockFace.EAST, beforeEastHeight);
//        beforeWall.setHeight(BlockFace.WEST, beforeWestHeight);
//        beforeWall.setHeight(BlockFace.NORTH, beforeNorthHeight);
//        beforeWall.setHeight(BlockFace.SOUTH, beforeSouthHeight);
//        beforeWall.setUp(beforeUp);
//        beforeWall.setWaterlogged(beforeWaterlogged);
//        world.setBlockData(0, 0, 0, beforeWall);
//
//        Set<Material> fromMaterial = Set.of(beforeMat);
//        Set<Material> toMaterial = Set.of(afterMat);
//        // fromMaterial is granite, not diorite
//        // fromDistribution.hasMeterial will be false since these are diff, null will be returned
//        Distribution fromDistribution = new Distribution(fromMaterial);
//        Distribution toDistribution = new Distribution(toMaterial);
//        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);
//
//        WallsChange wallsChange = wallsCommandExecutor.changeAtLocation(location, validationResult);
//        assertNull(wallsChange);
//
//    }
//
//}
