package gregtech.api.util.extensions;

import gregtech.api.objects.iterators.ArrayIterator;
import gregtech.api.objects.iterators.MergedIterator;
import gregtech.api.objects.iterators.NonNullIterator;

import java.util.Iterator;

public class IteratorExt {
    @SafeVarargs
    public static <T> Iterator<T> merge(Iterator<T>... iterators) {
        return new MergedIterator<>(iterators);
    }

    public static <T> Iterator<T> withoutNulls(Iterator<T> iterator) {
        return new NonNullIterator<>(iterator);
    }

    @SafeVarargs
    public static <T> ArrayIterator<T> ofArray(T... items) {
        return new ArrayIterator<>(items);
    }
}
