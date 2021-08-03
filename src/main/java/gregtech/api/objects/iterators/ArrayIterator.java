package gregtech.api.objects.iterators;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
    private final T[] arr;
    private int offset;

    public ArrayIterator(T[] arr) {
        this.arr = arr;
    }

    @Override
    public boolean hasNext() {
        return offset != arr.length;
    }

    @Override
    public T next() {
        T out = arr[offset];
        offset++;
        return out;
    }
}
