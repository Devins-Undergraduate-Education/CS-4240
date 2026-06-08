import java.util.*;

import ir.*;

public class BasicBlock {

    private int id;
    // private List<Integer> leaders;
    private List<IRInstruction> instructions;
    private List<BasicBlock> successors;
    private List<BasicBlock> predecessors;

    public BasicBlock(int id) {
        this.id = id;
        // this.leaders = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
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

}
