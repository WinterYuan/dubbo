package org.apache.dubbo.common.utils;


import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LFUCache
 * @author  winter
 * @date    2019/9/12
 */
public class LFUCache {
    private ConcurrentHashMap<Object, Node> cacheMap;
    private ConcurrentHashMap<Object, Set<Node>> frequencyMap;
    private int minFrequency;
    private int capacity;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        cacheMap = new ConcurrentHashMap<>();
        frequencyMap = new ConcurrentHashMap<>();
        minFrequency = 1;
    }

    public Object get(Object key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        } else {
            Node node = cacheMap.get(key);
            updateNode(node);
            return node.value;
        }
    }

    public void put(Object key, Object value) {
        if (capacity <= 0) {
            return;
        }
        if (cacheMap.containsKey(key)) {
            Node node = cacheMap.get(key);
            if (node.value != value) {
                node.value = value;
            }
            updateNode(node);
            return;
        } else {
            Node node = new Node(key, value, 1);
            if (!frequencyMap.containsKey(node.frequency)) {
                Set<Node> newLink = Collections.synchronizedSet(new LinkedHashSet<>());
                newLink.add(node);
                frequencyMap.put(node.frequency, newLink);
            } else {
                Set<Node> newLink = frequencyMap.get(node.frequency);
                newLink.add(node);
                frequencyMap.put(node.frequency, newLink);
            }
            cacheMap.put(key, node);
        }
        if (cacheMap.size() > capacity) {
            Set<Node> minFrequencyList = frequencyMap.get(minFrequency);
            Node evict = minFrequencyList.iterator().next();
            minFrequencyList.remove(evict);
            cacheMap.remove(evict.key);
        }
        minFrequency = 1;
    }

    private void updateNode(Node node) {
        Set<Node> currentLink = frequencyMap.get(node.frequency);
        currentLink.remove(node);
        if (currentLink.isEmpty() && node.frequency == minFrequency) {
            minFrequency++;
        }
        node.frequency++;
        if (frequencyMap.containsKey(node.frequency)) {
            Set<Node> nextLink = frequencyMap.get(node.frequency);
            nextLink.add(node);
            frequencyMap.put(node.frequency, nextLink);
        } else {
            Set<Node> nextLink = Collections.synchronizedSet(new LinkedHashSet<>());
            nextLink.add(node);
            frequencyMap.put(node.frequency, nextLink);
        }
    }

    private class Node {
        private Object key;
        private Object value;
        private int frequency;

        public Node(Object key, Object value, int frequency) {
            this.key = key;
            this.value = value;
            this.frequency = frequency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return key.equals(node.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
