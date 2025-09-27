package advanced.cluster;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class Node implements Comparable<Node> {
    private final int id;
    private final String host;
    private final int httpPort; //to accept http/https requests
    private final int socketPort; //to accepts tcp requests
    private final int zoneId;
    private final List<Integer> partitions; //virtual nodes

    public Node(int id, String host, int httpPort, int socketPort, int zoneId, List<Integer> partitions) {
        this.id = id;
        this.host = host;
        this.httpPort = httpPort;
        this.socketPort = socketPort;
        this.zoneId = zoneId;
        this.partitions = Collections.unmodifiableList(partitions);
    }

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public int getZoneId() {
        return zoneId;
    }

    public List<Integer> getPartitionIds() {
        return partitions;
    }

    public int getNumberOfPartitions() {
        return partitions.size();
    }

    public URI getHttpUrl() {
        try {
            return new URI("http://" + getHost() + ":" + getHttpPort());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid host format for node " + id + ".", e);
        }
    }

    public URI getSocketUrl() {
        try {
            return new URI("tcp://" + getHost() + ":" + getSocketPort());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid host format for node " + id + ".", e);
        }
    }

    @Override
    public String toString() {
        return nodeString() + " in zone " + getZoneId() + " partitionList: " + partitions;
    }

    public String nodeString() {
        return "Node " + getHost() + ":" + getSocketPort() + "[id " + getId() + "]";
    }

    public String getStateString() {
        return nodeString() + " in zone " + getZoneId() + " with socket port " + getSocketPort()
                + ", and http port" + getHttpPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node node)) {
            return false;
        }
        return getId() == node.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.id, other.getId());
    }

    public boolean isEqualState(Node other) {
        return id == other.getId() && host.equalsIgnoreCase(other.getHost())
                && httpPort == other.getHttpPort() && socketPort == other.getSocketPort()
                && zoneId == other.getZoneId();
    }
}
