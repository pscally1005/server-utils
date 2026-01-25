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
class FillContainerCommandExecutorTest {

    private FillContainerCommandExecutor fillContainerCommandExecutor;

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private World world;

    @Mock
    private Block block;

    @Mock
    private Container container;

    @Mock
    private Inventory inventory;

    @Mock
    private RayTraceResult rayTraceResult;

    @MockBukkitInject
    private ServerMock mockServer;

    @BeforeEach
    void before() {
        fillContainerCommandExecutor = new FillContainerCommandExecutor();
    }

    @Test
    void onCommand_happyPath() {
        final String[] args = new String[]{"0", "0", "0", "50%cobblestone,50%oak_planks"};

        Mockito.when(player.getWorld()).thenReturn(world);
        Mockito.when(world.getBlockAt(0, 0, 0)).thenReturn(block);
        Mockito.when(block.getType()).thenReturn(Material.CHEST);
        Mockito.when(block.getState()).thenReturn(container);
        Mockito.when(container.getInventory()).thenReturn(inventory);

        final ItemStack[] items = new ItemStack[20];
        Mockito.when(inventory.getContents()).thenReturn(items);

        final boolean result = fillContainerCommandExecutor.onCommand(player, command, "fill-container", args);
        assertTrue(result);

        for (ItemStack item : items) {
            assertNotNull(item);
        }
    }

    @Test
    void onCommand_invalidNumberOfArgs() {
        final String[] args = new String[]{"0", "0", "0"};
        final boolean result = fillContainerCommandExecutor.onCommand(player, command, "fill-container", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidSender() {
        final String[] args = new String[]{"0", "0", "0", "50%cobblestone,50%oak_planks"};
        final BlockCommandSender blockSender = Mockito.mock(BlockCommandSender.class);

        final boolean result = fillContainerCommandExecutor.onCommand(blockSender, command, "fill-container", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidCoords() {
        final String[] args = new String[]{"test", "0", "0", "cobblestone"};
        final boolean result = fillContainerCommandExecutor.onCommand(player, command, "fill-container", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidDistribution() {
        final String[] args = new String[]{"0", "0", "0", "sponge_slab"};
        final boolean result = fillContainerCommandExecutor.onCommand(player, command, "fill-container", args);
        assertFalse(result);
    }

    @Test
    void onCommand_invalidBlockAtCoords() {
        final String[] args = new String[]{"0", "0", "0", "cobblestone"};

        Mockito.when(block.getType()).thenReturn(Material.FURNACE);
        Mockito.when(player.getWorld()).thenReturn(world);
        Mockito.when(world.getBlockAt(0, 0, 0)).thenReturn(block);

        final boolean result = fillContainerCommandExecutor.onCommand(player, command, "fill-container", args);
        assertFalse(result);
    }

    @Test
    void onTabComplete_notPlayer() {
        final CommandSender sender = Mockito.mock(Zombie.class);
        assertEquals(List.of(), fillContainerCommandExecutor.onTabComplete(sender, command, "test", new String[]{}));
    }

    @Test
    void onTabComplete_tooManyArgs() {
        assertEquals(List.of(), fillContainerCommandExecutor.onTabComplete(player, command, "test",
                new String[]{"1", "2", "3"}));
    }

    @Test
    void onTabComplete_noRayTraceFound() {
        final String[] args = new String[]{"1", "2"};
        assertEquals(List.of(), fillContainerCommandExecutor.onTabComplete(player, command, "test", args));
    }

    @Test
    void onTabComplete_noHitBlock() {
        Mockito.when(player.rayTraceBlocks(Mockito.anyDouble())).thenReturn(rayTraceResult);
        final String[] args = new String[]{"1", "2"};
        assertEquals(List.of(), fillContainerCommandExecutor.onTabComplete(player, command, "test", args));
    }

    @Test
    void onTabComplete_happyPath() {
        Mockito.when(player.rayTraceBlocks(Mockito.anyDouble())).thenReturn(rayTraceResult);
        Mockito.when(rayTraceResult.getHitBlock()).thenReturn(new BlockMock(new Location(world, 1, 2, 3)));
        final String[] args = new String[]{"1", "2"};
        assertEquals(List.of("2", "2 3"), fillContainerCommandExecutor.onTabComplete(player, command, "test", args));
    }

}
