package implementation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Graph {
    private final int objCount;

    private final List<List<Integer>> adjArray;

    /**
     * Create graph by object count.
     * @param objectCount Object count.
     */
    public Graph(int objectCount) {
        this.objCount = objectCount;
        adjArray = new ArrayList<>(objectCount);
        for (int i = 0; i < objectCount; i++) {
            adjArray.add(new LinkedList<>());
        }
    }

    /**
     * Recursive function.
     * @param index index.
     * @param addedObejct Visited.
     * @param recursiveFields Recursive check.
     * @return is Cyclic.
     */
    private boolean isCyclicUtil(int index, boolean[] addedObejct, boolean[] recursiveFields) {

        if (recursiveFields[index]) {
            return recursiveFields[index];
        }
        if (addedObejct[index]) {
            return false;
        }
        addedObejct[index] = true;
        recursiveFields[index] = true;
        List<Integer> children = adjArray.get(index);
        for (Integer c : children) {
            if (isCyclicUtil(c, addedObejct, recursiveFields)) {
                return true;
            }
        }
        recursiveFields[index] = false;
        return false;
    }

    /**
     * Add edge.
     * @param parent Parent object.
     * @param child Children object.
     */
    public void addEdge(int parent, int child) {
        adjArray.get(parent).add(child);
    }

    /**
     * Is Cyclic the data structure.
     * @return Is cyclic?
     */
    public boolean isCyclic() {
        boolean[] visited = new boolean[objCount];
        boolean[] recStack = new boolean[objCount];
        for (int i = 0; i < objCount; i++) {
            if (isCyclicUtil(i, visited, recStack)) {
                return true;
            }
        }
        return false;
    }
}



