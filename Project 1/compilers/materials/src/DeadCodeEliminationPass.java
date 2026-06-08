import ir.*;
import java.util.*;

/**
 * DCE with Reaching Definitions using BasicBlock/CFG.
 * Steps:
 *  1) Build CFG blocks
 *  2) Compute block GEN/KILL (per variable, only last def in block is GEN; KILL = all other defs of vars defined in block)
 *  3) Forward dataflow on blocks to get IN/OUT sets
 *  4) Within each block, compute instruction-level IN by simulating transfer through the block from IN[block]
 *  5) Mark essential roots (control/side effects), then backwards-mark via reaching defs at each essential use
 *  6) Remove dead, side-effect-free definitions
 */
class DeadCodeEliminationPass {

    static void run(IRFunction f) {
        // build cfg
        CFG cfg = CFGBuilder.build(f);
        List<BasicBlock> blocks = cfg.blocks;

        // flat list and index maps
        List<IRInstruction> all = f.instructions;
        int n = all.size();
        if (n == 0) return;

        // defs
        List<Integer> defIdxs = new ArrayList<>();
        Map<String, List<Integer>> varToDefs = new HashMap<>();
        for (int i = 0; i < n; i++) {
            IRInstruction in = all.get(i);
            if (IRUtils.isDef(in)) {
                defIdxs.add(i);
                String v = IRUtils.getDefVarName(in);
                varToDefs.computeIfAbsent(v, k -> new ArrayList<>()).add(i);
            }
        }
        int D = defIdxs.size();
        Map<Integer,Integer> defIndexToBit = new HashMap<>();
        for (int k = 0; k < D; k++) defIndexToBit.put(defIdxs.get(k), k);

        // mask bits for each var's defs
        Map<String, BitSet> varMask = new HashMap<>();
        for (Map.Entry<String, List<Integer>> e : varToDefs.entrySet()) {
            BitSet m = new BitSet(D);
            for (int di : e.getValue()) {
                Integer b = defIndexToBit.get(di);
                if (b != null) m.set(b);
            }
            varMask.put(e.getKey(), m);
        }

        // block-level GEN/KILL
        Map<BasicBlock, BitSet> GENb = new HashMap<>();
        Map<BasicBlock, BitSet> KILLb = new HashMap<>();

        for (BasicBlock b : blocks) {
            BitSet GEN = new BitSet(D);
            BitSet KILL = new BitSet(D);

            // last def per var in this block
            Map<String,Integer> lastDef = new HashMap<>();
            Set<String> varsDefined = new HashSet<>();

            for (IRInstruction insn : b.getInstructions()) {
                if (IRUtils.isDef(insn)) {
                    String v = IRUtils.getDefVarName(insn);
                    varsDefined.add(v);
                    // record last definition index within this block
                    int globalIdx = all.indexOf(insn);
                    lastDef.put(v, globalIdx);
                }
            }

            for (String v : varsDefined) {
                // GEN: only the last def of v in this block
                Integer last = lastDef.get(v);
                if (last != null) {
                    Integer bit = defIndexToBit.get(last);
                    if (bit != null) GEN.set(bit);
                }
                // KILL: all defs of v not equal to that last def
                BitSet mask = (BitSet) varMask.getOrDefault(v, new BitSet()).clone();
                if (last != null) {
                    Integer bit = defIndexToBit.get(last);
                    if (bit != null) mask.clear(bit);
                }
                KILL.or(mask);
            }

            GENb.put(b, GEN);
            KILLb.put(b, KILL);
        }

        // forward over blocks
        Map<BasicBlock, BitSet> INb  = new HashMap<>();
        Map<BasicBlock, BitSet> OUTb = new HashMap<>();
        for (BasicBlock b : blocks) { INb.put(b, new BitSet(D)); OUTb.put(b, new BitSet(D)); }

        ArrayDeque<BasicBlock> wl = new ArrayDeque<>(blocks);
        while (!wl.isEmpty()) {
            BasicBlock b = wl.poll();

            // IN[b] = union OUT[p] for predecessors
            BitSet newIN = new BitSet(D);
            for (BasicBlock p : b.getPredecessors()) newIN.or(OUTb.get(p));

            BitSet newOUT = (BitSet) newIN.clone();
            newOUT.andNot(KILLb.get(b));
            newOUT.or(GENb.get(b));

            if (!newIN.equals(INb.get(b)) || !newOUT.equals(OUTb.get(b))) {
                INb.put(b, newIN);
                OUTb.put(b, newOUT);
                for (BasicBlock s : b.getSuccessors()) wl.add(s);
            }
        }

        // for each instruction index, store its IN set.
        BitSet[] INi = new BitSet[n];
        for (int i = 0; i < n; i++) INi[i] = new BitSet(D);

        for (BasicBlock b : blocks) {
            BitSet cur = (BitSet) INb.get(b).clone();
            for (IRInstruction insn : b.getInstructions()) {
                int idx = all.indexOf(insn);
                INi[idx] = (BitSet) cur.clone();

                // per-instruction transfer
                if (IRUtils.isDef(insn)) {
                    String v = IRUtils.getDefVarName(insn);
                    // kill all defs of v
                    BitSet mask = varMask.getOrDefault(v, new BitSet());
                    cur.andNot(mask);
                    // gen this def
                    Integer bit = defIndexToBit.get(idx);
                    if (bit != null) cur.set(bit);
                }
            }
        }

        // backward marking
        boolean[] keep = new boolean[n];
        ArrayDeque<Integer> Q = new ArrayDeque<>();

        // enqueue keep
        Runnable roots = () -> {
            for (int i = 0; i < n; i++) {
                IRInstruction insn = all.get(i);
                if (IRUtils.hasSideEffectsOrControl(insn)) {
                    if (!keep[i]) { keep[i] = true; Q.add(i); }
                }
            }
        };

        roots.run();

        while (!Q.isEmpty()) {
            int i = Q.poll();
            IRInstruction insn = all.get(i);

            // for every variable used, mark its reaching defs
            for (String v : IRUtils.getUsedVarNames(insn)) {
                BitSet reaching = (BitSet) INi[i].clone();
                reaching.and(varMask.getOrDefault(v, new BitSet()));
                for (int b = reaching.nextSetBit(0); b >= 0; b = reaching.nextSetBit(b + 1)) {
                    int defGlobalIdx = defIdxs.get(b);
                    if (!keep[defGlobalIdx]) { keep[defGlobalIdx] = true; Q.add(defGlobalIdx); }
                }
            }
        }

        // REBUILD: keep all control/side-effects + marked defs
        List<IRInstruction> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            IRInstruction insn = all.get(i);
            if (IRUtils.hasSideEffectsOrControl(insn)) {
                out.add(insn);
            } else if (IRUtils.isDef(insn)) {
                if (keep[i]) out.add(insn);
            } else {
                out.add(insn);
            }
        }
        f.instructions = out;
    }
}
