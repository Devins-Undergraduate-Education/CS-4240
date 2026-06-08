import ir.*;
import java.io.PrintStream;

/**
 * Main entry: TigerOptimizer <input.ir> <output.ir>
 *
 * Pipeline:
 *   - Parse program
 *   - Dead Code Elimination using Reaching Definitions (keeps all control/branch/call/store)
 *   - Print optimized program
 */
public class TigerOptimizer {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: TigerOptimizer <input.ir> <output.ir>");
            System.exit(1);
        }
        String inPath = args[0];
        String outPath = args[1];

        IRReader reader = new IRReader();
        IRProgram program = reader.parseIRFile(inPath);

        // DCE via Reaching Definitions (no branch removal)
        for (IRFunction f : program.functions) {
            DeadCodeEliminationPass.run(f);
        }

        IRPrinter printer = new IRPrinter(new PrintStream(outPath));
        printer.printProgram(program);
    }
}
