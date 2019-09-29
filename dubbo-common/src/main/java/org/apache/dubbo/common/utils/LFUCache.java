package org.apache.dubbo.common.utils;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;

public class LFUCache {
    private HashMap<Object, Node> kvMap;
    private HashMap<Object, LinkedHashSet<Node>> frequencyMap;
    private int minFrequency;
    private int capacity;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        kvMap = new HashMap<>();
        frequencyMap = new HashMap<>();
        minFrequency = 1;
    }

    public Object get(Object key) {
        if (!kvMap.containsKey(key)) {
            return null;
        } else {
            Node node = kvMap.get(key);
            updateNode(node);
            return node.value;
        }
    }

    public void put(Object key, Object value) {
        if (capacity <= 0) {
            return;
        }
        if (kvMap.containsKey(key)) {
            Node node = kvMap.get(key);
            if (node.value != value) {
                node.value = value;
            }
            updateNode(node);
            return;
        } else {
            Node node = new Node(key, value, 1);
            if (!frequencyMap.containsKey(node.frequency)) {
                LinkedHashSet<Node> newLink = new LinkedHashSet<>();
                newLink.add(node);
                frequencyMap.put(node.frequency, newLink);
            } else {
                LinkedHashSet<Node> newLink = frequencyMap.get(node.frequency);
                newLink.add(node);
                frequencyMap.put(node.frequency, newLink);
            }
            kvMap.put(key, node);
        }
        if (kvMap.size() > capacity) {
            LinkedHashSet<Node> minFrequencyList = frequencyMap.get(minFrequency);
            Node evict = minFrequencyList.iterator().next();
            minFrequencyList.remove(evict);
            kvMap.remove(evict.key);
        }
        minFrequency = 1;
    }

    private void updateNode(Node node) {
        LinkedHashSet<Node> currentLink = frequencyMap.get(node.frequency);
        currentLink.remove(node);
        if (currentLink.isEmpty() && node.frequency == minFrequency) {
            minFrequency++;
        }
        node.frequency++;
        if (frequencyMap.containsKey(node.frequency)) {
            LinkedHashSet<Node> nextLink = frequencyMap.get(node.frequency);
            nextLink.add(node);
            frequencyMap.put(node.frequency, nextLink);
        } else {
            LinkedHashSet<Node> nextLink = new LinkedHashSet<>();
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
