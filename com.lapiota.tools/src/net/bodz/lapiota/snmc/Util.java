package net.bodz.lapiota.snmc;

import net.bodz.bas.types.util.PatternProcessor;

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
     * <code>VRT_variable_TRV</code>, then strip the VRT/TRV wraps and returns
     * the stripped string.
     *
     * If no VRT found in the string, then null is returned.
     *
     * @return VRT-wrapper stripped string, or null if string doesn't contain
     *         VRT.
     */
    public static String parseVRT(String string) {
        PatternProcessor pp = new PatternProcessor("VRT_(\\w+?)_TRV") {
            @Override
            protected void matched(String part) {
                String var = matcher.group(1);
                buffer.append(var);
            }
        };

        String stripped = pp.process(string);
        if (pp.getMatchedCount() > 0)
            return stripped;
        return null;
    }

}
