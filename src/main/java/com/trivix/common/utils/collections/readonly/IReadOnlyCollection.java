package com.trivix.common.utils.collections.readonly;

import java.util.Enumeration;

public interface IReadOnlyCollection<T> extends Iterable<T> {
    int size();
    T get(int index);
    boolean contains(T element);
    IReadOnlyCollection<T> sublist(int startIndex, int endIndex);
    boolean isEmpty();
    T last();
}
