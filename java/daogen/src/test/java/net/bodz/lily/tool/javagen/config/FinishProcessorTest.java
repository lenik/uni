package net.bodz.lily.tool.javagen.config;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

import net.bodz.bas.c.string.Strings;

public class FinishProcessorTest
        extends Assert {

    static final Function<String, String> ident = (String s) -> s;
    static final Function<String, String> ucfirst = (String s) -> Strings.ucfirst(s);
    static final Function<String, String> trim_ = (String s) -> {
        while (s.endsWith("_"))
            s = s.substring(0, s.length() - 1);
        return s;
    };

    @Test
    public void testCommonPrefix() {
        // 1
        testCommonPrefix("foo_id", "id", trim_, "foo");
        testCommonPrefix("foo_cat", "dog", trim_, null);

        testCommonPrefix("foo", "id", trim_, null);
        testCommonPrefix("foo__id", "id", trim_, "foo");

        // 2
        testCommonPrefix("foo_a, foo_b", "a, b", null, "foo_");
        testCommonPrefix("foo_a, foo_b", "a, b", trim_, "foo");

        // implied component
        testCommonPrefix("foo_cat, foo", "cat, dog", trim_, "foo");
        testCommonPrefix("foo, foo_cat", "dog, cat", trim_, "foo");
        testCommonPrefix("foo, foo_cat", "cat, dog", trim_, null);

        // property vs field
        testCommonPrefix("fooA, fooB", "a, b", ident, ucfirst, trim_, "foo");
    }

    void testCommonPrefix(String as, String bs, Function<String, String> norm, String expected) {
        testCommonPrefix(as, bs, ident, ident, norm, expected);
    }

    void testCommonPrefix(String as, String bs, Function<String, String> map, Function<String, String> norm,
            String expected) {
        testCommonPrefix(as, bs, map, map, norm, expected);
    }

    void testCommonPrefix(String as, String bs, Function<String, String> mapa, Function<String, String> mapb,
            Function<String, String> norm, String expected) {
        String[] av = split(as, ",");
        String[] bv = split(bs, ",");
        String result = FinishProcessor.commonPrefix(av, bv, mapa, mapb, norm);
        assertEquals(expected, result);
    }

    String[] split(String s, String delim) {
        StringTokenizer tokens = new StringTokenizer(s, delim);
        List<String> list = new ArrayList<>();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            list.add(token.trim());
        }
        return list.toArray(new String[0]);
    }

}
