package com.scally.serverutils.logs;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.data.BlockDataMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class LogsCommandExecutorTest {

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private UndoManager undoManager;

    private WorldMock world;

    private ServerMock server;

    private LogsCommandExecutor logsCommandExecutor;

    private Location location;

    private int[] coords = {0, 0, 0, 5, 5, 5};

    private Coordinates coordinates = new Coordinates(coords);

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
        location = new Location(world, 0.0, 0.0, 0.0);
        logsCommandExecutor = new LogsCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    public void changeAtLocation_happyPath() {

        Material beforeMat = Material.OAK_LOG;
        Material afterMat = Material.SPRUCE_LOG;
        Axis beforeAxis = Axis.X;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(logsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Orientable afterLog = (Orientable) afterBlockData;
        Axis afterAxis = afterLog.getAxis();
        Material checkAfter = afterBlockData.getMaterial();

        assertEquals(checkAfter, afterMat);
        assertEquals(beforeAxis, afterAxis);

    }

    @Test
    public void changeAtLocation_returnNull() {
        Material beforeMat = Material.BIRCH_LOG;
        Material afterMat = Material.DARK_OAK_LOG;
        Axis beforeAxis = Axis.Z;

        location.getBlock().setType(beforeMat, false);
        // Use different material (jungle instead of birch) to return null instead
        Orientable beforeLog = (Orientable) BlockDataMock.mock(Material.JUNGLE_LOG);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        // fromMaterial is jungle, not birch
        // fromDistribution.hasMaterial will be false since these are diff, null will be returned
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(logsChange);

    }

    @Test
    public void changeAtLocation_strippedLog() {

        Material beforeMat = Material.STRIPPED_OAK_LOG;
        Material afterMat = Material.STRIPPED_BIRCH_LOG;
        Axis beforeAxis = Axis.Y;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(logsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Orientable afterLog = (Orientable) afterBlockData;
        Axis afterAxis = afterLog.getAxis();
        Material checkAfter = afterBlockData.getMaterial();

        assertEquals(checkAfter, afterMat);
        assertEquals(beforeAxis, afterAxis);

    }

    @Test
    public void changeAtLocation_oakLogDoesNotMatchOakWood() {
        // Test that specifying OAK_LOG in from distribution does NOT match OAK_WOOD
        Material beforeMat = Material.OAK_WOOD;
        Material fromDistributionMat = Material.OAK_LOG; // Only matches OAK_LOG exactly
        Material toDistributionMat = Material.BIRCH_LOG;
        Axis beforeAxis = Axis.X;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(fromDistributionMat);
        Set<Material> toMaterial = Set.of(toDistributionMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        // Should return null because OAK_LOG does not match OAK_WOOD
        assertNull(logsChange);
    }

    @Test
    public void changeAtLocation_strippedOakLogDoesNotMatchStrippedOakWood() {
        // Test that specifying STRIPPED_OAK_LOG does NOT match STRIPPED_OAK_WOOD
        Material beforeMat = Material.STRIPPED_OAK_WOOD;
        Material fromDistributionMat = Material.STRIPPED_OAK_LOG;
        Material toDistributionMat = Material.STRIPPED_BIRCH_LOG;
        Axis beforeAxis = Axis.Z;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(fromDistributionMat);
        Set<Material> toMaterial = Set.of(toDistributionMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        // Should return null because STRIPPED_OAK_LOG does not match STRIPPED_OAK_WOOD
        assertNull(logsChange);
    }

    @Test
    public void changeAtLocation_differentWoodTypeReturnsNull() {
        // Test that JUNGLE_LOG doesn't match when from distribution has OAK_LOG
        Material beforeMat = Material.JUNGLE_LOG;
        Material fromDistributionMat = Material.OAK_LOG;
        Material toDistributionMat = Material.BIRCH_LOG;
        Axis beforeAxis = Axis.Y;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(fromDistributionMat);
        Set<Material> toMaterial = Set.of(toDistributionMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(logsChange);
    }

    @Test
    public void changeAtLocation_oakLogToBirchLog() {
        // Test that OAK_LOG -> BIRCH_LOG works correctly
        Material beforeMat = Material.OAK_LOG;
        Material fromDistributionMat = Material.OAK_LOG;
        Material toDistributionMat = Material.BIRCH_LOG;
        Axis beforeAxis = Axis.X;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(fromDistributionMat);
        Set<Material> toMaterial = Set.of(toDistributionMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(logsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Orientable afterLog = (Orientable) afterBlockData;
        Axis afterAxis = afterLog.getAxis();
        Material checkAfter = afterBlockData.getMaterial();

        // Should change to exactly BIRCH_LOG
        assertEquals(Material.BIRCH_LOG, checkAfter);
        assertEquals(beforeAxis, afterAxis);
    }

    @Test
    public void changeAtLocation_oakLogToStrippedCherryWood() {
        // Test that OAK_LOG -> STRIPPED_CHERRY_WOOD replaces with exact material
        Material beforeMat = Material.OAK_LOG;
        Material fromDistributionMat = Material.OAK_LOG;
        Material toDistributionMat = Material.STRIPPED_CHERRY_WOOD;
        Axis beforeAxis = Axis.Y;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(fromDistributionMat);
        Set<Material> toMaterial = Set.of(toDistributionMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(logsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Orientable afterLog = (Orientable) afterBlockData;
        Axis afterAxis = afterLog.getAxis();
        Material checkAfter = afterBlockData.getMaterial();

        // Should change to exactly STRIPPED_CHERRY_WOOD (not OAK_WOOD or any other variant)
        assertEquals(Material.STRIPPED_CHERRY_WOOD, checkAfter);
        assertEquals(beforeAxis, afterAxis);
    }

    @Test
    public void changeAtLocation_strippedOakLogToBirchLog() {
        // Test that STRIPPED_OAK_LOG -> BIRCH_LOG replaces with exact material (removes stripped)
        Material beforeMat = Material.STRIPPED_OAK_LOG;
        Material fromDistributionMat = Material.STRIPPED_OAK_LOG;
        Material toDistributionMat = Material.BIRCH_LOG;
        Axis beforeAxis = Axis.Z;

        location.getBlock().setType(beforeMat, false);
        Orientable beforeLog = (Orientable) BlockDataMock.mock(beforeMat);
        beforeLog.setAxis(beforeAxis);
        world.setBlockData(0, 0, 0, (BlockData) beforeLog);

        Set<Material> fromMaterial = Set.of(fromDistributionMat);
        Set<Material> toMaterial = Set.of(toDistributionMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        LogsChange logsChange = logsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(logsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Orientable afterLog = (Orientable) afterBlockData;
        Axis afterAxis = afterLog.getAxis();
        Material checkAfter = afterBlockData.getMaterial();

        // Should change to exactly BIRCH_LOG (not STRIPPED_BIRCH_LOG)
        assertEquals(Material.BIRCH_LOG, checkAfter);
        assertEquals(beforeAxis, afterAxis);
    }

}
