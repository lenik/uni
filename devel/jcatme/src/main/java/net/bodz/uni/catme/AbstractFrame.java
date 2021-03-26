package net.bodz.uni.catme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.uni.catme.io.ResourceVariant;

public abstract class AbstractFrame
        implements
            IFrame {

    public static final int TO_END = -1;

    protected final IFrame parent;

    int echoLines;
    int skipLines;
    Pattern skipToPattern;

    List<String> arguments = new ArrayList<>();
    Map<String, String> parameters = new LinkedHashMap<>();

    Map<String, ICommand> commands = new LinkedHashMap<>();
    Map<String, ITextFilter> filters = new LinkedHashMap<>();

    Map<String, Object> vars = new LinkedHashMap<>();
    Stack<FilterEntry> filterStack = new Stack<>();

    MainParser parser;

    public AbstractFrame(IFrame parent, MainParser parser) {
        this.parent = parent;
        this.parser = parser;
    }

    @Override
    public IFrame getParent() {
        return parent;
    }

    @Override
    public FileFrame getClosestFileFrame() {
        if (isFile())
            return (FileFrame) this;
        if (parent != null)
            return parent.getClosestFileFrame();
        return null;
    }

    @Override
    public int getDepth() {
        int depth = 1;
        if (parent != null)
            depth += parent.getDepth();
        return depth;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public int getEchoLines() {
        return echoLines;
    }

    @Override
    public void setEchoLines(int echoLines) {
        this.echoLines = echoLines;
    }

    @Override
    public int getSkipLines() {
        return skipLines;
    }

    @Override
    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }

    @Override
    public Pattern getSkipToPattern() {
        return skipToPattern;
    }

    @Override
    public void setSkipToPattern(Pattern skipToPattern) {
        this.skipToPattern = skipToPattern;
    }

    public void parseEcho(String echoSpec) {
        if (echoSpec.isEmpty()) {
            this.echoLines = 1;
            return;
        }
        if (StringPred.isDecimal(echoSpec)) {
            int n = Integer.parseInt(echoSpec);
            this.echoLines = n;
            return;
        }
    }

    public void parseSkip(String skipSpec) {
        if (skipSpec.isEmpty()) {
            skipLines = TO_END;
            return;
        }
        StringTokenizer tokens = new StringTokenizer(skipSpec, "|");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            switch (token) {
            case "inf":
            case "eof":
                skipLines = TO_END;
                continue;
            }
            if (StringPred.isDecimal(token)) {
                skipLines = Integer.parseInt(token);
                continue;
            }
            if (token.startsWith("\"") && token.endsWith("\"")) {
                // StringQuoted.split()
                String str = token.substring(1, token.length() - 1);
                skipToPattern = Pattern.compile(str); // TODO regex supports.
                continue;
            }
            throw new IllegalArgumentException("Invalid skip token: '" + token + "'");
        }
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean isCommandDefined(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (commands.containsKey(name))
            return true;
        if (parent != null)
            return parent.isCommandDefined(name);
        return false;
    }

    @Override
    public ICommand getCommand(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (commands.containsKey(name))
            return commands.get(name);
        if (parent != null)
            return parent.getCommand(name);
        return null;
    }

    @Override
    public void addCommand(String name, ICommand command) {
        if (command == null)
            throw new NullPointerException("command");
        ICommand prev = getCommand(name);
        if (prev != null)
            throw new IllegalArgumentException("command is already defined: " + name);
        commands.put(name, command);
    }

    @Override
    public synchronized void removeCommand(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (!commands.containsKey(name))
            throw new IllegalArgumentException("command isn't defined: " + name);
        commands.remove(name);
    }

    @Override
    public boolean isFilterDefined(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (filters.containsKey(name))
            return true;
        if (parent != null)
            return parent.isFilterDefined(name);
        return false;
    }

    @Override
    public ITextFilter getFilter(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (filters.containsKey(name))
            return filters.get(name);
        if (parent != null)
            return parent.getFilter(name);
        return null;
    }

    @Override
    public void addFilter(String name, ITextFilter filter) {
        if (name == null)
            throw new NullPointerException("name");
        if (filter == null)
            throw new NullPointerException("filter");
        ITextFilter prev = getFilter(name);
        if (prev != null)
            throw new IllegalArgumentException("filter is already defined: " + name);
        filters.put(name, filter);
    }

    @Override
    public void removeFilter(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (!filters.containsKey(name))
            throw new IllegalArgumentException("filter isn't defined: " + name);
        filters.remove(name);
    }

    @Override
    public synchronized boolean isVarDefined(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (vars.containsKey(name))
            return true;
        if (parent != null)
            return parent.isVarDefined(name);
        return false;
    }

    public IFrame findVar(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (vars.containsKey(name))
            return this;
        if (parent != null)
            return parent.findVar(name);
        return null;
    }

    @Override
    public synchronized Object getVar(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (vars.containsKey(name)) {
            Object value = vars.get(name);
            return value;
        }
        if (parent != null)
            return parent.getVar(name);
        return null;
    }

    @Override
    public synchronized void putVar(String name, Object value) {
        if (name == null)
            throw new NullPointerException("name");
        IFrame frame = parent.findVar(name);
        if (frame == this || frame == null)
            vars.put(name, value);
        else
            frame.putVar(name, value);
    }

    @Override
    public void removeVar(String name) {
        if (name == null)
            throw new NullPointerException("name");
        vars.remove(name);
    }

    @Override
    public boolean isFilterInUse(String name) {
        for (FilterEntry filter : filterStack)
            if (name.equals(filter.key))
                return true;
        return false;
    }

    @Override
    public Stack<FilterEntry> getFilterStack() {
        return filterStack;
    }

    public void beginFilter(ITextFilter filter, String key) {
        FilterEntry entry = new FilterEntry(key, filter);
        filterStack.push(entry);
    }

    public FilterEntry endFilter(String key) {
        if (filterStack.isEmpty())
            throw new IllegalStateException("No filter in use in the current frame.");
        FilterEntry top = filterStack.peek();
        if (!Nullables.equals(top.key, key))
            throw new IllegalArgumentException("Unmatched filter environment pair.");
        filterStack.pop();
        return top;
    }

    @Override
    public String filter(String s)
            throws EvalException {
        if (filterStack != null)
            for (FilterEntry entry : filterStack)
                s = entry.filter.filter(s);

        if (parent != null)
            s = parent.filter(s);

        return s;
    }

    @Override
    public MainParser getParser() {
        return parser;
    }

    @Override
    public ResourceVariant resolveHref(String href)
            throws IOException {
        if (href == null)
            throw new NullPointerException("href");
        if (parent != null)
            return parent.resolveHref(href);
        return null;
    }

    @Override
    public ResourceVariant resolveQName(String qName)
            throws IOException {
        if (parent != null)
            return parent.resolveQName(qName);
        return null;
    }

    @Override
    public ResourceVariant resolveModule(String module)
            throws IOException {
        if (parent != null)
            return parent.resolveModule(module);
        return null;
    }

    public FileFrame createChildFrame(ResourceVariant res)
            throws IOException {
        if (res == null)
            throw new NullPointerException("res");
        return new FileFrame(this, parser, res.file);
    }

    @Override
    public void parse(String href)
            throws IOException, ParseException {
        if (parent != null)
            parent.parse(href);
    }

    @Override
    public void processComments(String s, int textStart, int textEnd, boolean multiLine)
            throws IOException, ParseException {
        String text = s.substring(textStart, textEnd);

        FileFrame ff = getClosestFileFrame();
        boolean special = text.trim().startsWith(ff.escapePrefix);

        if (special) {
            if (echoLines != 0) {
                parser.out.print(s);
                if (echoLines != -1)
                    echoLines--;
            }
            parser.parseInstruction(this, text);
        } else {
            parser.out.print(s);
        }
    }

    @Override
    public void processText(String s)
            throws IOException {
        parser.out.print(s);
    }

}
