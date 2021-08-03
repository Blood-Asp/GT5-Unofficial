package gregtech.api.objects.iterators;

import java.util.Iterator;

public class NonNullIterator<T> implements Iterator<T> {
    private final Iterator<T> internal;
    private T last = null;

    public NonNullIterator(Iterator<T> in) {
        this.internal = in;
    }

    @Override
    public boolean hasNext() {
        while (last == null) {
            if (internal.hasNext()) {
                last = internal.next();
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public T next() {
        while (last == null) {
            last = internal.next();
        }

        T temp = last;
        last = null;
        return temp;
    }
}
