package graph;

import java.util.*;

/**
 * Created by gharizanov on 23.9.2015 �..
 */
public class DependencyGraph {

    private Map<String, Node> graph;

    public DependencyGraph() {
        graph = new HashMap<>();
    }

    /**
     * For testing purposes
     * @param firstNodeLabel
     * @param secondNodeLabel
     * @return whether firstNode depends on secondNode.
     */
    public boolean hasDependency(String firstNodeLabel, String secondNodeLabel){
        Node firstNode = graph.get(firstNodeLabel);
        Node secondNode = graph.get(secondNodeLabel);

        if(firstNode == null || secondNode == null){
            return false;
        }

        if(firstNode.dependsOnList.contains(secondNode)){
            return true;
        }

        return false;
    }

    /**
     * For testing purposes
     * @param firstNodeLabel
     * @param secondNodeLabel
     * @return whether firstNode is depended upon by secondNode.
     */
    public boolean hasInverseDependency(String firstNodeLabel, String secondNodeLabel){
        Node firstNode = graph.get(firstNodeLabel);
        Node secondNode = graph.get(secondNodeLabel);

        if(firstNode == null || secondNode == null){
            return false;
        }

        if(firstNode.dependentUponList.contains(secondNode)){
            return true;
        }

        return false;
    }


    /**
     * Creates dependencies between the node specified in the first parameter and the nodes in the rest. Creates the
     * nodes if they do not exist. In case a CyclicGraphException is thrown, the dependencies are reverted.
     * @param rootNodeLabel target node
     * @param nodeDependenciesLabels dependency nodes
     * @throws CyclicGraphException
     */
    public void addDependency(String rootNodeLabel, String... nodeDependenciesLabels) throws CyclicGraphException{
        Node dependency = getOrCreateNode(rootNodeLabel);
        try{
            for (String newDependencyLabel : nodeDependenciesLabels) {
                pushDependencyToNode(dependency, newDependencyLabel);
            }
        } catch (CyclicGraphException ex){
            for (String dependencyLabel : nodeDependenciesLabels) {
                removeDependency(dependency.label, dependencyLabel);
            }
        }
    }


    public void removeDependency(String rootNodeLabel, String... nodeDependenciesLabels){
        Node node = graph.get(rootNodeLabel);

        if(node == null){
            return;
        }

        for (String dependency : nodeDependenciesLabels) {

            Node dependencyNode = graph.get(dependency);

            if(dependencyNode != null){
                node.dependsOnList.remove(dependencyNode);
                dependencyNode.dependentUponList.remove(node);
            }
        }

        //clear unused nodes
        if(node.dependsOnList.size() == 0 && node.dependentUponList.size() == 0){
            graph.remove(node.label);
        }
    }


    private void pushDependencyToNode(Node node, String newDependencyLabel) throws CyclicGraphException {
        Node dependencyNode = getOrCreateNode(newDependencyLabel);

        if(linkCreatesCyclicDependency(node, dependencyNode)){
             throw new CyclicGraphException(String.format("Dependency %s -> %s creates a cyclic dependency.", node.label, dependencyNode.label));
        }

        node.dependsOnList.add(dependencyNode);
        dependencyNode.dependentUponList.add(node);
    }


    private boolean linkCreatesCyclicDependency(Node node, Node dependencyNode) {

        String path = BFS(dependencyNode, node);

        if(path.contains(node.label)){
            return true;
        }

        return false;
    }


    private Node getOrCreateNode(String label){
        Node node = graph.get(label);

        if(node == null){
            node = new Node(label);
            graph.put(label, node);
        }

        return node;
    }


    public List<String> buildFullDependencySet(){

        List<String> result = new ArrayList<>();

        for (String root : graph.keySet()) {

            Node node = graph.get(root);

            if(node.dependsOnList.size() != 0){
                result.add(BFS(node, null));
            }
        }

        return result;
    }


    public List<String> buildFullInverseDependencySet(){

        List<String> result = new ArrayList<>();

        for (String root : graph.keySet()) {

            Node node = graph.get(root);

            if(node.dependentUponList.size() != 0){
                result.add(BFS(node, null, true));
            }
        }

        return result;
    }

    /**
     * Wrapper method, calls BFS with the inverse flag set to false.
     */
    private String BFS(Node start, Node end){
        return BFS(start, end, false);
    }

    /**
    * @return String representation of the path between start and end. If end is null, a full traversing is performed.
    */
    private String BFS(Node start, Node end, boolean inverse){
        StringBuilder sb = new StringBuilder();

        final Set<String> visited = new TreeSet<>();

        Queue<Node> BFSQueue = new ArrayDeque<>();
        BFSQueue.offer(start);

        while(!BFSQueue.isEmpty()){

            Node current = BFSQueue.remove();

            if(end != null && current.equals(end)){
                sb.append(String.format("%s", current.label));
                return sb.toString();
            }

            if(!visited.contains(current.label)){
                sb.append(String.format("%s ", current.label));
                visited.add(current.label);
            }

            Set<Node> nextNodes = inverse ? current.dependentUponList : current.dependsOnList;

            for (Node dependency : nextNodes) {
                BFSQueue.offer(dependency);
            }
        }

        //remove the trailing " "
        return sb.toString().trim();
    }

    private class Node {

        String label;
        Set<Node> dependsOnList;
        Set<Node> dependentUponList;

        public Node() {
            label = "";
            dependsOnList = new LinkedHashSet<>();
            dependentUponList = new LinkedHashSet<>();
        }

        public Node(String label) {
            this();
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node that = (Node) o;

            return label.equals(that.label);
        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }

    }
}
