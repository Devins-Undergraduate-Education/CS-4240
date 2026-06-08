package MIPSGenerator;

import ir.*;
import ir.operand.*;
import java.util.*;

public class IRUtils {
    static String op(IRInstruction insn) {
        return insn.opCode.name();
    }

    static boolean isBranch(IRInstruction insn) {
        String s = op(insn);
        return s.startsWith("BR");
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

    static boolean isDef(IRInstruction insn) {
        if (hasSideEffectsOrControl(insn))
            return false;
        if (insn.operands == null || insn.operands.length == 0)
            return false;
        return (insn.operands[0] instanceof IRVariableOperand);
    }

    static String getDefVarName(IRInstruction insn) {
        if (!isDef(insn))
            return null;
        return ((IRVariableOperand) insn.operands[0]).getName();
    }

    static List<String> getUsedVarNames(IRInstruction insn) {
        List<String> used = new ArrayList<>();
        int start;
        if (isDef(insn)) {
            start = 1;
        } else {
            start = 0;
        }
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

    static void replaceOperandByName(IRInstruction insn, String from, IRVariableOperand toVar) {
        int start = 0;
        if (isDef(insn))
            start = 1;
        if (isBranch(insn) || isGoto(insn))
            start = 1;
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
