package net.bodz.uni.snmcc;

import java.io.IOException;
import java.util.regex.Matcher;

import net.bodz.bas.c.java.util.regex.IPartProcessor;
import net.bodz.bas.c.java.util.regex.TextPrepByParts;

public class Util {

    /**
     * Convert relative path to FQCN.
     */
    public static String rpathToFQCN(String rpath) {
        rpath = rpath.replace('\\', '/');
        rpath = rpath.replace("//", "/");
        rpath = rpath.replace("./", "");
        return rpath.replace('/', '.');
    }

    /**
     * If string contains VRT(Variable Reference in Text) in the format of
     * <code>VRT_variable_TRV</code>, then strip the VRT/TRV wraps and returns the stripped string.
     *
     * If no VRT found in the string, then null is returned.
     *
     * @return VRT-wrapper stripped string, or null if string doesn't contain VRT.
     */
    public static String parseVRT(String string) {
        TextPrepByParts pp = TextPrepByParts.match("VRT_(\\w+?)_TRV", new IPartProcessor() {

            @Override
            public void process(CharSequence in, int start, int end, Appendable out, Matcher matcher)
                    throws IOException {
                String var = matcher.group(1);
                out.append(var);
            }
        });

        String stripped = pp.process(string);
        if (pp.getMatchedCount() > 0)
            return stripped;
        return null;
    }

}
