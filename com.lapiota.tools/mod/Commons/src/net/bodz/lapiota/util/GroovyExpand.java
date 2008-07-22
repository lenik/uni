package net.bodz.lapiota.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.bodz.bas.io.CharOuts;
import net.bodz.bas.io.CharOuts.Buffer;
import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.bas.text.interp.PatternProcessor;

public class GroovyExpand extends PatternProcessor {

    private static Pattern gspTag;
    static {
        gspTag = Pattern.compile("<%(//.*?\n | /\\*.*?\\*/ | .)*?%>",
                Pattern.DOTALL | Pattern.COMMENTS);
    }

    private GroovyShell    shell;
    private Binding        binding;

    public GroovyExpand() {
        super(gspTag);
    }

    public GroovyExpand(Map<String, ?> map) {
        super(gspTag);
        Map<String, Object> mapCopy = new HashMap<String, Object>(map);
        this.binding = new Binding(mapCopy);
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
            String exp = t.substring(1);
            println("out.print(" + exp + ")");
            break;
        default:
            String code = t.substring(1);
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
        s = s.replaceAll("\\", "\\\\");
        s = s.replaceAll("\"", "\\\"");
        s = s.replaceAll("\'", "\\\'");
        if (newline)
            println("out.println(\"" + s + "\")");
        else
            println("out.print(\"" + s + "\")");
    }

    @Override
    public synchronized String process(String source) {
        shell = new GroovyShell(binding);

        StringBuffer contents = new StringBuffer(source.length());
        Buffer out = CharOuts.get(contents);
        shell.setVariable("out", out);

        String script = super.process(source);
        shell.evaluate(script);

        return contents.toString();
    }

}
