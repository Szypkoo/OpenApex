package lol.apex.manager;

import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ListManager<T> implements Iterable<T> {

    private final ArrayList<T> values = new ArrayList<>();

    @Override
    public @NonNull Iterator<T> iterator() {
        return this.values.iterator();
    }

    @SafeVarargs
    public final void addAll(Supplier<T>... values) {
        for(Supplier<T> value : values) {
            this.add(value.get());
        }
    }

    @SafeVarargs
    public final void addAll(T... values) {
        for(T value : values) {
            this.add(value);
        }
    }

    public void add(T value) {
        if(value != null) {
            this.values.add(value);
        }
    }

    public void addIfAbsent(T value) {
        if(value != null && !this.values.contains(value)) {
            this.values.add(value);
        }
    }

    public void remove(T value) {
        this.values.remove(value);
    }

    public void removeByIndex(int index) {
        this.values.remove(index);
    }

    public void clear() {
        this.values.clear();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public Stream<T> stream() {
        return this.values.stream();
    }

    public Optional<T> getByIndex(int index) {
        if(index > 0 && index < this.values.size()) {
            return Optional.ofNullable(this.values.get(index));
        }

        return Optional.empty();
    }

    public Optional<T> getFirst(Predicate<T> predicate) {
        return this.values.stream().filter(predicate).findFirst();
    }

    public List<T> getAll(Predicate<T> predicate) {
        return this.values.stream().filter(predicate).toList();
    }

    public List<T> getAllSorted(Predicate<T> predicate, Comparator<T> comparator) {
        return this.values.stream().filter(predicate).sorted(comparator).toList();
    }

}
