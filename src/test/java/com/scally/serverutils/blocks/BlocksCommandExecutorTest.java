package com.scally.serverutils.blocks;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.data.BlockDataMock;
import be.seeseemelk.mockbukkit.block.data.StairsMock;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
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

public class BlocksCommandExecutorTest {

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private UndoManager undoManager;

    private WorldMock world;

    private ServerMock server;

    private BlocksCommandExecutor blocksCommandExecutor;

    private Location location;

    private int[] coords = {0, 0, 0, 5, 5, 5};

    private Coordinates coordinates = new Coordinates(coords);

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
        location = new Location(world, 0.0, 0.0, 0.0);
        blocksCommandExecutor = new BlocksCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void changeAtLocation_happyPath() {

        Material beforeMat = Material.OAK_PLANKS;
        Material afterMat = Material.WARPED_PLANKS;

        location.getBlock().setType(beforeMat, false);
        BlockDataMock beforeBlock = (BlockDataMock) BlockDataMock.mock(beforeMat);
        world.setBlockData(0, 0, 0, beforeBlock);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        BlocksChange blocksChange = blocksCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(blocksChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        BlockData afterBlock = (BlockData) afterBlockData;
        Material checkAfter = afterBlockData.getMaterial();
    }

    @Test
    public void changeAtLocation_returnNull() {
        Material beforeMat = Material.COBBLESTONE;
        Material afterMat = Material.ANDESITE;

        location.getBlock().setType(beforeMat, false);
        // Use different material (stone brick instead of cobblestone) to return null instead
        StairsMock beforeBlock = (StairsMock) BlockDataMock.mock(Material.STONE_BRICKS);
        world.setBlockData(0, 0, 0, beforeBlock);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        // fromMaterial is stone brick, not cobblestone
        // fromDistribution.hasMeterial will be false since these are diff, null will be returned
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        BlocksChange blocksChange = blocksCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(blocksChange);

    }

}