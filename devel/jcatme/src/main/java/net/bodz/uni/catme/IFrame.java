package net.bodz.uni.catme;

import java.io.IOException;
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

    boolean isCommandDefined(String name);

    Command getCommand(String name);

    void addCommand(Command command);

    void removeCommand(String name);

    boolean isVarDefined(String name);

    Object getVar(String name);

    IFrame findVar(String name);

    void putVar(String name, Object value);

    void removeVar(String name);

    boolean isFilterInUse(String name);

    int getFilterCount();

    void pushFilter(FilterEntry filter);

    FilterEntry popFilter();

    FilterEntry peekFilter();

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
