package com.scally.serverutils.stairs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.data.BlockDataMock;
import be.seeseemelk.mockbukkit.block.data.SlabMock;
import be.seeseemelk.mockbukkit.block.data.StairsMock;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.slabs.SlabsChange;
import com.scally.serverutils.slabs.SlabsCommandExecutor;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StairsCommandExecutorTest {

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private UndoManager undoManager;

    private WorldMock world;

    private ServerMock server;

    private StairsCommandExecutor stairsCommandExecutor;

    private Location location;

    private int[] coords = {0, 0, 0, 5, 5, 5};

    private Coordinates coordinates = new Coordinates(coords);

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
        location = new Location(world, 0.0, 0.0, 0.0);
        stairsCommandExecutor = new StairsCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void changeAtLocation_happyPath() {

        Material beforeMat = Material.OAK_STAIRS;
        Material afterMat = Material.WARPED_STAIRS;
        Bisected.Half beforeHalf = Bisected.Half.TOP;
        BlockFace beforeFacing = BlockFace.EAST;
        Stairs.Shape beforeShape = Stairs.Shape.OUTER_LEFT;
        boolean beforeWaterlogged = true;

        location.getBlock().setType(beforeMat, false);
        StairsMock beforeStair = (StairsMock) BlockDataMock.mock(beforeMat);
        beforeStair.setHalf(beforeHalf);
        beforeStair.setFacing(beforeFacing);
        beforeStair.setShape(beforeShape);
        beforeStair.setWaterlogged(beforeWaterlogged);
        world.setBlockData(0, 0, 0, beforeStair);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        StairsChange stairsChange = stairsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(stairsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Stairs afterStair = (Stairs) afterBlockData;
        Bisected.Half afterHalf = afterStair.getHalf();
        BlockFace afterFacing = afterStair.getFacing();
        Stairs.Shape afterShape = afterStair.getShape();
        boolean afterWaterlogged = afterStair.isWaterlogged();
        Material checkAfter = afterBlockData.getMaterial();

        //assertEquals(checkAfter, afterMat);
        assertEquals(beforeHalf, afterHalf);
        assertEquals(beforeFacing, afterFacing);
        assertEquals(beforeShape, afterShape);
        assertEquals(beforeWaterlogged, afterWaterlogged);

    }

    @Test
    public void changeAtLocation_returnNull() {
        Material beforeMat = Material.COBBLESTONE_STAIRS;
        Material afterMat = Material.ANDESITE_STAIRS;
        Bisected.Half beforeHalf = Bisected.Half.BOTTOM;
        BlockFace beforeFacing = BlockFace.SOUTH;
        Stairs.Shape beforeShape = Stairs.Shape.INNER_RIGHT;
        boolean beforeWaterlogged = true;

        location.getBlock().setType(beforeMat, false);
        // Use different material (stone brick instead of cobblestone) to return null instead
        StairsMock beforeStair = (StairsMock) BlockDataMock.mock(Material.STONE_BRICK_STAIRS);
        beforeStair.setHalf(beforeHalf);
        beforeStair.setFacing(beforeFacing);
        beforeStair.setShape(beforeShape);
        beforeStair.setWaterlogged(beforeWaterlogged);
        world.setBlockData(0, 0, 0, beforeStair);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        // fromMaterial is stone brick, not cobblestone
        // fromDistribution.hasMeterial will be false since these are diff, null will be returned
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        StairsChange stairsChange = stairsCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(stairsChange);

    }

}
