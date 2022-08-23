package com.scally.serverutils.undo;

import com.scally.serverutils.chat.ChatMessageSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class UndoManager {

    public static final int stackSize = 10;

    private static UndoManager instance = null;

    private final Map<UUID, Stack<Changeset> > changes;

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

        Stack<Changeset> stack;
        if(changes.get(player.getUniqueId()) == null) {
            stack = new Stack<Changeset>();
        } else {
            stack = changes.get(player.getUniqueId());
        }

        if(stack.size() >= stackSize) {
            stack.remove(0);
        }
        stack.push(changeset);
        changes.put(player.getUniqueId(), stack);
    }

    public boolean undo(Player player) {

        final Stack<Changeset> stack = changes.get(player.getUniqueId());
        if (stack == null || stack.empty()) {
            messageSender.sendError(player, "Nothing to undo!");
            return false;
        }

        String message = stack.pop().undo();
        messageSender.sendSuccess(player, message);
        changes.put(player.getUniqueId(), stack);

        return true;
    }

}
