package net.bodz.lapiota.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.util.Map;
import java.util.regex.Pattern;

import org.codehaus.groovy.control.CompilationFailedException;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.java.util.regex.PatternProcessor;
import net.bodz.bas.c.string.StringEscape;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.io.BCharOut;

public class GroovyExpand
        extends PatternProcessor {

    private static Pattern gspTag;
    static {
        gspTag = Pattern.compile("<%((?://.*?\n | /\\*.*?\\*/ | .)*?)%>", Pattern.DOTALL | Pattern.COMMENTS);
    }

    private GroovyShell shell;
    private Binding binding;
    protected TextMap<Object> vars;
    private String script;

    public GroovyExpand() {
        super(gspTag);
    }

    public GroovyExpand(Map<String, ?> variables) {
        super(gspTag);
        this.vars = new HashTextMap<Object>(variables);
        this.binding = new Binding() {
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
            append(decl);
            break;
        case '=':
            String exp = t.substring(1).trim();
            append("out.print(" + exp + ");");
            break;
        default:
            String code = t;
            append(code);
        }
        append('\n');
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
        s = StringEscape.escapeJava(s);
        s = s.replace("$", "\\$"); // suppress in-quote $var expansion.
        if (newline)
            append("out.println(\"" + s + "\");\n");
        else
            append("out.print(\"" + s + "\");\n");
    }

    /**
     * Call {@link GroovyExpand#compileAndEvaluate(String)} instead.
     */
    @Override
    @Deprecated
    public final synchronized String process(String source) {
        return compileAndEvaluate(source);
    }

    /**
     * @exception CompilationFailedException
     *                If any groovy code in source has syntax errors. To check the compiled script,
     *                call {@link GroovyExpand#getCompiledScript()}.
     */
    public synchronized String compileAndEvaluate(String source)
            throws CompilationFailedException {
        shell = new GroovyShell(binding);

        BCharOut out = new BCharOut(source.length());
        shell.setVariable("out", out);

        script = super.process(source);
        shell.evaluate(script);

        return out.toString();
    }

    public String getCompiledScript() {
        return script;
    }

}
