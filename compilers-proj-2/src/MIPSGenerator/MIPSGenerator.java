package MIPSGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ir.*;
import ir.datatype.*;
import ir.operand.*;

public class MIPSGenerator {
    private int stackSize;
    private int totalStackSize;
    private String functionName;

    private String allocationStrategy;
    private Map<String, Integer> variableOffsets;
    private Map<String, Boolean> isArrayParameter;

    private Map<String, Boolean> isLocalArray;
    private List<String> dataSection;
    private List<String> textSection;
    private CFG cfg;

    private IntraBlockAllocator allocator;
    private GlobalAllocator globalAllocator;
    private BasicBlock currentBlock;
    private Set<String> loadedInCurrentBlock;
    private RegisterAllocation globalAllocation;
    private List<String> savedRegisters;
    private Map<String, IRVariableOperand> variableMap;

    public MIPSGenerator(IRFunction function) {
        this(function, "naive");
    }

    public MIPSGenerator(IRFunction function, String strategy) {
        this.functionName = function.name;
        this.allocationStrategy = strategy;
        this.variableOffsets = new HashMap<>();
        this.isArrayParameter = new HashMap<>();

        this.isLocalArray = new HashMap<>();
        this.dataSection = new ArrayList<>();
        this.textSection = new ArrayList<>();
        this.savedRegisters = new ArrayList<>();
        this.variableMap = new HashMap<>();

        this.stackSize = 0;
        this.currentBlock = null;
        this.loadedInCurrentBlock = new HashSet<>();
        this.totalStackSize = stackSize;

        dataSection.add(".data");
        dataSection.add("newline: .asciiz \"\\n\"");
        calculateStackOffsets(function);
    }

    private void calculateStackOffsets(IRFunction function) {
        int offset = 8;
        for (IRVariableOperand param : function.parameters) {
            String varName = param.getName();
            variableMap.put(varName, param);
            variableOffsets.put(varName, offset);

            boolean isArray = param.type instanceof IRArrayType;
            isArrayParameter.put(varName, isArray);

            isLocalArray.put(varName, false);
            offset += 4;
        }
        for (IRVariableOperand var : function.variables) {
            String varName = var.getName();
            variableMap.put(varName, var);
            variableOffsets.put(varName, offset);
            isArrayParameter.put(varName, false);
            boolean isArray = var.type instanceof IRArrayType;

            isLocalArray.put(varName, isArray);
            if (isArray) {
                IRArrayType arrayType = (IRArrayType) var.type;
                offset += arrayType.getSize() * 4;
            } else {
                offset += 4;
            }
        }

        this.stackSize = offset;
        this.totalStackSize = this.stackSize;
    }

    private int getVariablOffset(String varName) {
        if (!variableOffsets.containsKey(varName)) {

            throw new RuntimeException("Variable " + varName + " not found in offset map");
        }
        return variableOffsets.get(varName);
    }

    private String getAllocatedRegister(String varName) {
        if (allocationStrategy.equals("greedy")) {
            if (currentBlock == null || allocator == null)
                return null;
            RegisterAllocation allocation = allocator.getAllocation(currentBlock);
            if (allocation == null)
                return null;
            return allocation.getRegister(varName);
        } else if (allocationStrategy.equals("color")) {
            if (globalAllocation == null)
                return null;
            return globalAllocation.getRegister(varName);
        }
        return null;
    }

    private String loadOperand(IROperand operand, String defaultReg) {
        if (operand instanceof IRConstantOperand) {
            textSection.add("\tli " + defaultReg + ", " + operand.toString());
            return defaultReg;
        }
        String varName = operand.toString();
        String allocatedReg = getAllocatedRegister(varName);
        if (allocatedReg != null) {
            if (!loadedInCurrentBlock.contains(varName)) {
                textSection.add("\tlw " + allocatedReg + ", -" + getVariablOffset(varName) + "($fp)");
                loadedInCurrentBlock.add(varName);
            }
            return allocatedReg;
        }
        textSection.add("\tlw " + defaultReg + ", -" + getVariablOffset(varName) + "($fp)");
        return defaultReg;
    }

    private void storeVariable(String varName, String sourceReg) {
        String allocatedReg = getAllocatedRegister(varName);
        String regToStore = sourceReg;
        if (allocatedReg != null) {
            if (!allocatedReg.equals(sourceReg)) {
                textSection.add("\tmove " + allocatedReg + ", " + sourceReg);
            }
            loadedInCurrentBlock.add(varName);
            regToStore = allocatedReg;
        }
        textSection.add("\tsw " + regToStore + ", -" + getVariablOffset(varName) + "($fp)");
    }

    private void loadArgumentToRegister(IROperand arg, String targetReg) {
        if (arg instanceof IRConstantOperand) {
            textSection.add("\tli " + targetReg + ", " + arg.toString());
            return;
        }
        String argName = arg.toString();
        int offset = getVariablOffset(argName);
        if (isArrayParameter.getOrDefault(argName, false)) {
            textSection.add("\tlw " + targetReg + ", -" + offset + "($fp)");
        } else if (isLocalArray.getOrDefault(argName, false)) {
            textSection.add("\taddi " + targetReg + ", $fp, -" + offset);
        } else {
            String loadedReg = loadOperand(arg, targetReg);
            if (!loadedReg.equals(targetReg)) {
                textSection.add("\tmove " + targetReg + ", " + loadedReg);
            }
        }
    }

    private void loadBlockLiveIns(BasicBlock block) {
        if (!allocationStrategy.equals("color") || globalAllocation == null)
            return;
        // We no longer eagerly load live-ins; loads happen lazily on first use.
    }

    public void generateMIPS(IRFunction function) {
        if (allocationStrategy.equals("greedy")) {

            cfg = CFGBuilder.build(function);
            allocator = new IntraBlockAllocator();
            allocator.allocate(cfg);
        } else if (allocationStrategy.equals("color")) {
            cfg = CFGBuilder.build(function);
            globalAllocator = new GlobalAllocator();
            globalAllocation = globalAllocator.allocate(cfg, variableMap);
            savedRegisters = new ArrayList<>(globalAllocation.getUsedRegisters());
            totalStackSize = stackSize + savedRegisters.size() * 4;
        }

        mipsInit(function);
        if (allocationStrategy.equals("greedy") || allocationStrategy.equals("color")) {
            for (BasicBlock block : cfg.blocks) {
                currentBlock = block;
                loadedInCurrentBlock.clear();
                for (IRInstruction instruction : block.getInstructions()) {
                    translateInstruction(instruction);
                }
                loadedInCurrentBlock.clear();
            }
        } else {
            for (IRInstruction instruction : function.instructions) {
                translateInstruction(instruction);
            }
        }

        if (function.returnType == null) {
            if (functionName.equals("main")) {
                textSection.add("\tj __exit");
            } else {
                mipsEnd();
            }
        }
    }

    private void mipsInit(IRFunction function) {
        textSection.add("\n.text");
        textSection.add(".globl " + functionName);
        textSection.add(functionName + ":");
        textSection.add("\taddi $sp, $sp, -8");
        textSection.add("\tsw $ra, 4($sp)");
        textSection.add("\tsw $fp, 0($sp)");
        textSection.add("\tmove $fp, $sp");
        textSection.add("\taddi $sp, $sp, -" + totalStackSize);

        for (int i = 0; i < savedRegisters.size(); i++) {
            String reg = savedRegisters.get(i);
            int offset = stackSize + (i + 1) * 4;
            textSection.add("\tsw " + reg + ", -" + offset + "($fp)");
        }

        String[] argRegs = { "$a0", "$a1", "$a2", "$a3" };
        int numParams = function.parameters.size();
        for (int i = 0; i < numParams; i++) {
            IRVariableOperand param = function.parameters.get(i);
            String paramName = param.getName();
            int offset = getVariablOffset(paramName);
            if (i < 4) {
                textSection.add("\tsw " + argRegs[i] + ", -" + offset + "($fp)");
            } else {
                int stackPos = 8 + (i - 4) * 4;
                textSection.add("\tlw $t0, " + stackPos + "($fp)");
                textSection.add("\tsw $t0, -" + offset + "($fp)");
            }
        }
    }

    private void mipsEnd() {
        for (int i = 0; i < savedRegisters.size(); i++) {
            String reg = savedRegisters.get(i);
            int offset = stackSize + (i + 1) * 4;
            textSection.add("\tlw " + reg + ", -" + offset + "($fp)");
        }
        textSection.add("\tmove $sp, $fp");
        textSection.add("\tlw $fp, 0($sp)");
        textSection.add("\tlw $ra, 4($sp)");
        textSection.add("\taddi $sp, $sp, 8");
        textSection.add("\tjr $ra");
    }

    private void translateInstruction(IRInstruction instruction) {
        IRInstruction.OpCode opCode = instruction.opCode;
        IROperand[] operands = instruction.operands;
        switch (opCode) {
            case LABEL:
                translateLabel(operands);
                break;
            case ASSIGN:
                translateAssign(operands);
                break;

            case ADD:

                translateBinaryOp(operands, "add");
                break;
            case SUB:
                translateBinaryOp(operands, "sub");
                break;

            case MULT:

                translateMult(operands);
                break;
            case DIV:
                translateDiv(operands);
                break;
            case AND:

                translateBinaryOp(operands, "and");
                break;
            case OR:
                translateBinaryOp(operands, "or");
                break;
            case GOTO:
                translateGoto(operands);
                break;
            case BREQ:

                translateBranch(operands, "beq");
                break;
            case BRNEQ:
                translateBranch(operands, "bne");

                break;
            case BRLT:
                translateBranch(operands, "blt");
                break;
            case BRGT:
                translateBranch(operands, "bgt");
                break;
            case BRGEQ:
                translateBranch(operands, "bge");

                break;
            case ARRAY_LOAD:
                translateArrayLoad(operands);
                break;
            case ARRAY_STORE:
                translateArrayStore(operands);
                break;
            case CALL:
                translateCall(operands, false);
                break;
            case CALLR:
                translateCall(operands, true);
                break;
            case RETURN:
                translateReturn(operands);
                break;
            default:
                break;
        }
    }

    private void translateLabel(IROperand[] operands) {
        String labelName = operands[0].toString();
        String uniqueLabel = functionName + "_" + labelName;
        textSection.add(uniqueLabel + ":");
    }

    private void translateAssign(IROperand[] operands) {
        String dest = operands[0].toString();
        IROperand src = operands[1];
        String srcReg = loadOperand(src, "$t0");
        storeVariable(dest, srcReg);
    }

    private void translateBinaryOp(IROperand[] operands, String mipsOp) {
        String res = operands[0].toString();
        IROperand op1 = operands[1];
        IROperand op2 = operands[2];

        String reg1 = loadOperand(op1, "$t0");
        String reg2 = loadOperand(op2, "$t1");
        String resReg = getAllocatedRegister(res);
        String computReg = (resReg != null) ? resReg : "$t2";
        textSection.add("\t" + mipsOp + " " + computReg + ", " + reg1 + ", " + reg2);
        storeVariable(res, computReg);
    }

    private void translateMult(IROperand[] operands) {
        String res = operands[0].toString();
        IROperand op1 = operands[1];
        IROperand op2 = operands[2];
        String reg1 = loadOperand(op1, "$t0");
        String reg2 = loadOperand(op2, "$t1");

        textSection.add("\tmult " + reg1 + ", " + reg2);
        String resReg = getAllocatedRegister(res);
        String computeReg = (resReg != null) ? resReg : "$t2";

        textSection.add("\tmflo " + computeReg);
        storeVariable(res, computeReg);
    }

    private void translateDiv(IROperand[] operands) {
        String res = operands[0].toString();
        IROperand op1 = operands[1];
        IROperand op2 = operands[2];
        String reg1 = loadOperand(op1, "$t0");
        String reg2 = loadOperand(op2, "$t1");

        textSection.add("\tdiv " + reg1 + ", " + reg2);
        String resReg = getAllocatedRegister(res);
        String compReg = (resReg != null) ? resReg : "$t2";

        textSection.add("\tmflo " + compReg);
        storeVariable(res, compReg);
    }

    private void translateGoto(IROperand[] operands) {
        String label = operands[0].toString();
        String uniqueLabel = functionName + "_" + label;
        textSection.add("\tj " + uniqueLabel);
    }

    private void translateBranch(IROperand[] operands, String mipsBranch) {
        String label = operands[0].toString();
        IROperand op1 = operands[1];
        IROperand op2 = operands[2];

        String reg1 = loadOperand(op1, "$t0");
        String reg2 = loadOperand(op2, "$t1");

        String uniqueLabel = functionName + "_" + label;
        textSection.add("\t" + mipsBranch + " " + reg1 + ", " + reg2 + ", " + uniqueLabel);
    }

    private void translateArrayLoad(IROperand[] operands) {
        String dest = operands[0].toString();
        String arrayName = operands[1].toString();
        IROperand index = operands[2];

        int arrayOffset = getVariablOffset(arrayName);
        String indexReg = loadOperand(index, "$t0");
        textSection.add("\tsll $t0, " + indexReg + ", 2");

        if (isArrayParameter.getOrDefault(arrayName, false)) {
            textSection.add("\tlw $t1, -" + arrayOffset + "($fp)");
        } else {
            textSection.add("\taddi $t1, $fp, -" + arrayOffset);
        }

        textSection.add("\tadd $t0, $t0, $t1");

        textSection.add("\tlw $t2, 0($t0)");
        storeVariable(dest, "$t2");
    }

    private void translateArrayStore(IROperand[] operands) {
        IROperand value = operands[0];
        String arrayName = operands[1].toString();
        IROperand index = operands[2];
        int arrayOffset = getVariablOffset(arrayName);

        String valueReg = loadOperand(value, "$t2");
        String indexReg = loadOperand(index, "$t0");

        textSection.add("\tsll $t0, " + indexReg + ", 2");
        if (isArrayParameter.getOrDefault(arrayName, false)) {
            textSection.add("\tlw $t1, -" + arrayOffset + "($fp)");
        } else {
            textSection.add("\taddi $t1, $fp, -" + arrayOffset);
        }
        textSection.add("\tadd $t0, $t0, $t1");
        textSection.add("\tsw " + valueReg + ", 0($t0)");
    }

    private void translateCall(IROperand[] operands, boolean hasReturnValue) {
        int startIdx = hasReturnValue ? 1 : 0;
        String funcName = operands[startIdx].toString();

        if (isIntrinsicFunction(funcName)) {
            translateIntrinsicCall(operands, funcName, hasReturnValue);
            return;
        }
        int argStartIdx = startIdx + 1;
        int numArgs = operands.length - argStartIdx;
        String[] argRegs = { "$a0", "$a1", "$a2", "$a3" };
        int stackArgCount = Math.max(0, numArgs - argRegs.length);
        int stackBytes = stackArgCount * 4;

        if (stackBytes > 0) {
            textSection.add("\taddi $sp, $sp, -" + stackBytes);
        }

        for (int i = 0; i < numArgs; i++) {
            IROperand arg = operands[argStartIdx + i];
            if (i < argRegs.length) {
                loadArgumentToRegister(arg, argRegs[i]);
            } else {
                loadArgumentToRegister(arg, "$t0");
                int offset = (i - argRegs.length) * 4;
                textSection.add("\tsw $t0, " + offset + "($sp)");
            }
        }
        textSection.add("\tjal " + funcName);
        if (!allocationStrategy.equals("color")) {
            loadedInCurrentBlock.clear();
        }

        if (stackBytes > 0) {
            textSection.add("\taddi $sp, $sp, " + stackBytes);
        }
        if (hasReturnValue) {
            String res = operands[0].toString();
            storeVariable(res, "$v0");
        }
    }

    private boolean isIntrinsicFunction(String funcName) {
        return funcName.equals("geti") || funcName.equals("getf") || funcName.equals("getc") ||

                funcName.equals("puti") || funcName.equals("putf") || funcName.equals("putc");
    }

    private void translateIntrinsicCall(IROperand[] operands, String funcName, boolean hasReturnValue) {
        switch (funcName) {
            case "geti":
                textSection.add("\tli $v0, 5");
                textSection.add("\tsyscall");
                if (hasReturnValue) {
                    String res = operands[0].toString();
                    storeVariable(res, "$v0");
                }
                break;
            case "getf":
                textSection.add("\tli $v0, 6");
                textSection.add("\tsyscall");
                if (hasReturnValue) {
                    String resf = operands[0].toString();
                    storeVariable(resf, "$v0");
                }
                break;
            case "getc":
                textSection.add("\tli $v0, 12");
                textSection.add("\tsyscall");
                if (hasReturnValue) {
                    String res = operands[0].toString();
                    storeVariable(res, "$v0");
                }
                break;
            case "puti":
                IROperand arg = operands[1];
                String argReg = loadOperand(arg, "$a0");
                if (!argReg.equals("$a0")) {
                    textSection.add("\tmove $a0, " + argReg);

                }
                textSection.add("\tli $v0, 1");
                textSection.add("\tsyscall");

                break;
            case "putf":
                IROperand fArg = operands[1];
                String fArgReg = loadOperand(fArg, "$f12");
                if (!fArgReg.equals("$f12")) {
                    textSection.add("\tmove $f12, " + fArgReg);
                }
                textSection.add("\tli $v0, 2");
                textSection.add("\tsyscall");
                break;
            case "putc":
                IROperand charArg = operands[1];
                String charReg = loadOperand(charArg, "$a0");

                if (!charReg.equals("$a0")) {
                    textSection.add("\tmove $a0, " + charReg);
                }
                textSection.add("\tli $v0, 11");
                textSection.add("\tsyscall");
                break;
        }
    }

    private void translateReturn(IROperand[] operands) {
        if (operands.length > 0) {
            IROperand returnValue = operands[0];
            String reg = loadOperand(returnValue, "$v0");
            if (!reg.equals("$v0")) {
                textSection.add("\tmove $v0, " + reg);
            }
        }
        mipsEnd();
    }

    public String getMIPSCode() {
        StringBuilder sb = new StringBuilder();

        for (String line : dataSection) {
            sb.append(line).append("\n");
        }
        sb.append("\n");
        for (String line : textSection) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public List<String> getTextSection() {
        return textSection;
    }

    public List<String> getDataSection() {
        return dataSection;
    }
}
