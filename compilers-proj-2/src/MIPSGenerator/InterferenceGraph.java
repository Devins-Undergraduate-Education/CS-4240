package MIPSGenerator;

import java.util.*;

public class InterferenceGraph {
    private Map<String, Set<String>> adjecencyList;
    private Map<String, Integer> degree;

    public InterferenceGraph() {
        this.adjecencyList = new HashMap<>();
        this.degree = new HashMap<>();
    }

    public void addNode(String variable) {
        if (!adjecencyList.containsKey(variable)) {
            adjecencyList.put(variable, new HashSet<>());
            degree.put(variable, 0);
        }
    }

    public void addEdge(String var1, String var2) {
        if (var1.equals(var2)) {
            return;
        }
        addNode(var1);
        addNode(var2);
        if (!adjecencyList.get(var1).contains(var2)) {
            adjecencyList.get(var1).add(var2);
            adjecencyList.get(var2).add(var1);
            degree.put(var1, degree.get(var1) + 1);
            degree.put(var2, degree.get(var2) + 1);
        }
    }

    public Set<String> getNeighbors(String variable) {
        if (!adjecencyList.containsKey(variable)) {
            return new HashSet<>();
        }
        return new HashSet<>(adjecencyList.get(variable));
    }

    public int getDegree(String variable) {
        return degree.getOrDefault(variable, 0);
    }

    public Set<String> getAllVariables() {
        return new HashSet<>(adjecencyList.keySet());
    }

    public boolean interferes(String var1, String var2) {
        if (!adjecencyList.containsKey(var1)) {
            return false;
        }
        return adjecencyList.get(var1).contains(var2);
    }

    public int size() {
        return adjecencyList.size();
    }
}
