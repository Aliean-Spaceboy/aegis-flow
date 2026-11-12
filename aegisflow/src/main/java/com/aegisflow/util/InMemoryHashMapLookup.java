package com.aegisflow.util;

import java.util.HashMap;

/**
 * Minimal generic in-memory key-value lookup utility backed by {@link HashMap}.
 *
 * <p><strong>Lifecycle notice:</strong> All entries are stored exclusively in heap memory
 * for the lifetime of this instance and are permanently lost when the application process stops.
 *
 * <p><strong>Thread-safety notice:</strong> This utility is <em>not</em> thread-safe; callers
 * sharing an instance across multiple threads must provide external synchronization.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class InMemoryHashMapLookup<K, V> {

    private final HashMap<K, V> entries = new HashMap<>();

    /**
     * Stores a key-value pair in the lookup map, replacing any existing value for the key.
     *
     * @param key   lookup key
     * @param value associated value
     * @return previous value associated with {@code key}, or {@code null} if none
     */
    public V put(K key, V value) {
        return entries.put(key, value);
    }

    /**
     * Retrieves the value mapped to {@code key}, or {@code null} if the key is absent.
     *
     * @param key lookup key
     * @return mapped value, or {@code null} when missing (or explicitly mapped to {@code null})
     */
    public V get(K key) {
        return entries.get(key);
    }

    /**
     * Checks whether an entry exists for {@code key}.
     *
     * @param key lookup key
     * @return {@code true} if {@code key} is present, {@code false} otherwise
     */
    public boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    /**
     * Removes the entry for {@code key} if present.
     *
     * @param key lookup key
     * @return previous value associated with {@code key}, or {@code null} if absent
     */
    public V remove(K key) {
        return entries.remove(key);
    }

    /**
     * Removes all entries from the lookup map.
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Returns the current number of stored entries.
     *
     * @return entry count
     */
    public int size() {
        return entries.size();
    }
}

