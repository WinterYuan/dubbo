package org.apache.dubbo.common.utils;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;

public class LFUCache {
    private HashMap<Object, Node> kvMap;
    private HashMap<Integer, LinkedHashSet<Node>> frequenceMap;
    private int minFrequency;
    private int capacity;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        kvMap = new HashMap<>();
        frequenceMap = new HashMap<>();
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
            updateNode(kvMap.get(key));
            return;
        } else {
            Node node = new Node(key, value, 1);
            if (!frequenceMap.containsKey(node.frequency)) {
                LinkedHashSet<Node> newLink = new LinkedHashSet<>();
                newLink.add(node);
                frequenceMap.put(node.frequency, newLink);
            } else {
                LinkedHashSet<Node> newLink = frequenceMap.get(node.frequency);
                newLink.add(node);
                frequenceMap.put(node.frequency, newLink);
            }
            kvMap.put(key, node);
            minFrequency = 1;
        }
        if (kvMap.size() > capacity) {
            LinkedHashSet<Node> minFrequencyList = frequenceMap.get(minFrequency);
            Node toRemoveNode = minFrequencyList.iterator().next();
            minFrequencyList.remove(toRemoveNode);
            kvMap.remove(toRemoveNode.key);
        }
    }

    private void updateNode(Node node) {
        LinkedHashSet currentLink = frequenceMap.get(node.frequency);
        currentLink.remove(node);
        if (currentLink.isEmpty() && node.frequency == minFrequency) {
            minFrequency++;
        }
        node.frequency++;
        if (frequenceMap.containsKey(node.frequency)) {
            LinkedHashSet<Node> nextLink = frequenceMap.get(node.frequency);
            nextLink.add(node);
            frequenceMap.put(node.frequency, nextLink);
        } else {
            LinkedHashSet<Node> nextLink = new LinkedHashSet<>();
            nextLink.add(node);
            frequenceMap.put(node.frequency, nextLink);
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
            return frequency == node.frequency &&
                    value.equals(node.value) && key.equals(node.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value, frequency);
        }

    }
}
