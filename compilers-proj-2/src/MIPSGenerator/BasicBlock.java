package MIPSGenerator;

import java.util.*;
import ir.*;

public class BasicBlock {
    private int id;
    private List<IRInstruction> instructions;
    private List<BasicBlock> successors;
    private List<BasicBlock> predecessors;
    private Set<String> uevar;
    private Set<String> varkill;
    private Set<String> liveIn;
    private Set<String> liveOut;
    private Set<String> def;
    private Set<String> use;

    public BasicBlock(int id) {
        this.id = id;
        this.instructions = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
        this.uevar = new HashSet<>();
        this.varkill = new HashSet<>();
        this.liveIn = new HashSet<>();
        this.liveOut = new HashSet<>();
        this.def = new HashSet<>();
        this.use = new HashSet<>();
    }

    public void addInstuction(IRInstruction inst) {
        instructions.add(inst);
    }

    public void addSuccessor(BasicBlock succ) {
        successors.add(succ);
    }

    public void addPredecessor(BasicBlock pre) {
        predecessors.add(pre);
    }

    public int getId() {
        return this.id;
    }

    public List<IRInstruction> getInstructions() {
        return this.instructions;
    }

    public IRInstruction getLastInstruction() {
        if (instructions.isEmpty())
            return null;
        return instructions.get(instructions.size() - 1);
    }

    public List<BasicBlock> getSuccessors() {
        return this.successors;
    }

    public List<BasicBlock> getPredecessors() {
        return this.predecessors;
    }

    public Set<String> getUEVAR() {
        return this.uevar;
    }

    public Set<String> getVARKILL() {
        return this.varkill;
    }

    public Set<String> getLiveIn() {
        return this.liveIn;
    }

    public Set<String> getLiveOut() {
        return this.liveOut;
    }

    public void setLiveIn(Set<String> liveIn) {
        this.liveIn = liveIn;
    }

    public void setLiveOut(Set<String> liveOut) {
        this.liveOut = liveOut;
    }

    public Set<String> getDef() {
        return def;
    }

    public Set<String> getUse() {
        return use;
    }

    public void setDef(Set<String> def) {
        this.def = def;
    }

    public void setUse(Set<String> use) {
        this.use = use;
    }

    public void computeUEVARAndVARKILL() {
        uevar.clear();
        varkill.clear();
        Set<String> defined = new HashSet<>();
        for (IRInstruction inst : instructions) {
            List<String> uses = IRUtils.getUsedVarNames(inst);
            for (String var : uses) {
                if (!defined.contains(var)) {
                    uevar.add(var);
                }
            }
            if (IRUtils.isDef(inst)) {
                String defVar = IRUtils.getDefVarName(inst);
                if (defVar != null) {
                    varkill.add(defVar);
                    defined.add(defVar);
                }
            }
        }
    }
}
