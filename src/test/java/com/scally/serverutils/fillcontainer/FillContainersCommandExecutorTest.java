package com.scally.serverutils.fillcontainer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.BlockMock;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class, MockBukkitExtension.class})
class FillContainersCommandExecutorTest {

    private FillContainersCommandExecutor fillContainersCommandExecutor;

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private World world;

    @Mock
    private Block block1;

    @Mock
    private Block block2;

    @Mock
    private Block block3;

    @Mock
    private Container container1;

    @Mock
    private Container container2;

    @Mock
    private Inventory inventory1;

    @Mock
    private Inventory inventory2;

    @Mock
    private RayTraceResult rayTraceResult;

    @MockBukkitInject
    private ServerMock mockServer;

    @BeforeEach
    void before() {
        fillContainersCommandExecutor = new FillContainersCommandExecutor();
    }

    @Test
    void onCommand_happyPath() {
        final String[] args = new String[]{"chest", "0", "0", "0", "1", "0", "0", "50%cobblestone,50%oak_planks"};

        Mockito.when(player.getWorld()).thenReturn(world);
        
        // First chest at (0, 0, 0)
        Mockito.when(world.getBlockAt(0, 0, 0)).thenReturn(block1);
        Mockito.when(block1.getType()).thenReturn(Material.CHEST);
        Mockito.when(block1.getState()).thenReturn(container1);
        Mockito.when(container1.getInventory()).thenReturn(inventory1);
        final ItemStack[] items1 = new ItemStack[20];
        Mockito.when(inventory1.getContents()).thenReturn(items1);
        
        // Second chest at (1, 0, 0)
        Mockito.when(world.getBlockAt(1, 0, 0)).thenReturn(block2);
        Mockito.when(block2.getType()).thenReturn(Material.CHEST);
        Mockito.when(block2.getState()).thenReturn(container2);
        Mockito.when(container2.getInventory()).thenReturn(inventory2);
        final ItemStack[] items2 = new ItemStack[20];
        Mockito.when(inventory2.getContents()).thenReturn(items2);

        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertTrue(result);

        for (ItemStack item : items1) {
            assertNotNull(item);
        }
        for (ItemStack item : items2) {
            assertNotNull(item);
        }
    }

    @Test
    void onCommand_happyPathWithShelf() {
        final String[] args = new String[]{"oak_shelf", "0", "0", "0", "0", "0", "0", "cobblestone"};

        Mockito.when(player.getWorld()).thenReturn(world);
        Mockito.when(world.getBlockAt(0, 0, 0)).thenReturn(block1);
        Mockito.when(block1.getType()).thenReturn(Material.OAK_SHELF);
        Mockito.when(block1.getState()).thenReturn(container1);
        Mockito.when(container1.getInventory()).thenReturn(inventory1);
        final ItemStack[] items = new ItemStack[6]; // Shelves have 6 slots
        Mockito.when(inventory1.getContents()).thenReturn(items);

        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertTrue(result);
    }

    @Test
    void onCommand_invalidNumberOfArgs() {
        final String[] args = new String[]{"chest", "0", "0", "0", "1", "0", "0"};
        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidSender() {
        final String[] args = new String[]{"chest", "0", "0", "0", "1", "0", "0", "cobblestone"};
        final BlockCommandSender blockSender = Mockito.mock(BlockCommandSender.class);

        final boolean result = fillContainersCommandExecutor.onCommand(blockSender, command, "fill-containers", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidBlockType() {
        final String[] args = new String[]{"invalid_block", "0", "0", "0", "1", "0", "0", "cobblestone"};
        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertFalse(result);
    }

    @Test
    void onCommand_notAllowedBlockType() {
        final String[] args = new String[]{"furnace", "0", "0", "0", "1", "0", "0", "cobblestone"};
        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidCoords() {
        final String[] args = new String[]{"chest", "test", "0", "0", "1", "0", "0", "cobblestone"};
        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidDistribution() {
        final String[] args = new String[]{"chest", "0", "0", "0", "1", "0", "0", "sponge_slab"};
        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertFalse(result);
    }

    @Test
    void onCommand_skipsNonMatchingBlocks() {
        final String[] args = new String[]{"chest", "0", "0", "0", "2", "0", "0", "cobblestone"};

        Mockito.when(player.getWorld()).thenReturn(world);
        
        // Chest at (0, 0, 0) - matches
        Mockito.when(world.getBlockAt(0, 0, 0)).thenReturn(block1);
        Mockito.when(block1.getType()).thenReturn(Material.CHEST);
        Mockito.when(block1.getState()).thenReturn(container1);
        Mockito.when(container1.getInventory()).thenReturn(inventory1);
        final ItemStack[] items1 = new ItemStack[20];
        Mockito.when(inventory1.getContents()).thenReturn(items1);
        
        // Barrel at (1, 0, 0) - doesn't match (we're looking for chests)
        Mockito.when(world.getBlockAt(1, 0, 0)).thenReturn(block2);
        Mockito.when(block2.getType()).thenReturn(Material.BARREL);
        
        // Chest at (2, 0, 0) - matches
        Mockito.when(world.getBlockAt(2, 0, 0)).thenReturn(block3);
        Mockito.when(block3.getType()).thenReturn(Material.CHEST);
        Mockito.when(block3.getState()).thenReturn(container2);
        Mockito.when(container2.getInventory()).thenReturn(inventory2);
        final ItemStack[] items2 = new ItemStack[20];
        Mockito.when(inventory2.getContents()).thenReturn(items2);

        final boolean result = fillContainersCommandExecutor.onCommand(player, command, "fill-containers", args);
        assertTrue(result);
    }

    @Test
    void onTabComplete_notPlayer() {
        final CommandSender sender = Mockito.mock(Zombie.class);
        assertEquals(List.of(), fillContainersCommandExecutor.onTabComplete(sender, command, "test", new String[]{}));
    }

    @Test
    void onTabComplete_tooManyArgs() {
        assertEquals(List.of(), fillContainersCommandExecutor.onTabComplete(player, command, "test",
                new String[]{"chest", "0", "0", "0", "1", "0", "0", "cobblestone"}));
    }

    @Test
    void onTabComplete_suggestsBlockTypes() {
        final List<String> result = fillContainersCommandExecutor.onTabComplete(player, command, "test", new String[]{"ch"});
        assertTrue(result.contains("chest"));
        assertTrue(result.contains("cherry_shelf"));
    }

    @Test
    void onTabComplete_noRayTraceFound() {
        final String[] args = new String[]{"chest", "1", "2"};
        assertEquals(List.of(), fillContainersCommandExecutor.onTabComplete(player, command, "test", args));
    }

    @Test
    void onTabComplete_noHitBlock() {
        Mockito.when(player.rayTraceBlocks(Mockito.anyDouble())).thenReturn(rayTraceResult);
        final String[] args = new String[]{"chest", "1", "2"};
        assertEquals(List.of(), fillContainersCommandExecutor.onTabComplete(player, command, "test", args));
    }

    @Test
    void onTabComplete_happyPath() {
        Mockito.when(player.rayTraceBlocks(Mockito.anyDouble())).thenReturn(rayTraceResult);
        Mockito.when(rayTraceResult.getHitBlock()).thenReturn(new BlockMock(new Location(world, 1, 2, 3)));
        final String[] args = new String[]{"chest", "1", "2"};
        assertEquals(List.of("2", "2 3"), fillContainersCommandExecutor.onTabComplete(player, command, "test", args));
    }
}
