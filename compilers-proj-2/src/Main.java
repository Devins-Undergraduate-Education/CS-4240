import ir.*;
import MIPSGenerator.MIPSGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java Main <input.ir> <out.s> [naive|greedy|color]");
            System.exit(1);
        }

        String inputfilePath = args[0];
        String outputFilePath = args[1];
        String alocationStrategy = (args.length >= 3) ? args[2] : "naive";
        if (!alocationStrategy.equals("naive") && !alocationStrategy.equals("greedy")
                && !alocationStrategy.equals("color")) {
            System.err.println("Allocation strategy must be naive, greedy, or color");
            System.exit(1);
        }

        File outputFile = new File(outputFilePath);
        
        try {
            if (outputFile.createNewFile()) {
                System.out.println("File created successfully: " + outputFile.getName());
            } else {
                System.out.println("File already exists or could not be created.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file: " + e.getMessage());
        }

        File inputFile = new File(inputfilePath);
        if (!inputFile.exists() || !inputFile.canRead()) {
            System.err.println("cant read: " + inputfilePath);
            System.exit(1);
        }

        Path inputPath = Paths.get(inputfilePath);
        Path dir = inputPath.getParent();
        //String outputFilePath = (dir != null) ? dir.resolve("out.s").toString() : "out.s";
        
        try {
            IRReader reader = new IRReader();
            IRProgram program = reader.parseIRFile(inputfilePath);
            String mipsCode = generateMIPSCode(program, alocationStrategy);
            writeOutputFile(outputFilePath, mipsCode);
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String generateMIPSCode(IRProgram program, String strategy) {
        StringBuilder mipCode = new StringBuilder();

        mipCode.append(".data\n");
        mipCode.append("newline: .asciiz \"\\n\"\n");
        mipCode.append("\n");

        for (IRFunction function : program.functions) {
            MIPSGenerator generator = new MIPSGenerator(function, strategy);
            generator.generateMIPS(function);

            for (String line : generator.getTextSection()) {
                mipCode.append(line).append("\n");
            }
            mipCode.append("\n");
        }

        if (hasMainFunction(program)) {
            mipCode.append("\n");
            mipCode.append("__exit:\n");
            mipCode.append("\tli $v0, 10\n");
            mipCode.append("\tsyscall\n");
        }

        return mipCode.toString();
    }

    private static boolean hasMainFunction(IRProgram program) {
        for (IRFunction function : program.functions) {
            if (function.name.equals("main")) {
                return true;
            }
        }
        return false;
    }

    private static void writeOutputFile(String outputPath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(content);
        }
    }
}
