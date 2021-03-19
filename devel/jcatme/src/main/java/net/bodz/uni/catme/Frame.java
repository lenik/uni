package net.bodz.uni.catme;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.script.Invocable;

import org.graalvm.polyglot.HostAccess.Export;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringPred;

public class Frame {

    static final int SKIP_TO_END = -1;

    final Frame parent;

    int echo;
    int skip;
    String skipToPattern;

    Map<String, Command> commands;
    Map<String, Object> vars;
    Stack<FilterEntry> filters;

    public Frame(Frame parent) {
        this.parent = parent;
    }

    @Export
    public Frame getParent() {
        return parent;
    }

    @Export
    public int getEcho() {
        return echo;
    }

    @Export
    public void setEcho(int echo) {
        this.echo = echo;
    }

    @Export
    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    @Export
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

    @Export
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

    @Export
    public synchronized boolean isCommandDefined(String name) {
        if (commands != null)
            if (commands.containsKey(name))
                return true;
        if (parent != null)
            return parent.isCommandDefined(name);
        return false;
    }

    @Export
    public synchronized Command getCommand(String name) {
        if (commands != null)
            if (commands.containsKey(name)) {
                Command value = commands.get(name);
                return value;
            }
        if (parent != null)
            return parent.getCommand(name);
        return null;
    }

    @Export
    public synchronized void register(Command command) {
        String name = command.getName();
        if (commands == null)
            commands = new HashMap<>();
        commands.put(name, command);
    }

    @Export
    public synchronized void unregister(String command) {
        if (commands != null)
            commands.remove(command);
    }

    @Export
    public synchronized boolean isVarDefined(String name) {
        if (vars != null)
            if (vars.containsKey(name))
                return true;
        if (parent != null)
            return parent.isVarDefined(name);
        return false;
    }

    @Export
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

    @Export
    public synchronized void define(String name, Object value) {
        if (vars == null)
            vars = new HashMap<>();
        vars.put(name, value);
    }

    @Export
    public void undefine(String name) {
        vars.remove(name);
    }

    @Export
    public synchronized String filter(String s) {
        if (filters != null)
            for (FilterEntry entry : filters)
                s = entry.filter.filter(s);

        if (parent != null)
            s = parent.filter(s);

        return s;
    }

    @Export
    public void beginFilter(ITextFilter filter, String key) {
        FilterEntry entry = new FilterEntry(key, filter);
        filters.push(entry);
    }

    @Export
    public FilterEntry endFilter(String key) {
        if (filters.isEmpty())
            throw new IllegalStateException("No filter in use in the current frame.");
        FilterEntry top = filters.peek();
        if (!Nullables.equals(top.key, key))
            throw new IllegalArgumentException("Unmatched filter environment pair.");
        filters.pop();
        return top;
    }

    @Export
    public void beginScriptFilter(Invocable invocable, String function) {
        ScriptFilter filter = new ScriptFilter(invocable, function);
        beginFilter(filter, null);
    }

}
