package com.scally.serverutils.undo;

public interface Changeset<T> {
    void add(T change);
    String undo();
    void lock();
    int count();
}
