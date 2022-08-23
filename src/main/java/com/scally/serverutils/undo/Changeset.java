package com.scally.serverutils.undo;

public interface Changeset<T> {
    void add(T change);
    boolean undo();
    void lock();
    int count();
}
