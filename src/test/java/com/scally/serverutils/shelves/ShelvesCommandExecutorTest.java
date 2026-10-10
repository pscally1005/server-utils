package com.scally.serverutils.shelves;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;
import org.bukkit.block.data.type.Shelf;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ShelvesCommandExecutorTest {

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private UndoManager undoManager;

    private WorldMock world;

    private ServerMock server;

    private ShelvesCommandExecutor shelvesCommandExecutor;

    private Location location;

    private int[] coords = {0, 0, 0, 5, 5, 5};

    private Coordinates coordinates = new Coordinates(coords);

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
        location = new Location(world, 0.0, 0.0, 0.0);
        shelvesCommandExecutor = new ShelvesCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    public void changeAtLocation_happyPath() {

        Material beforeMat = Material.OAK_SHELF;
        Material afterMat = Material.WARPED_SHELF;
        BlockFace beforeFacing = BlockFace.EAST;
        SideChaining.ChainPart beforeSideChain = SideChaining.ChainPart.LEFT;
        boolean beforePowered = true;
        boolean beforeWaterlogged = true;

        location.getBlock().setType(beforeMat, false);
        BlockData beforeData = location.getBlock().getBlockData();
        Shelf beforeShelf = (Shelf) beforeData;
        beforeShelf.setFacing(beforeFacing);
        beforeShelf.setSideChain(beforeSideChain);
        beforeShelf.setPowered(beforePowered);
        beforeShelf.setWaterlogged(beforeWaterlogged);
        world.setBlockData(0, 0, 0, beforeShelf);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        ShelvesChange shelvesChange = shelvesCommandExecutor.changeAtLocation(location, validationResult);
        assertNotNull(shelvesChange);

        BlockData afterBlockData = location.getBlock().getBlockData();
        Shelf afterShelf = (Shelf) afterBlockData;
        assertEquals(beforeFacing, afterShelf.getFacing());
        assertEquals(beforeSideChain, afterShelf.getSideChain());
        assertEquals(beforePowered, afterShelf.isPowered());
        assertEquals(beforeWaterlogged, afterShelf.isWaterlogged());

    }

    @Test
    public void changeAtLocation_returnNull() {
        Material beforeMat = Material.OAK_SHELF;
        Material afterMat = Material.SPRUCE_SHELF;

        location.getBlock().setType(Material.BIRCH_SHELF, false);

        Set<Material> fromMaterial = Set.of(beforeMat);
        Set<Material> toMaterial = Set.of(afterMat);
        Distribution fromDistribution = new Distribution(fromMaterial);
        Distribution toDistribution = new Distribution(toMaterial);
        ValidationResult validationResult = new ValidationResult(true, coordinates, fromDistribution, toDistribution);

        ShelvesChange shelvesChange = shelvesCommandExecutor.changeAtLocation(location, validationResult);
        assertNull(shelvesChange);

    }

}
