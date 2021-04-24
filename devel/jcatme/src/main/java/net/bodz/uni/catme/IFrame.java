package net.bodz.uni.catme;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.uni.catme.io.ResourceVariant;

public interface IFrame {

    String VAR_FRAME = "Frame";

    IFrame getParent();

    int getParentLine();

    int getParentColumn();

    FileFrame getClosestFileFrame();

    int getDepth();

    boolean isFile();

    int getCurrentLine();

    int getCurrentColumn();

    boolean isRemoveLeads();

    void setRemoveLeads(boolean removeLeads);

    String getIndenter();

    void setIndenter(String indenter);

    int getEchoLines();

    void setEchoLines(int echoLines);

    int getSkipLines();

    void setSkipLines(int skipLines);

    Pattern getSkipToPattern();

    void setSkipToPattern(Pattern pattern);

    List<String> getArguments();

    Map<String, String> getParameters();

    boolean isCommandDefined(String name);

    ICommand getCommand(String name);

    void addCommand(String name, ICommand command);

    void removeCommand(String name);

    Map<String, Object> getLocalVarMap();

    boolean isVarDefined(String name);

    Object getVar(String name);

    IFrame findVar(String name);

    void putVar(String name, Object value);

    void removeVar(String name);

    boolean isImported(String qName);

    boolean addImported(String qName);

    boolean removeImported(String qName);

    boolean isFilterDefined(String name);

    ITextFilterClass getFilter(String name);

    void addFilter(String name, ITextFilterClass filter);

    void removeFilter(String name);

    Stack<FilterEntry> getFilterStack();

    boolean isFilterInUse(String name);

    String filter(String s)
            throws FilterException;

    /**
     * @return The StringBuilder has the output.
     */
    StringBuilder fastFilter(IFrame caller, StringBuilder in, StringBuilder out)
            throws FilterException;

    MainParser getParser();

    ResourceVariant resolveHref(String href)
            throws IOException;

    ResourceVariant resolveQName(String qName)
            throws IOException;

    ResourceVariant resolveModule(String module)
            throws IOException;

    Object eval(String code)
            throws EvalException, IOException;

    Object eval(String code, String fileName)
            throws EvalException, IOException;

    void parse(String href)
            throws IOException, ParseException;

}
