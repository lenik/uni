package net.bodz.uni.shelj.av;

import java.util.List;

import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

import net.bodz.bas.c.string.Strings;

public class TreeUtils {

    /** Platform dependent end-of-line marker */
    public static final String Eol = System.lineSeparator();
    /** The literal indent char(s) used for pretty-printing */
    public static final String Indents = "    ";
    private static int level;

    private TreeUtils() {
    }

    /**
     * Pretty print out a whole tree. {@link #getNodeText} is used on the node payloads to get the
     * text for the nodes. (Derived from Trees.toStringTree(....))
     */
    public static String toPrettyTree(final Tree t, final List<String> ruleNames) {
        level = 0;
        String s = process(t, ruleNames);
        String trim = s.replaceAll("(?m)^\\s+$", "");
        String merge = trim.replaceAll("\\r?\\n\\r?\\n", Eol);
        return merge;
    }

    private static String process(Tree node, List<String> ruleNames) {
        String text = Trees.getNodeText(node, ruleNames);
        text = Utils.escapeWhitespace(text, false);

        if (node.getChildCount() == 0)
            return text;

        StringBuilder sb = new StringBuilder();
        sb.append(lead(level));
        level++;
        sb.append(text);
        sb.append(" ");

        for (int i = 0; i < node.getChildCount(); i++)
            sb.append(process(node.getChild(i), ruleNames));

        level--;
        sb.append(lead(level));
        return sb.toString();
    }

    private static String lead(int level) {
        if (level <= 0)
            return "";
        else
            return Eol + Strings.repeat(level, Indents);
    }

}
