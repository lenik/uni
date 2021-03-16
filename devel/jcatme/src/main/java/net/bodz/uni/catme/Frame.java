package net.bodz.uni.catme;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.script.Invocable;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringPred;

public class Frame {

    static final int SKIP_TO_END = -1;

    final Frame parent;

    int echo;
    int skip;
    String skipToPattern;

    Map<String, Object> vars;
    Stack<FilterEntry> filters;

    public Frame(Frame parent) {
        this.parent = parent;
    }

    public Frame getParent() {
        return parent;
    }

    public int getEcho() {
        return echo;
    }

    public void setEcho(int echo) {
        this.echo = echo;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public void parseEcho(String echoSpec) {
        if (echoSpec.isEmpty()) {
            this.echo = 1;
            return;
        }
        if (StringPred.isDecimal(echoSpec)) {
            int n = Integer.parseInt(echoSpec);
            this.echo = n;
            return;
        }
    }

    public void parseSkip(String skipSpec) {
        if (skipSpec.isEmpty()) {
            skip = SKIP_TO_END;
            return;
        }
        StringTokenizer tokens = new StringTokenizer(skipSpec, "|");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            switch (token) {
            case "inf":
            case "eof":
                skip = SKIP_TO_END;
                continue;
            }
            if (StringPred.isDecimal(token)) {
                skip = Integer.parseInt(token);
                continue;
            }
            if (token.startsWith("\"") && token.endsWith("\"")) {
                // StringQuoted.split()
                String str = token.substring(1, token.length() - 1);
                skipToPattern = str; // TODO regex supports.
                continue;
            }
            throw new IllegalArgumentException("Invalid skip token: '" + token + "'");
        }
    }

    public synchronized boolean isVarDefined(String name) {
        if (vars != null)
            if (vars.containsKey(name))
                return true;
        if (parent != null)
            return parent.isVarDefined(name);
        return false;
    }

    public synchronized <T> T getVar(String name) {
        if (vars != null)
            if (vars.containsKey(name)) {
                @SuppressWarnings("unchecked")
                T value = (T) vars.get(name);
                return value;
            }
        if (parent != null)
            return parent.getVar(name);
        return null;
    }

    public synchronized void define(String name, Object value) {
        if (vars == null)
            vars = new HashMap<>();
        vars.put(name, value);
    }

    public void undefine(String name) {
        vars.remove(name);
    }

    public synchronized String filter(String s) {
        if (filters != null)
            for (FilterEntry entry : filters)
                s = entry.filter.filter(s);

        if (parent != null)
            s = parent.filter(s);

        return s;
    }

    public void beginFilter(ITextFilter filter, String key) {
        FilterEntry entry = new FilterEntry(key, filter);
        filters.push(entry);
    }

    public FilterEntry endFilter(String key) {
        if (filters.isEmpty())
            throw new IllegalStateException("No filter in use in the current frame.");
        FilterEntry top = filters.peek();
        if (!Nullables.equals(top.key, key))
            throw new IllegalArgumentException("Unmatched filter environment pair.");
        filters.pop();
        return top;
    }

    public void beginScriptFilter(Invocable invocable, String function) {
        ScriptFilter filter = new ScriptFilter(invocable, function);
        beginFilter(filter, null);
    }

}
