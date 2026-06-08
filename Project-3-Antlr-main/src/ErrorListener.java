import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;

public class ErrorListener extends BaseErrorListener {
    private final String prefix;
    private boolean hasErrors = false;

    public ErrorListener(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                            int charPositionInLine, String msg, RecognitionException e) {
        hasErrors = true;
        System.out.printf("[%s] line %d:%d %s%n", prefix, line, charPositionInLine, msg);
    }

    public boolean hasErrors() {
        return hasErrors;
    }
}
