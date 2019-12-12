package com.trivix.common.utils.collections.readonly;

import java.util.Iterator;
import java.util.List;

public class ReadOnlyList<T> implements IReadOnlyCollection<T> {
    private List<T> list;

    public ReadOnlyList(List<T> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public boolean contains(T element) {
        return list.contains(element);
    }

    @Override
    public IReadOnlyCollection<T> sublist(int startIndex, int endIndex) {
        return new ReadOnlyList<>(list.subList(startIndex, endIndex));
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public T last() {
        return list.get(list.size() - 1);
    }


    @Override
    public Iterator<T> iterator() {
        return new ReadOnlyListIterator(list);
    }

    class ReadOnlyListIterator implements Iterator<T> {
        private int currentIndex;
        private List<T> list;

        // initialize pointer to head of the list for iteration 
        public ReadOnlyListIterator(List<T> list)
        {
            this.list = list;
            currentIndex = 0;
        }

        // returns false if next element does not exist 
        public boolean hasNext()
        {
            return list.size() > 0 && currentIndex < list.size();

        }

        // return current data and update pointer 
        public T next()
        {
            if (list.size() > 0 && currentIndex < list.size()) {
                T element = list.get(currentIndex);
                currentIndex++;
                return element;
            }
            
            return null;
        }

        // implement if needed 
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
