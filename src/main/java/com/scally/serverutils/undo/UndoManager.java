package com.scally.serverutils.undo;

import com.scally.serverutils.slabs.SlabsChangeset;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UndoManager {

    private static UndoManager instance = null;

    private final Map<UUID, SlabsChangeset> changes;

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

    public void store(Player player, SlabsChangeset changeset) {
        changeset.lock();
        changes.put(player.getUniqueId(), changeset);
    }

    public boolean undo(Player player) {
        final SlabsChangeset changeset = changes.get(player.getUniqueId());
        if (changeset == null) {
            return false;
        }

//        changeset.undo();
        changeset.returnStrings(player);
        return true;
    }

}
