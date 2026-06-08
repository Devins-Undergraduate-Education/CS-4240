import ir.*;
import ir.operand.*;
import java.util.*;

class IRUtils {

    static String op(IRInstruction insn) {
        return insn.opCode.name(); // e.g., "ADD", "ASSIGN", "ARRAY_STORE", "BRLT", "GOTO", "CALL", "LABEL", ...
    }

    static boolean isBranch(IRInstruction insn) {
        String s = op(insn);
        return s.startsWith("BR"); // BRLT, BRGT, BRGEQ, BRLEQ, BRNEQ, etc.
    }

    static boolean isGoto(IRInstruction insn) {
        return "GOTO".equals(op(insn));
    }

    static boolean isLabel(IRInstruction insn) {
        return "LABEL".equals(op(insn));
    }

    static boolean isCall(IRInstruction insn) {
        return "CALL".equals(op(insn));
    }

    static boolean isReturn(IRInstruction insn) {
        String s = op(insn);
        return "RET".equals(s) || "RETURN".equals(s);
    }

    static boolean isArrayStore(IRInstruction insn) {
        return "ARRAY_STORE".equals(op(insn));
    }

    static boolean hasSideEffectsOrControl(IRInstruction insn) {
        return isBranch(insn) || isGoto(insn) || isLabel(insn) || isCall(insn) || isReturn(insn) || isArrayStore(insn);
    }

    /** True if instruction defines a scalar variable in operands[0] */
    static boolean isDef(IRInstruction insn) {
        if (hasSideEffectsOrControl(insn)) return false;
        if (insn.operands == null || insn.operands.length == 0) return false;
        return (insn.operands[0] instanceof IRVariableOperand);
    }

    /** destination variable name */
    static String getDefVarName(IRInstruction insn) {
        if (!isDef(insn)) return null;
        return ((IRVariableOperand) insn.operands[0]).getName();
    }

    /** variable uses in an instruction (by name). For defs, skip operand[0] */
    static List<String> getUsedVarNames(IRInstruction insn) {
        List<String> used = new ArrayList<>();
        int start = isDef(insn) ? 1 : 0;

        // for branches/goto, operand[0] is a label operand, not a variable
        if (isBranch(insn) || isGoto(insn)) {
            start = 1;
        }

        for (int i = start; i < insn.operands.length; i++) {
            IROperand op = insn.operands[i];
            if (op instanceof IRVariableOperand) {
                used.add(((IRVariableOperand) op).getName());
            }
        }
        return used;
    }

    /** Replace any variable operand in 'insn' named 'from' with the provided IRVariableOperand 'toVar' */
    static void replaceOperandByName(IRInstruction insn, String from, IRVariableOperand toVar) {
        int start = 0;
        if (isDef(insn)) start = 1; // don't replace dest
        if (isBranch(insn) || isGoto(insn)) start = 1; // skip label in operand[0]

        for (int i = start; i < insn.operands.length; i++) {
            IROperand op = insn.operands[i];
            if (op instanceof IRVariableOperand) {
                IRVariableOperand v = (IRVariableOperand) op;
                if (v.getName().equals(from)) {
                    insn.operands[i] = toVar;
                }
            }
        }
    }
}
