package com.scally.serverutils.slabs;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.data.BlockDataMock;
import org.mockbukkit.mockbukkit.block.data.SlabDataMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class SlabsCommandExecutorTest {

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private UndoManager undoManager;

    private WorldMock world;

    private ServerMock server;

    private SlabsCommandExecutor slabsCommandExecutor;

    private Location location;

    private int[] coords = {0, 0, 0, 5, 5, 5};

    private Coordinates coordinates = new Coordinates(coords);

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
        location = new Location(world, 0.0, 0.0, 0.0);
        slabsCommandExecutor = new SlabsCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void changeAtLocation_happyPath() {

        Material beforeMat = Material.OAK_SLAB;
        Material afterMat = Material.WARPED_SLAB;
        Slab.Type beforeType = Slab.Type.TOP;
        boolean beforeWaterlogged = true;

        location.getBlock().setType(beforeMat, false);
        SlabDataMock beforeSlab = (SlabDataMock) BlockDataMock.mock(beforeMat);
        beforeSlab.setType(beforeType);
        beforeSlab.setWaterlogged(beforeWaterlogged);
        world.setBlockData(0, 0, 0, beforeSlab);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        SlabsChange slabsChange = slabsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(slabsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Slab afterSlab = (Slab) afterBlockData;
        Slab.Type afterType = afterSlab.getType();
        boolean afterWaterlogged = afterSlab.isWaterlogged();
        Material checkAfter = afterBlockData.getMaterial();

        //assertEquals(checkAfter, afterMat);
        assertEquals(beforeType, afterType);
        assertEquals(beforeWaterlogged, afterWaterlogged);

    }

    @Test
    public void changeAtLocation_returnNull() {
        Material beforeMat = Material.COBBLESTONE_SLAB;
        Material afterMat = Material.ANDESITE_SLAB;
        Slab.Type beforeType = Slab.Type.TOP;
        boolean beforeWaterlogged = true;

        location.getBlock().setType(beforeMat, false);
        // Use different material (stone brick instead of cobblestone) to return null instead
        SlabDataMock beforeSlab = (SlabDataMock) BlockDataMock.mock(Material.STONE_BRICK_SLAB);
        beforeSlab.setWaterlogged(beforeWaterlogged);
        beforeSlab.setType(beforeType);
        world.setBlockData(0, 0, 0, beforeSlab);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        // fromMaterial is stone brick, not cobblestone
        // fromDistribution.hasMeterial will be false since these are diff, null will be returned
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        SlabsChange slabsChange = slabsCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(slabsChange);

    }

}
