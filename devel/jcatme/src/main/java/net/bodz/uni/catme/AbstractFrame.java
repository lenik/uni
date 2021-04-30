package net.bodz.uni.catme;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.uni.catme.io.ResourceVariant;

public abstract class AbstractFrame
        implements
            IMutableFrame {

    public static final int TO_END = -1;

    protected final IFrame parent;
    protected final int parentLine;
    protected final int parentColumn;

    int currentLine;
    int currentColumn;

    Boolean removeLeads;
    static boolean defaultRemoveLeads = true;
    String indenter = "";

    int echoLines;
    int skipLines;
    Pattern skipToPattern;

    List<String> arguments = new ArrayList<>();
    Map<String, String> parameters = new LinkedHashMap<>();

    Map<String, ICommand> commands = new LinkedHashMap<>();
    Map<String, ITextFilterClass> filterClasses = new LinkedHashMap<>();

    Map<String, Object> vars = new LinkedHashMap<>();
    Set<String> importSet;
    Stack<FilterEntry> filterStack = new Stack<>();

    MainParser parser;

    public AbstractFrame(IFrame parent, MainParser parser) {
        this.parent = parent;
        if (parent != null) {
            this.parentLine = parent.getCurrentLine();
            this.parentColumn = parent.getCurrentColumn();
        } else {
            this.parentLine = 0;
            this.parentColumn = 0;
        }
        this.parser = parser;
    }

    @Override
    public IFrame getParent() {
        return parent;
    }

    @Override
    public int getParentLine() {
        return parentLine;
    }

    @Override
    public int getParentColumn() {
        return parentColumn;
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
    public int getCurrentLine() {
        return currentLine;
    }

    @Override
    public int getCurrentColumn() {
        return currentColumn;
    }

    @Override
    public void setLocation(int line, int column) {
        this.currentLine = line;
        this.currentColumn = column;
    }

    @Override
    public boolean isRemoveLeads() {
        if (removeLeads == null)
            if (parent == null)
                return parser.app.removeLeads;
            else
                return parent.isRemoveLeads();
        else
            return removeLeads.booleanValue();
    }

    @Override
    public void setRemoveLeads(boolean removeLeads) {
        this.removeLeads = removeLeads;
    }

    @Override
    public String getIndenter() {
        return indenter;
    }

    @Override
    public void setIndenter(String indenter) {
        this.indenter = indenter;
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
        if (filterClasses.containsKey(name))
            return true;
        if (parent != null)
            return parent.isFilterDefined(name);
        return false;
    }

    @Override
    public ITextFilterClass getFilter(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (filterClasses.containsKey(name))
            return filterClasses.get(name);
        if (parent != null)
            return parent.getFilter(name);
        return null;
    }

    @Override
    public void addFilter(String name, ITextFilterClass filterClass) {
        if (name == null)
            throw new NullPointerException("name");
        if (filterClass == null)
            throw new NullPointerException("filterClass");
        ITextFilterClass prev = getFilter(name);
        if (prev != null)
            throw new IllegalArgumentException("filter-class is already defined: " + name);
        filterClasses.put(name, filterClass);
    }

    @Override
    public void removeFilter(String name) {
        if (name == null)
            throw new NullPointerException("name");
        if (!filterClasses.containsKey(name))
            throw new IllegalArgumentException("filter-class isn't defined: " + name);
        filterClasses.remove(name);
    }

    @Override
    public Map<String, Object> getLocalVarMap() {
        return vars;
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
        IFrame frame = findVar(name);
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

    protected void allocateImportSet() {
        importSet = new HashSet<>();
    }

    @Override
    public synchronized boolean isImported(String qName) {
        if (importSet != null)
            if (importSet.contains(qName))
                return true;
        if (parent != null)
            if (parent.isImported(qName))
                return true;
        return false;
    }

    @Override
    public synchronized boolean addImported(String qName) {
        if (importSet != null)
            return importSet.add(qName);

        if (parent != null)
            return parent.addImported(qName);

        throw new UnsupportedOperationException("Can't import in an abstract frame.");
    }

    @Override
    public boolean removeImported(String qName) {
        if (importSet != null)
            return importSet.remove(qName);

        if (parent != null)
            return parent.removeImported(qName);

        return false;
    }

    @Override
    public boolean isFilterInUse(String name) {
        if (name == null)
            throw new NullPointerException("name");
        for (FilterEntry filter : filterStack)
            if (name.equals(filter.name))
                return true;
        return false;
    }

    @Override
    public Stack<FilterEntry> getFilterStack() {
        return filterStack;
    }

    @Override
    public void beginFilter(String name, String... args) {
        beginFilter(name, Arrays.asList(args));
    }

    @Override
    public void beginFilter(String name, List<String> args) {
        ITextFilterClass filterClass = getFilter(name);
        ITextFilter filter = filterClass.createFilter(this, args);
        FilterEntry filterEntry = new FilterEntry(name, filter);
        filterStack.push(filterEntry);
    }

    @Override
    public FilterEntry endFilter(String name)
            throws FilterException {
        if (filterStack.isEmpty())
            throw new IllegalStateException("No filter in use in the current frame.");
        FilterEntry top = filterStack.peek();
        if (!Nullables.equals(top.name, name))
            throw new IllegalArgumentException("Unmatched filter environment pair.");
        filterStack.pop();
        return top;
    }

    @Override
    public String filter(String s)
            throws FilterException {
        StringBuilder in = new StringBuilder(s);
        StringBuilder out = new StringBuilder(s.length());
        fastFilter(this, in, out);
        return out.toString();
    }

    @Override
    public StringBuilder fastFilter(IFrame caller, StringBuilder in, StringBuilder out)
            throws FilterException {
        if (filterStack != null)
            for (FilterEntry entry : filterStack) {
                try {
                    entry.filter.filter(caller, in, out);
                } catch (FilterException e) {
                    throw e;
                } catch (Exception e) {
                    String type = entry.filter.getClass().getSimpleName();
                    String name = type + " " + entry.name;
                    throw new FilterException("Filter(" + name + ") error: " + e.getMessage(), e);
                }
                StringBuilder _in = in;
                in.setLength(0);
                in = out;
                out = _in;
            }
        if (parent != null)
            return parent.fastFilter(caller, in, out);
        else
            return in;
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
    public Object eval(String code)
            throws EvalException, IOException {
        if (parent != null)
            return parent.eval(code);
        else
            throw new NotImplementedException();
    }

    @Override
    public Object eval(String code, String fileName)
            throws EvalException, IOException {
        if (parent != null)
            return parent.eval(code, fileName);
        else
            throw new NotImplementedException();
    }

}
