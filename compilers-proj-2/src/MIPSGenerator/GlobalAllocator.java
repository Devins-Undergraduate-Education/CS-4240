package MIPSGenerator;

import java.util.*;
import ir.*;
import ir.operand.IRVariableOperand;

/**
 * Global Chaitin-Briggs style graph-coloring allocator across a function.
 */
public class GlobalAllocator {
    private static final List<String> AVAILABLE_REGS = Arrays.asList(
            "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7");

    private RegisterAllocation allocation;

    public RegisterAllocation allocate(CFG cfg, Map<String, IRVariableOperand> varMap) {
        Set<String> allocVars = new HashSet<>();
        for (String name : varMap.keySet()) {
            IRVariableOperand v = varMap.get(name);
            if (!(v.type instanceof ir.datatype.IRArrayType)) {
                allocVars.add(name);
            }
        }

        Map<BasicBlock, Set<String>> use = new HashMap<>();
        Map<BasicBlock, Set<String>> def = new HashMap<>();
        computeUseDef(cfg, allocVars, use, def);
        computeCFG_liveness(cfg, use, def);

        InterferenceGraph ig = buildInterferenceGraph(cfg, allocVars);
        Map<String, Integer> usageCount = countUsage(cfg, allocVars);

        allocation = chaitinBriggs(ig, usageCount);
        return allocation;
    }

    public RegisterAllocation getAllocation() {
        return allocation;
    }

    private void computeUseDef(CFG cfg, Set<String> allocVars, Map<BasicBlock, Set<String>> use,
            Map<BasicBlock, Set<String>> def) {
        for (BasicBlock bb : cfg.blocks) {
            Set<String> useSet = new HashSet<>();
            Set<String> defSet = new HashSet<>();
            for (IRInstruction inst : bb.getInstructions()) {
                List<String> uses = IRUtils.getUsedVarNames(inst);
                for (String u : uses) {
                    if (!allocVars.contains(u))
                        continue;
                    if (!defSet.contains(u))
                        useSet.add(u);
                }
                if (IRUtils.isDef(inst)) {
                    String d = IRUtils.getDefVarName(inst);
                    if (allocVars.contains(d))
                        defSet.add(d);
                }
            }
            use.put(bb, useSet);
            def.put(bb, defSet);
        }
    }

    private void computeCFG_liveness(CFG cfg, Map<BasicBlock, Set<String>> use,
            Map<BasicBlock, Set<String>> def) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = cfg.blocks.size() - 1; i >= 0; i--) {
                BasicBlock bb = cfg.blocks.get(i);
                Set<String> oldIn = new HashSet<>(bb.getLiveIn());
                Set<String> oldOut = new HashSet<>(bb.getLiveOut());

                Set<String> out = new HashSet<>();
                for (BasicBlock succ : bb.getSuccessors()) {
                    out.addAll(succ.getLiveIn());
                }
                Set<String> in = new HashSet<>(use.get(bb));
                Set<String> outMinusDef = new HashSet<>(out);
                outMinusDef.removeAll(def.get(bb));
                in.addAll(outMinusDef);

                bb.setLiveIn(in);
                bb.setLiveOut(out);
                if (!oldIn.equals(in) || !oldOut.equals(out))
                    changed = true;
            }
        }
    }

    private InterferenceGraph buildInterferenceGraph(CFG cfg, Set<String> allocVars) {
        InterferenceGraph ig = new InterferenceGraph();
        for (BasicBlock bb : cfg.blocks) {
            Set<String> live = new HashSet<>();
            live.addAll(bb.getLiveOut());
            List<IRInstruction> insts = bb.getInstructions();
            for (int i = insts.size() - 1; i >= 0; i--) {
                IRInstruction inst = insts.get(i);
                String def = null;
                if (IRUtils.isDef(inst)) {
                    def = IRUtils.getDefVarName(inst);
                }
                if (def != null && allocVars.contains(def)) {
                    ig.addNode(def);
                    for (String l : live) {
                        if (allocVars.contains(l))
                            ig.addEdge(def, l);
                    }
                    live.remove(def);
                }
                List<String> uses = IRUtils.getUsedVarNames(inst);
                for (String u : uses) {
                    if (allocVars.contains(u))
                        live.add(u);
                }
                // Add interference among all currently live variables (including simultaneous uses).
                List<String> liveList = new ArrayList<>();
                for (String v : live) {
                    if (allocVars.contains(v))
                        liveList.add(v);
                }
                for (int a = 0; a < liveList.size(); a++) {
                    for (int b = a + 1; b < liveList.size(); b++) {
                        ig.addEdge(liveList.get(a), liveList.get(b));
                    }
                }
            }
        }
        return ig;
    }

    private Map<String, Integer> countUsage(CFG cfg, Set<String> allocVars) {
        Map<String, Integer> usage = new HashMap<>();
        for (BasicBlock bb : cfg.blocks) {
            for (IRInstruction inst : bb.getInstructions()) {
                if (IRUtils.isDef(inst)) {
                    String def = IRUtils.getDefVarName(inst);
                    if (allocVars.contains(def))
                        usage.put(def, usage.getOrDefault(def, 0) + 1);
                }
                for (String u : IRUtils.getUsedVarNames(inst)) {
                    if (allocVars.contains(u))
                        usage.put(u, usage.getOrDefault(u, 0) + 1);
                }
            }
        }
        return usage;
    }

    private RegisterAllocation chaitinBriggs(InterferenceGraph ig, Map<String, Integer> usage) {
        int K = AVAILABLE_REGS.size();
        RegisterAllocation result = new RegisterAllocation();

        Map<String, Set<String>> adj = new HashMap<>();
        for (String v : ig.getAllVariables()) {
            adj.put(v, ig.getNeighbors(v));
        }

        Deque<String> stack = new ArrayDeque<>();
        Set<String> potentialSpills = new HashSet<>();

        Map<String, Integer> degree = new HashMap<>();
        for (String v : adj.keySet())
            degree.put(v, adj.get(v).size());

        while (!adj.isEmpty()) {
            Optional<String> lowDeg = adj.keySet().stream()
                    .filter(v -> degree.get(v) < K)
                    .findAny();
            if (lowDeg.isPresent()) {
                String n = lowDeg.get();
                pushAndRemove(n, adj, degree, stack);
            } else {
                String spill = chooseSpill(adj.keySet(), degree, usage);
                potentialSpills.add(spill);
                pushAndRemove(spill, adj, degree, stack);
            }
        }

        while (!stack.isEmpty()) {
            String v = stack.pop();
            Set<String> neighbors = ig.getNeighbors(v);
            Set<String> used = new HashSet<>();
            for (String n : neighbors) {
                String r = result.getRegister(n);
                if (r != null)
                    used.add(r);
            }
            String assigned = null;
            for (String r : AVAILABLE_REGS) {
                if (!used.contains(r)) {
                    assigned = r;
                    break;
                }
            }
            if (assigned == null) {
                result.spillVariable(v);
            } else {
                result.assignRegister(v, assigned);
            }
        }
        return result;
    }

    private void pushAndRemove(String node, Map<String, Set<String>> adj,
            Map<String, Integer> degree, Deque<String> stack) {
        stack.push(node);
        Set<String> neighbors = adj.remove(node);
        if (neighbors == null)
            return;
        for (String n : neighbors) {
            if (adj.containsKey(n)) {
                degree.put(n, degree.get(n) - 1);
                adj.get(n).remove(node);
            }
        }
        degree.remove(node);
    }

    private String chooseSpill(Set<String> nodes, Map<String, Integer> degree,
            Map<String, Integer> usage) {
        String best = null;
        double worstScore = -1;
        for (String n : nodes) {
            double d = degree.getOrDefault(n, 0);
            double u = usage.getOrDefault(n, 1);
            double score = d / u; // higher degree and lower use => higher score
            if (score > worstScore) {
                worstScore = score;
                best = n;
            }
        }
        return best != null ? best : nodes.iterator().next();
    }
}
