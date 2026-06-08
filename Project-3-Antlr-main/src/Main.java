import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: run.sh <source.tiger>");
            System.exit(1);
        }

        CharStream input = CharStreams.fromFileName(args[0]);

        tigerLexer lexer = new tigerLexer(input);
        ErrorListener lexerListener = new ErrorListener("LexicalError");
        lexer.removeErrorListeners();
        lexer.addErrorListener(lexerListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        tigerParser parser = new tigerParser(tokens);
        ErrorListener parserListener = new ErrorListener("SyntacticError");
        parser.removeErrorListeners();
        parser.addErrorListener(parserListener);

        parser.tiger_program();

        if (!lexerListener.hasErrors() && !parserListener.hasErrors()) {
            System.out.println("successful");
        }
    }
}
