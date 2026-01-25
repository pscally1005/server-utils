package com.scally.serverutils.trapdoors;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.data.BlockDataMock;
import org.mockbukkit.mockbukkit.block.data.TrapDoorDataMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrapDoorsCommandExecutorTest {

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private UndoManager undoManager;

    private WorldMock world;

    private ServerMock server;

    private TrapDoorsCommandExecutor trapDoorsCommandExecutor;

    private Location location;

    private int[] coords = {0, 0, 0, 5, 5, 5};

    private Coordinates coordinates = new Coordinates(coords);

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
        location = new Location(world, 0.0, 0.0, 0.0);
        trapDoorsCommandExecutor = new TrapDoorsCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void changeAtLocation_happyPath() {

        Material beforeMat = Material.OAK_TRAPDOOR;
        Material afterMat = Material.WARPED_TRAPDOOR;
        Bisected.Half beforeHalf = Bisected.Half.TOP;
        BlockFace beforeFacing = BlockFace.WEST;
        boolean beforeOpen = false;
        boolean beforePowered = true;
        boolean beforeWaterlogged = true;

        location.getBlock().setType(beforeMat, false);
        TrapDoorDataMock beforeTrapDoor = (TrapDoorDataMock) BlockDataMock.mock(beforeMat);
        beforeTrapDoor.setHalf(beforeHalf);
        beforeTrapDoor.setFacing(beforeFacing);
        beforeTrapDoor.setOpen(beforeOpen);
        beforeTrapDoor.setPowered(beforePowered);
        beforeTrapDoor.setWaterlogged(beforeWaterlogged);
        world.setBlockData(0, 0, 0, beforeTrapDoor);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        TrapDoorsChange trapDoorsChange = trapDoorsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(trapDoorsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        TrapDoor afterTrapDoor = (TrapDoor) afterBlockData;
        Bisected.Half afterHalf = afterTrapDoor.getHalf();
        BlockFace afterFacing = afterTrapDoor.getFacing();
        boolean afterOpen = afterTrapDoor.isOpen();
        boolean afterPowered = afterTrapDoor.isPowered();
        boolean afterWaterlogged = afterTrapDoor.isWaterlogged();
        Material checkAfter = afterBlockData.getMaterial();

//        assertEquals(checkAfter, afterMat);
        assertEquals(beforeHalf, afterHalf);
        assertEquals(beforeFacing, afterFacing);
        assertEquals(beforeOpen, afterOpen);
        assertEquals(beforePowered, afterPowered);
        assertEquals(beforeWaterlogged, afterWaterlogged);

    }

    @Test
    public void changeAtLocation_returnNull() {
        Material beforeMat = Material.MANGROVE_TRAPDOOR;
        Material afterMat = Material.CRIMSON_TRAPDOOR;
        Bisected.Half beforeHalf = Bisected.Half.TOP;
        BlockFace beforeFacing = BlockFace.NORTH;
        boolean beforeOpen = true;
        boolean beforePowered = false;
        boolean beforeWaterlogged = false;

        location.getBlock().setType(beforeMat, false);
        // Use different material (birch instead of mangrove) to return null instead
        TrapDoorDataMock beforeTrapDoor = (TrapDoorDataMock) BlockDataMock.mock(Material.BIRCH_TRAPDOOR);
        beforeTrapDoor.setHalf(beforeHalf);
        beforeTrapDoor.setFacing(beforeFacing);
        beforeTrapDoor.setOpen(beforeOpen);
        beforeTrapDoor.setPowered(beforePowered);
        beforeTrapDoor.setWaterlogged(beforeWaterlogged);
        world.setBlockData(0, 0, 0, beforeTrapDoor);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        // fromMaterial is birch, not mangrove
        // fromDistribution.hasMeterial will be false since these are diff, null will be returned
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        TrapDoorsChange trapDoorsChange = trapDoorsCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(trapDoorsChange);

    }

}
