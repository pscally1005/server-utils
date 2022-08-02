package com.scally.serverutils.undo;

public interface Changeset<T> {
    void add(T before, T after);
    boolean undo();
}
