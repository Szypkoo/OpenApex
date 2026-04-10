package lol.apex.manager;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class MapManager<K, V> implements Iterable<V> {

    private final Map<K, V> map = createMap();

    public Map<K, V> createMap() {
        return new HashMap<>();
    }

    @Override
    public @NonNull Iterator<V> iterator() {
        return this.map.values().iterator();
    }

    public void put(K key, V value) {
        if(key != null && value != null) {
            this.map.put(key, value);
        }
    }

    public Optional<V> getByKey(K key) {
        return Optional.ofNullable(this.map.get(key));
    }

}
