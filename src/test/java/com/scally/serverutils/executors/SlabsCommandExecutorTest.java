package com.scally.serverutils.executors;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SlabsCommandExecutorTest {

    @Mock
    private CommandSender commandSender;

    @Mock
    private Command command;

    @Mock
    private Player player;

    private ServerMock server;

    private AutoCloseable mocks;

    private final SlabsCommandExecutor slabsCommandExecutor = new SlabsCommandExecutor();

    @BeforeEach
    public void before() {
        mocks = MockitoAnnotations.openMocks(this);
        server = MockBukkit.mock();
    }

    @AfterEach
    public void after() throws Exception {
        mocks.close();
        MockBukkit.unmock();
    }

    @Test
    public void slabsCommand_invalidNumberOfArgs() {
        final String[] args = new String[2];
        args[0] = "oak_slab";
        args[1] = "birch_slab";

        final boolean result = slabsCommandExecutor.onCommand(commandSender, command, "slabs", args);
        Assertions.assertFalse(result);
    }

    @Test
    public void onTabComplete_secondSlab() {
        final String[] args = new String[] {
          "0", "0", "0", "0", "0", "0", "birch_slab,a"
        };

        final List<String> tabOptions = slabsCommandExecutor.onTabComplete(player, command, "slabs", args);
        assertEquals(2, tabOptions.size());
        assertTrue(tabOptions.contains("birch_slab,acacia_slab"));
        assertTrue(tabOptions.contains("birch_slab,andesite_slab"));
    }

    // TODO: test for "birch_slab,"

    // TODO: test for "birch_slab,acacia_slab,d"

}
