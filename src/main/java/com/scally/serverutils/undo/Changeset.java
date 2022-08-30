package com.scally.serverutils.undo;

public interface Changeset<T extends Change> {
    void add(T change);
    boolean undo();
    void lock();
    int count();
}
