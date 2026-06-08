package MIPSGenerator;

import java.util.*;
import ir.*;

public class IntraBlockAllocator {
    private static final List<String> AVALABLE_REGISTERS = Arrays.asList(
            "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8", "$t9");

    private final int numRegisters;
    private Map<BasicBlock, RegisterAllocation> blockAllocations;

    public IntraBlockAllocator() {
        this.numRegisters = AVALABLE_REGISTERS.size();
        this.blockAllocations = new HashMap<>();
    }

    public void allocate(CFG cfg) {
        for (BasicBlock block : cfg.blocks) {
            block.computeUEVARAndVARKILL();
            computeIntraBlockLiveness(block);

            InterferenceGraph graph = buildInterferenceGraph(block);
            RegisterAllocation allocation = allocateRegistersGreedy(block, graph);

            blockAllocations.put(block, allocation);
        }
    }

    public RegisterAllocation getAllocation(BasicBlock block) {
        return blockAllocations.get(block);
    }

    private void computeIntraBlockLiveness(BasicBlock block) {
        Set<String> liveOut = new HashSet<>(block.getVARKILL());
        block.setLiveOut(liveOut);

        Set<String> liveIn = new HashSet<>(block.getUEVAR());
        Set<String> temp = new HashSet<>(liveOut);

        temp.removeAll(block.getVARKILL());
        liveIn.addAll(temp);
        block.setLiveIn(liveIn);
    }

    private InterferenceGraph buildInterferenceGraph(BasicBlock block) {
        InterferenceGraph graph = new InterferenceGraph();
        Set<String> currentlyLive = new HashSet<>(block.getLiveOut());

        List<IRInstruction> instructions = block.getInstructions();
        for (int i = instructions.size() - 1; i >= 0; i--) {
            IRInstruction inst = instructions.get(i);
            if (IRUtils.isDef(inst)) {
                String def = IRUtils.getDefVarName(inst);
                if (def != null) {
                    graph.addNode(def);
                    for (String live : currentlyLive) {
                        if (!def.equals(live)) {
                            graph.addEdge(def, live);
                        }
                    }
                    currentlyLive.remove(def);
                }
            }
            List<String> uses = IRUtils.getUsedVarNames(inst);
            for (String use : uses) {
                graph.addNode(use);
                currentlyLive.add(use);
            }
        }
        return graph;
    }

    private RegisterAllocation allocateRegistersGreedy(BasicBlock block, InterferenceGraph graph) {
        RegisterAllocation allocation = new RegisterAllocation();
        Map<String, Integer> usageCount = countVariableUsage(block);
        List<String> variables = new ArrayList<>(graph.getAllVariables());
        variables.sort((v1, v2) -> {

            int count1 = usageCount.getOrDefault(v1, 0);
            int count2 = usageCount.getOrDefault(v2, 0);
            if (count1 != count2) {
                return Integer.compare(count2, count1);
            }
            return Integer.compare(graph.getDegree(v2), graph.getDegree(v1));
        });
        for (String var : variables) {
            String assignedReg = findAvailableRegister(var, graph, allocation);
            if (assignedReg != null) {
                allocation.assignRegister(var, assignedReg);
            } else {
                allocation.spillVariable(var);
            }
        }
        return allocation;
    }

    private String findAvailableRegister(String variable, InterferenceGraph graph,
            RegisterAllocation allocation) {
        Set<String> neighbors = graph.getNeighbors(variable);
        Set<String> usedByNeighbors = new HashSet<>();

        for (String neighbor : neighbors) {
            String neighborReg = allocation.getRegister(neighbor);
            if (neighborReg != null) {
                usedByNeighbors.add(neighborReg);
            }
        }
        for (String reg : AVALABLE_REGISTERS) {
            if (!usedByNeighbors.contains(reg)) {
                return reg;
            }
        }
        return null;
    }

    private Map<String, Integer> countVariableUsage(BasicBlock block) {
        Map<String, Integer> usageCount = new HashMap<>();

        for (IRInstruction inst : block.getInstructions()) {
            for (String var : IRUtils.getUsedVarNames(inst)) {
                usageCount.put(var, usageCount.getOrDefault(var, 0) + 1);
            }
            if (IRUtils.isDef(inst)) {
                String defVar = IRUtils.getDefVarName(inst);
                if (defVar != null) {
                    usageCount.put(defVar, usageCount.getOrDefault(defVar, 0) + 1);
                }
            }
        }
        return usageCount;
    }
}
