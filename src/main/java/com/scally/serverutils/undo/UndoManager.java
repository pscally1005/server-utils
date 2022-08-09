package com.scally.serverutils.undo;

import com.scally.serverutils.chat.ChatMessageSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UndoManager {

    private static UndoManager instance = null;

    private final Map<UUID, Changeset> changes;

    private final ChatMessageSender messageSender = new ChatMessageSender();

    private UndoManager() {
        changes = new HashMap<>();
    }

    public static UndoManager getInstance() {
        synchronized (UndoManager.class) {
            if (instance == null) {
                instance = new UndoManager();
            }
            return instance;
        }
    }

    public void store(Player player, Changeset changeset) {
        changeset.lock();
        changes.put(player.getUniqueId(), changeset);
    }

    public boolean undo(Player player) {
        final Changeset changeset = changes.get(player.getUniqueId());
        if (changeset == null) {
            return false;
        }

        String message = changeset.undo();
        messageSender.sendSuccess(player, message);
        return true;
    }

}
