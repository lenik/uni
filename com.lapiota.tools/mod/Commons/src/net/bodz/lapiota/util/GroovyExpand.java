package net.bodz.lapiota.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.util.Map;
import java.util.regex.Pattern;

import net.bodz.bas.io.CharOuts;
import net.bodz.bas.io.CharOuts.Buffer;
import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.bas.text.interp.PatternProcessor;
import net.bodz.bas.types.TextMap;
import net.bodz.bas.types.TextMap.HashTextMap;
import net.bodz.bas.types.util.Strings;

import org.codehaus.groovy.control.CompilationFailedException;

public class GroovyExpand extends PatternProcessor {

    private static Pattern    gspTag;
    static {
        gspTag = Pattern.compile("<%((?://.*?\n | /\\*.*?\\*/ | .)*?)%>",
                Pattern.DOTALL | Pattern.COMMENTS);
    }

    private GroovyShell       shell;
    private Binding           binding;
    protected TextMap<Object> vars;
    private String            script;

    public GroovyExpand() {
        super(gspTag);
    }

    public GroovyExpand(Map<String, ?> variables) {
        super(gspTag);
        this.vars = new HashTextMap<Object>(variables);
        this.binding = new Binding() {
            @SuppressWarnings("unchecked")
            @Override
            public Map getVariables() {
                return vars;
            }

            @Override
            public Object getVariable(String name) {
                return get(name);
            }

            @Override
            public void setVariable(String name, Object value) {
                set(name, value);
            }
        };
    }

    protected Object get(String name) {
        Object value = vars.get(name);
        if (value == null && !vars.containsKey(name))
            throw new MissingPropertyException(name, Binding.class);
        return value;
    }

    public void set(String name, Object value) {
        vars.put(name, value);
    }

    @Override
    protected void matched(int start, int end) {
        String t = matcher.group(1).trim();
        if (t.isEmpty())
            return;
        switch (t.charAt(0)) {
        case '@':
            throw new NotImplementedException("<%@ ... %> isn't supported");
        case '!':
            String decl = t.substring(1);
            println(decl);
            break;
        case '=':
            String exp = t.substring(1).trim();
            println("out.print(" + exp + ");");
            break;
        default:
            String code = t;
            println(code);
        }
    }

    @Override
    protected void unmatched(String s) {
        String[] lines = s.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == lines.length - 1) {
                if (!line.isEmpty())
                    echo(line, false);
            } else {
                echo(line, true);
            }
        }
    }

    void echo(String s, boolean newline) {
        s = Strings.escape(s);
        s = s.replace("$", "\\$"); // suppress in-quote $var expansion.
        if (newline)
            println("out.println(\"" + s + "\");");
        else
            println("out.print(\"" + s + "\");");
    }

    /**
     * Call {@link GroovyExpand#compileAndEvaluate(String)} instead.
     */
    @Override
    @Deprecated
    public final String process(String source) {
        return compileAndEvaluate(source);
    }

    /**
     * @exception CompilationFailedException
     *                If any groovy code in source has syntax errors. To check
     *                the compiled script, call
     *                {@link GroovyExpand#getCompiledScript()}.
     */
    public synchronized String compileAndEvaluate(String source)
            throws CompilationFailedException {
        shell = new GroovyShell(binding);

        StringBuffer contents = new StringBuffer(source.length());
        Buffer out = CharOuts.get(contents);
        shell.setVariable("out", out);

        script = super.process(source);
        shell.evaluate(script);

        return contents.toString();
    }

    public String getCompiledScript() {
        return script;
    }

}
