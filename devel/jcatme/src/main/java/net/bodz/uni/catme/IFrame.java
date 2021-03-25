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

    FileFrame getClosestFileFrame();

    int getDepth();

    boolean isFile();

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

    boolean isVarDefined(String name);

    Object getVar(String name);

    IFrame findVar(String name);

    void putVar(String name, Object value);

    void removeVar(String name);

    boolean isFilterDefined(String name);

    ITextFilter getFilter(String name);

    void addFilter(String name, ITextFilter filter);

    void removeFilter(String name);

    Stack<FilterEntry> getFilterStack();

    boolean isFilterInUse(String name);

    String filter(String s)
            throws EvalException;

    MainParser getParser();

    ResourceVariant resolveHref(String href)
            throws IOException;

    ResourceVariant resolveQName(String qName)
            throws IOException;

    void parse(String href)
            throws IOException, ParseException;

    int TextNormal = 0;
    int SpaceIgnorableLeading = 1;
    int SpaceIgnorableBetween = 2;

    void processComments(String s, int textStart, int textEnd, boolean multiLine)
            throws IOException, ParseException;

    void processText(String s)
            throws IOException;

}
