package com.shyamanand.lrucache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCache<K, V> implements Cache<K, V> {

    private static final class CacheNode<K, V> {
        private final K key;
        private final V value;
        private Long accessedAt;

        private CacheNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private Long getAccessedAt() {
            return accessedAt;
        }

        private void accessed() {
            this.accessedAt = System.nanoTime();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheNode<?, ?> cacheNode = (CacheNode<?, ?>) o;
            return Objects.equals(key, cacheNode.key) && Objects.equals(value, cacheNode.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

    private static final class EvictionQueue<K, V> {
        private final PriorityQueue<CacheNode<K, V>> evictionQueue;

        private EvictionQueue(int capacity) {
            evictionQueue = new PriorityQueue<>(
                    capacity, Comparator.comparing(CacheNode::getAccessedAt));
        }

        private synchronized Optional<CacheNode<K, V>> evict() {
            if (evictionQueue.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(evictionQueue.remove());
        }

        private synchronized void add(CacheNode<K, V> node) {
            evictionQueue.remove(node);

            node.accessed();
            evictionQueue.add(node);
        }

        private void clear() {
            evictionQueue.clear();
        }
    }
    private final int capacity;
    private final Map<K, CacheNode<K, V>> cache;
    private final EvictionQueue<K, V> evictionQueue;

    public LRUCache(int size) {
        this.capacity = size;
        this.cache = new ConcurrentHashMap<>(size);
        this.evictionQueue = new EvictionQueue<>(size);
    }

    public boolean set(K key, V value) {
        CacheNode<K, V> newNode = new CacheNode<>(key, value);
        if (size() == capacity) {
            evictionQueue.evict().ifPresent(evictedNode -> cache.remove(evictedNode.key));
        }
        cache.putIfAbsent(key, newNode);
        evictionQueue.add(newNode);
        return true;
    }

    public Optional<V> get(K key) {
        if (!cache.containsKey(key)) {
            return Optional.empty();
        }
        CacheNode<K, V> cacheNode = cache.get(key);
        evictionQueue.add(cacheNode);
        return Optional.of(cacheNode.value);
    }

    public int size() {
        return this.cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public void clear() {
        cache.clear();
        evictionQueue.clear();
    }
}