import ir.*;
import java.util.*;

public class CFGBuilder {

    public static CFG build(IRFunction f) {
        CFG cfg = new CFG(f);
        List<IRInstruction> insns = f.instructions;
        int n = insns.size();
        if (n == 0) {
            BasicBlock bb = new BasicBlock(0);
            cfg.addBlock(bb);
            cfg.entry = bb;
            cfg.exit = bb;
            return cfg;
        }

        // Collect label -> index + leader set
        Map<String,Integer> labelToIndex = new HashMap<>();
        boolean[] leader = new boolean[n];
        leader[0] = true;

        for (int i = 0; i < n; i++) {
            IRInstruction in = insns.get(i);
            if (IRUtils.isLabel(in) && in.operands.length >= 1) {
                labelToIndex.put(in.operands[0].toString(), i);
                leader[i] = true;
            }
        }

        for (int i = 0; i < n; i++) {
            IRInstruction in = insns.get(i);
            if (IRUtils.isBranch(in) || IRUtils.isGoto(in) || IRUtils.isReturn(in)) {
                if (i + 1 < n) leader[i + 1] = true; // fallthrough leader
                if (IRUtils.isBranch(in) || IRUtils.isGoto(in)) {
                    if (in.operands.length >= 1) {
                        Integer tgt = labelToIndex.get(in.operands[0].toString());
                        if (tgt != null) leader[tgt] = true;
                    }
                }
            }
        }

        // Create blocks
        List<Integer> leaders = new ArrayList<>();
        for (int i = 0; i < n; i++) if (leader[i]) leaders.add(i);
        // add an "end"
        leaders.add(n);

        List<BasicBlock> blocks = new ArrayList<>();
        Map<Integer,BasicBlock> indexToBlock = new HashMap<>();

        for (int b = 0; b < leaders.size() - 1; b++) {
            int start = leaders.get(b);
            int end = leaders.get(b + 1);

            BasicBlock bb = new BasicBlock(b);
            for (int i = start; i < end; i++) {
                bb.addInstuction(insns.get(i));
                indexToBlock.put(i, bb);
            }
            cfg.addBlock(bb);
            blocks.add(bb);
        }

        cfg.entry = blocks.get(0);
        cfg.exit  = blocks.get(blocks.size() - 1);

        // add edges
        for (int b = 0; b < blocks.size(); b++) {
            BasicBlock bb = blocks.get(b);
            IRInstruction last = bb.getLastInstruction();
            if (last == null) {
                if (b + 1 < blocks.size()) cfg.addEdge(bb, blocks.get(b + 1));
                continue;
            }

            if (IRUtils.isGoto(last)) {
                if (last.operands.length >= 1) {
                    Integer tgt = labelToIndex.get(last.operands[0].toString());
                    if (tgt != null) cfg.addEdge(bb, indexToBlock.get(tgt));
                }
            } else if (IRUtils.isBranch(last)) {
                // target
                if (last.operands.length >= 1) {
                    Integer tgt = labelToIndex.get(last.operands[0].toString());
                    if (tgt != null) cfg.addEdge(bb, indexToBlock.get(tgt));
                }
                // fallthrough
                if (b + 1 < blocks.size()) cfg.addEdge(bb, blocks.get(b + 1));
            } else if (IRUtils.isReturn(last)) {
                // no edges
            } else {
                if (b + 1 < blocks.size()) cfg.addEdge(bb, blocks.get(b + 1));
            }
        }

        return cfg;
    }
}
