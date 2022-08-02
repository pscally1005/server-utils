package com.scally.serverutils;

import com.scally.serverutils.chat.ChatMessageSender;
import com.scally.serverutils.executors.FillContainerCommandExecutor;
import com.scally.serverutils.executors.SlabsCommandExecutor;
import com.scally.serverutils.undo.UndoCommandExecutor;
import com.scally.serverutils.undo.UndoManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils extends JavaPlugin {

    private final ChatMessageSender messageSender = new ChatMessageSender();
    private final UndoManager undoManager = UndoManager.getInstance();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("slabs").setExecutor(new SlabsCommandExecutor(messageSender, undoManager));
        this.getCommand("fill-container").setExecutor(new FillContainerCommandExecutor(messageSender));

        this.getCommand("s-undo").setExecutor(new UndoCommandExecutor(messageSender, undoManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
