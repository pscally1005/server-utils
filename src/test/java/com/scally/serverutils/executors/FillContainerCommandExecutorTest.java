package com.scally.serverutils.executors;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FillContainerCommandExecutorTest {

    private final FillContainerCommandExecutor fillContainerCommandExecutor = new FillContainerCommandExecutor();

    @Mock
    private Entity sender;

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

    private AutoCloseable mocks;

    @BeforeEach
    public void before() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void after() throws Exception {
        mocks.close();
    }

    @Test
    public void onCommand_happyPath() {
        final String[] args = new String[] { "0", "0", "0", "50%cobblestone,50%oak_planks" };

        Mockito.when(sender.getWorld()).thenReturn(world);
        Mockito.when(world.getBlockAt(0, 0, 0)).thenReturn(block);
        Mockito.when(block.getType()).thenReturn(Material.CHEST);
        Mockito.when(block.getState()).thenReturn(container);
        Mockito.when(container.getInventory()).thenReturn(inventory);

        final ItemStack[] items = new ItemStack[20];
        Mockito.when(inventory.getContents()).thenReturn(items);

        final boolean result = fillContainerCommandExecutor.onCommand(sender, command, "fill-container", args);
        assertTrue(result);

        for (int i = 0; i < items.length; i++) {
            assertNotNull(items[i]);
        }
    }

}
