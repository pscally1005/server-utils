package com.scally.serverutils.undo;

import com.scally.serverutils.chat.ChatMessageSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class UndoManager {

    public static final int STACK_SIZE = 10;

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

        if(stack.size() >= STACK_SIZE) {
            stack.remove(0);
        }

        if(changeset.count() > 0) {
            stack.push(changeset);
            changes.put(player.getUniqueId(), stack);
        }

    }

    public boolean undo(Player player, int undoSize) {

        final Stack<Changeset> stack = changes.get(player.getUniqueId());
        if (stack == null || stack.empty()) {
            messageSender.sendError(player, "Nothing to undo!");
            return false;
        }

        String message;
        if(undoSize > stack.size()) {
            message = String.format("Undid %d edits", stack.size());
        } else {
            message = String.format("Undid %d edits", undoSize);
        }

        while(undoSize > 0) {
            stack.pop().undo();
            undoSize--;
            if(stack.empty()) {
                break;
            }
        }
        messageSender.sendSuccess(player, message);

        changes.put(player.getUniqueId(), stack);
        return true;
    }

}
