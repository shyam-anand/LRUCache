package com.shyamanand.lrucache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LRUClassUnitTest {
    @Test
    public void addSomeDataToCache_WhenGetData_ThenIsEqualWithCacheElement() {
        LRUCache<String, String> lruCache = new LRUCache<>(3);
        lruCache.set("1", "test1");
        lruCache.set("2", "test2");
        lruCache.set("3", "test3");
        assertTrue(lruCache.get("1").isPresent());
        assertEquals("test1", lruCache.get("1").get());
        assertTrue(lruCache.get("2").isPresent());
        assertEquals("test2", lruCache.get("2").get());
        assertTrue(lruCache.get("3").isPresent());
        assertEquals("test3", lruCache.get("3").get());
    }

    @Test
    public void addDataToCacheToTheNumberOfSize_WhenAddOneMoreData_ThenLeastRecentlyDataWillEvict() {
        LRUCache<String, String> lruCache = new LRUCache<>(3);
        lruCache.set("1", "test1");
        lruCache.set("2", "test2");
        lruCache.set("3", "test3");
        lruCache.get("1");
        lruCache.set("4", "test4");

        assertTrue(lruCache.get("1").isPresent());
        assertFalse(lruCache.get("2").isPresent());
    }
}
