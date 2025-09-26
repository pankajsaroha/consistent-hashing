package basic;

import java.util.*;

public class ConsistentHashing {

    private final int virtualNodes;
    private final int replicationFactor;
    private final NavigableMap<Integer, Node> ring;
    private final Set<String> distinct;

    public ConsistentHashing(int virtualNodes, int replicationFactor) {
        this.virtualNodes = virtualNodes;
        this.replicationFactor = replicationFactor;
        this.ring = new TreeMap<>();
        this.distinct = new HashSet<>();
    }

    public void addNode(Node node) {
        String key = node.id() + "-" + node.host() + ":" + node.port();
        distinct.add(key);
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hashCode(key + "-" + i), node);
        }
    }

    public void removeNode(Node node) {
        String key = node.id() + "-" + node.host() + ":" + node.port();
        distinct.remove(key);
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hashCode(key + "-" + i));
        }
    }

    //get Primary Node for the key
    public Node getPrimaryNode(String key) {
        if (ring.isEmpty()) return null;

        SortedMap<Integer, Node> tailMap = ring.tailMap(hashCode(key));
        return tailMap.isEmpty() ? ring.firstEntry().getValue() : tailMap.firstEntry().getValue();
    }

    //collect distinct physical nodes
    public List<Node> getNodesForKey (String key) {
        List<Node> replicas = new ArrayList<>();
        if (ring.isEmpty()) return null;

        Set<String> seen = new HashSet<>();
        Map.Entry<Integer, Node> entry = ring.ceilingEntry(hashCode(key));
        if (entry == null) entry = ring.firstEntry();

        //considering replicationFactor is less than or equals to distinct nodes
        //still taking min because it might go in infinite loop if replicationFactor is greater than distinct nodes
        while (replicas.size() < Math.min(replicationFactor, distinct.size())) {
            Node node = entry.getValue();
            String nodeId = node.id() + "@" + node.host() + ":" + node.port();

            if (seen.add(nodeId)) replicas.add(node);

            entry = ring.higherEntry(entry.getKey());
            if (entry == null) {
                entry = ring.firstEntry();
            }
        }

        return replicas;
    }

    private int hashCode(String key) {
        return key.hashCode();
    }

    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing(10, 3);
        Node n1 = new Node(1, "10.0.0.1", 9000);
        Node n2 = new Node(2, "10.0.0.2", 9000);
        Node n3 = new Node(3, "10.0.0.3", 9000);

        ch.addNode(n1);
        ch.addNode(n2);
        ch.addNode(n3);

        System.out.println("Primary for 'alice': " + ch.getPrimaryNode("alice").toString());
        System.out.println("Replicas for 'bob': " + ch.getNodesForKey("bob").toString());
    }
}