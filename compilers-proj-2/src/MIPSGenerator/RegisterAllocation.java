package MIPSGenerator;

import java.util.*;

public class RegisterAllocation {
    private Map<String, String> registerAssignment;
    private Set<String> spilledVariables;

    public RegisterAllocation() {
        this.registerAssignment = new HashMap<>();
        this.spilledVariables = new HashSet<>();
    }

    public void assignRegister(String variable, String register) {
        registerAssignment.put(variable, register);
        spilledVariables.remove(variable);
    }

    public void spillVariable(String variable) {
        spilledVariables.add(variable);
        registerAssignment.remove(variable);
    }

    public String getRegister(String variable) {
        return registerAssignment.get(variable);
    }

    public boolean isSpilled(String variable) {
        return spilledVariables.contains(variable);
    }

    public boolean hasRegister(String variable) {
        return registerAssignment.containsKey(variable);
    }

    public Set<String> getAssignedVariables() {
        return new HashSet<>(registerAssignment.keySet());
    }

    public Set<String> getSpilledVariables() {
        return new HashSet<>(spilledVariables);
    }

    public Set<String> getUsedRegisters() {
        return new HashSet<>(registerAssignment.values());
    }

    public String getVariableForRegister(String register) {
        for (Map.Entry<String, String> entry : registerAssignment.entrySet()) {
            if (entry.getValue().equals(register)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
