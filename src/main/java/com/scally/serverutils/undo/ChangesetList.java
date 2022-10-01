package com.scally.serverutils.undo;

import java.util.ArrayList;
import java.util.List;

public abstract class ChangesetList<T extends Change> implements Changeset<T> {

    private final List<Change> changeList = new ArrayList<>();
    private boolean locked = false;

    @Override
    public void add(Change change) {
        if (locked) {
            throw new IllegalStateException("Changeset is already locked!");
        }
        changeList.add(change);
    }

    @Override
    public boolean undo() {
        for (Change change : changeList) {
            final boolean success = change.undo();
            if (!success) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void lock() {
        locked = true;
    }

    @Override
    public int count() {
        return changeList.size();
    }

}
