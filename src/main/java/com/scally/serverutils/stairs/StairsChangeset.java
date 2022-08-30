package com.scally.serverutils.stairs;

import com.scally.serverutils.undo.Changeset;

public class StairsChangeset implements Changeset<StairsChange> {

    @Override
    public void add(StairsChange change) {
        // TODO: implement
    }

    @Override
    public boolean undo() {
        // TODO: implement
        return false;
    }

    @Override
    public void lock() {
        // TODO: implement
    }

    @Override
    public int count() {
        // TODO: implement
        return 0;
    }
}
