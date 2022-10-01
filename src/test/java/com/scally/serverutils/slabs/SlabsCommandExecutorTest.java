package com.scally.serverutils.slabs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.scally.serverutils.undo.UndoManager;
import org.bukkit.Tag;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    private ServerMock server;

    private SlabsCommandExecutor slabsCommandExecutor;

    @BeforeEach
    public void before() {
        server = MockBukkit.mock();
        slabsCommandExecutor = new SlabsCommandExecutor(undoManager);
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    public void onTabComplete_blank() {
        final String[] args = new String[] {
                "0", "0", "0", "0", "0", "0", ""
        };

        final List<String> tabOptions = slabsCommandExecutor.onTabComplete(player, command, "alias", args);
        assertNotNull(tabOptions);
        assertEquals(Tag.SLABS.getValues().size(), tabOptions.size());
        assertEquals("acacia_slab", tabOptions.get(0));
    }

    // TODO: also should be part of InputValidator tests
//    @Test
//    public void onCommand_volumeTooLarge_sendsCorrectMessage() {
//        final String[] args = new String[] {
//                "0", "0", "0", "65", "65", "65", "oak_slab", "birch_slab"
//        };
//
//        final boolean result = slabsCommandExecutor.onCommand(player, command, "slabs", args);
//        assertFalse(result);
//
//        final String expectedMessage = String.format("Volume must be less than %d blocks",
//                ServerUtils.VOLUME_LIMIT);
//
//        Mockito.verify(messageSender, Mockito.times(1))
//                .sendError(player, expectedMessage);
//    }

    // TODO: this should be an InputValidator test
//    @Test
//    public void onCommand_enterTilda() {
//        final String[] args = new String[] {
//                "~", "~", "~", "~", "~", "~"
//        };
//
//        Mockito.when(location.getBlockX()).thenReturn(0);
//        Mockito.when(location.getBlockY()).thenReturn(0);
//        Mockito.when(location.getBlockZ()).thenReturn(0);
//        Mockito.when(player.getLocation()).thenReturn(location);
//
//        final int[] coords = InputValidator.parseArgs(player, args);
//        for(int i = 0; i < coords.length; i++) {
//            if(i == 0 || i == 3) { assertEquals(coords[i], location.getBlockX()); }
//            else if(i == 1 || i == 4) { assertEquals(coords[i], location.getBlockY()); }
//            else if(i == 2 || i == 5) { assertEquals(coords[i], location.getBlockZ()); }
//        }
//
//    }

    // TODO: onCommand tests

}
