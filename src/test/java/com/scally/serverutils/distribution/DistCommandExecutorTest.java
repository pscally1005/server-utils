package com.scally.serverutils.distribution;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistCommandExecutorTest {

    @Mock
    private Command command;

    @TempDir
    private Path tempDir;

    private ServerMock server;
    private SavedDistributionStore store;
    private DistCommandExecutor executor;
    private Plugin permissionPlugin;

    @BeforeEach
    void setUp() {
        permissionPlugin = null;
        server = MockBukkit.mock();
        store = new SavedDistributionStore(tempDir.toFile());
        store.load();
        executor = new DistCommandExecutor(store);
    }

    private Plugin permissionPlugin() {
        if (permissionPlugin == null) {
            permissionPlugin = mock(Plugin.class);
            when(permissionPlugin.isEnabled()).thenReturn(true);
        }
        return permissionPlugin;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private void grantReadWrite(PlayerMock player) {
        player.addAttachment(permissionPlugin(), "scally.dist.read", true);
        player.addAttachment(permissionPlugin(), "scally.dist.write", true);
    }

    private void grantReadOnly(PlayerMock player) {
        player.addAttachment(permissionPlugin(), "scally.dist.read", true);
    }

    @Test
    void nonPlayer_rejects() {
        final CommandSender sender = mock(CommandSender.class);
        assertTrue(executor.onCommand(sender, command, "dist", new String[]{"list"}));
        verify(sender).sendMessage(ChatColor.RED + "This command can only be used by players.");
    }

    @Test
    void readPermissionDenied() {
        final PlayerMock player = server.addPlayer("Lonely");
        assertTrue(executor.onCommand(player, command, "dist", new String[]{"list"}));
        player.assertSaid(ChatColor.RED + "You do not have permission to use this command.");
    }

    @Test
    void save_thenDuplicateKey() {
        final PlayerMock player = server.addPlayer("Saver");
        grantReadWrite(player);
        assertTrue(executor.onCommand(player, command, "dist", new String[]{"save", "town", "stone"}));
        assertTrue(executor.onCommand(player, command, "dist", new String[]{"save", "town", "dirt"}));
        player.assertSaid(ChatColor.GREEN + "Saved distribution \"town\".");
        player.assertSaid(ChatColor.RED + "A distribution named \"town\" already exists. Use update.");
    }

    @Test
    void save_invalidDistribution() {
        final PlayerMock player = server.addPlayer("BadDist");
        grantReadWrite(player);
        assertTrue(executor.onCommand(player, command, "dist", new String[]{"save", "x", "not_a_material"}));
        player.assertSaid(ChatColor.RED + "Material not_a_material not found!");
    }

    @Test
    void writePermissionDenied_onSave() {
        final PlayerMock player = server.addPlayer("NoWrite");
        grantReadOnly(player);
        assertTrue(executor.onCommand(player, command, "dist", new String[]{"save", "k", "stone"}));
        player.assertSaid(ChatColor.RED + "You do not have permission to save distributions.");
    }

    @Test
    void get_crossPlayerReadAllowed() {
        final PlayerMock alice = server.addPlayer("Alice");
        final PlayerMock bob = server.addPlayer("Bob");
        grantReadWrite(alice);
        grantReadOnly(bob);
        assertTrue(executor.onCommand(alice, command, "dist", new String[]{"save", "path", "50%stone,50%dirt"}));
        assertTrue(executor.onCommand(bob, command, "dist", new String[]{"get", "Alice", "path"}));
        bob.assertSaid(ChatColor.GREEN + "50%stone,50%dirt");
    }

    @Test
    void update_nonOwnerCannotTouchOthersEntry() {
        final PlayerMock alice = server.addPlayer("Alice");
        final PlayerMock bob = server.addPlayer("Bob");
        grantReadWrite(alice);
        grantReadWrite(bob);
        assertTrue(executor.onCommand(alice, command, "dist", new String[]{"save", "path", "stone"}));
        assertTrue(executor.onCommand(bob, command, "dist", new String[]{"update", "path", "dirt"}));
        bob.assertSaid(ChatColor.RED + "No distribution named \"path\" to update.");
        assertEquals("stone", store.get(alice.getUniqueId(), "path"));
    }

    @Test
    void delete_nonOwnerCannotDeleteOthersEntry() {
        final PlayerMock alice = server.addPlayer("Alice");
        final PlayerMock bob = server.addPlayer("Bob");
        grantReadWrite(alice);
        grantReadWrite(bob);
        assertTrue(executor.onCommand(alice, command, "dist", new String[]{"save", "path", "stone"}));
        assertTrue(executor.onCommand(bob, command, "dist", new String[]{"delete", "path"}));
        bob.assertSaid(ChatColor.RED + "No distribution named \"path\" to delete.");
        assertEquals("stone", store.get(alice.getUniqueId(), "path"));
    }

    @Test
    void list_unknownPlayer() {
        final PlayerMock player = server.addPlayer("Lister");
        grantReadWrite(player);
        assertTrue(executor.onCommand(player, command, "dist", new String[]{"list", "NeverJoinedPlayerXyz"}));
        player.assertSaid(ChatColor.RED + "Unknown or never-seen player: NeverJoinedPlayerXyz");
    }

    @Test
    void wrongArity_returnsFalse() {
        final PlayerMock player = server.addPlayer("Arity");
        grantReadWrite(player);
        assertFalse(executor.onCommand(player, command, "dist", new String[]{"get"}));
        assertFalse(executor.onCommand(player, command, "dist", new String[]{"save", "only"}));
    }
}
