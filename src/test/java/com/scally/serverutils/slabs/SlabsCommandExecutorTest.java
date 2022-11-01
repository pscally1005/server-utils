package com.scally.serverutils.slabs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.data.BlockDataMock;
import be.seeseemelk.mockbukkit.block.data.SlabMock;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.distribution.DistributionMaterial;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void changeAtLocation_happyPath() {

        location.getBlock().setType(Material.OAK_SLAB, false);
        BlockData beforeBlockData = location.getBlock().getBlockData();
        SlabMock beforeSlab = (SlabMock) BlockDataMock.mock(Material.OAK_SLAB);
        beforeSlab.setWaterlogged(true);
        beforeSlab.setType(Slab.Type.TOP);
        world.setBlockData(0, 0, 0, beforeSlab);
        Slab.Type beforeType = beforeSlab.getType();
        boolean beforeWaterlogged = beforeSlab.isWaterlogged();

        Set<Material> fromMaterial = Set.of(Material.OAK_SLAB, Material.POLISHED_ANDESITE_SLAB);
        Set<Material> toMaterial = Set.of(Material.WARPED_SLAB);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        SlabsChange slabsChange = slabsCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(slabsChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Material afterMaterial = slabsChange.afterMaterial();
        Slab afterSlab = (Slab) afterBlockData;
        Slab.Type afterType = afterSlab.getType();
        boolean afterWaterlogged = afterSlab.isWaterlogged();;

        assertEquals(Material.WARPED_SLAB, afterMaterial);
        assertEquals(beforeType, afterType);
        assertEquals(beforeWaterlogged, afterWaterlogged);

    }

    /*@Test
    public void changeAtLocation_notASlab() {

    }*/

    // TODO: onCommand tests

}
