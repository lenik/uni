package net.bodz.uni.catme.js;

import java.io.IOException;
import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.FilterEntry;
import net.bodz.uni.catme.FilterException;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;
import net.bodz.uni.catme.ITextFilterClass;
import net.bodz.uni.catme.cmd.AbstractCommand;
import net.bodz.uni.catme.lex.ITokenLexer;

/**
 * Example:
 *
 * <pre>
 * -- \map return x.toupper()
 * ...
 * -- \end
 * </pre>
 */
public class FilterDefCommand
        extends AbstractCommand {

    public FilterDefCommand() {
        super(CAPTURE);
    }

    public FilterDefCommand(ITokenLexer<?>... lexers) {
        super(lexers);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        String code = StringArray.join(" ", args);

        FilterEntry entry = new FilterEntry("<code>", new MapFilter(code));
        frame.getFilterStack().add(entry);
    }

    public static final FilterDefCommand MAP = new FilterDefCommand();

}

class MapFilter
        implements
            ITextFilterClass,
            ITextFilter {

    String code;
    Value compiled;

    public MapFilter(String code) {
        this.code = code;
    }

    @Override
    public boolean isScript() {
        return true;
    }

    @Override
    public ITextFilter createFilter(IFrame frame, List<String> args) {
        IScriptContext scriptContext = frame.getParser().getScriptContext();
        String expr2Fn = "(function (x) { " + code + " })";
        try {
            Object ret = scriptContext.eval(expr2Fn);
            if (ret == null)
                throw new RuntimeException("can't eval.");
            compiled = (Value) ret;
        } catch (EvalException | IOException e) {
            throw new IllegalArgumentException("Uncompilable code.", e);
        }
        return this;
    }

    @Override
    public ITextFilterClass getFilterClass() {
        return this;
    }

    @Override
    public void filter(IFrame frame, StringBuilder in, Appendable out)
            throws IOException, FilterException {
        if (compiled == null)
            throw new NullPointerException("compiled");
        String x = in.toString();
        try {
            Value result = compiled.execute(x);
            Object y = ValueFn.convert(result);
            String yStr = String.valueOf(y);
            out.append(yStr);
        } catch (Exception e) {
            throw new FilterException(e);
        }
    }

}