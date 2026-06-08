package MIPSGenerator;

import java.util.ArrayList;
import java.util.List;
import ir.IRFunction;

public class CFG {
    public IRFunction function;
    public List<BasicBlock> blocks = new ArrayList<>();
    public BasicBlock entry;
    public BasicBlock exit;

    public CFG(IRFunction f) {
        this.function = f;
    }

    public void addBlock(BasicBlock bb) {
        blocks.add(bb);
    }

    public void addEdge(BasicBlock a, BasicBlock b) {
        a.addSuccessor(b);
        b.addPredecessor(a);
    }
}
