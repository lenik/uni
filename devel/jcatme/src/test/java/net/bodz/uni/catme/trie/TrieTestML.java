package net.bodz.uni.catme.trie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.c.loader.ClassResource;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.StringCharIn;
import net.bodz.bas.io.res.builtin.URLResource;

public class TrieTestML
        implements
            ITokenCallback<Msym> {

    TrieLexer<Msym> lexer;
    TrieLexer<Msym> xlexer;

    public TrieTestML() {
        Map<String, String> map = new HashMap<>();
        map.put("/*", "comment-start");
        map.put("//", "sl-1");
        map.put("#", "sharp-1");
        map.put("<!--", "xml-comment");
        map.put("<![CDATA[", "cdata");
        map.put("--", "sql-1");

        Map<String, String> xmap = new HashMap<>();
        xmap.put("*/", "comment-end");
        xmap.put("-->", "xml-end");
        xmap.put("]]>", "cdata-end");
        xmap.put("\\", "escape");

        lexer = convert(map);
        xlexer = convert(xmap);
    }

    TrieLexer<Msym> convert(Map<String, String> map) {
        TrieLexer<Msym> lexer = new TrieLexer<>();
        int id = 1;
        for (String k : map.keySet()) {
            String name = map.get(k);
            Msym kw = new Msym(id++, name);
            lexer.declare(k, kw);
        }
        return lexer;
    }

    StringCharIn in;

    void run()
            throws Exception {
        URLResource res = ClassResource.getData(TrieTestApp.class, "1");
        String source = res.read().readString();
        in = new StringCharIn(source);
        lexer.parse(in, this);
    }

    boolean sub;

    @Override
    public boolean onToken(TrieParser<Msym> parser, Token<Msym> token)
            throws IOException, ParseException {
        if (token.isSymbol()) {
            if (sub) {
                System.out.printf("`%d:%d:%s'", token.line, token.column, token.text);
                if (token.symbol.end)
                    return false;
            } else {
                System.out.printf("<%d:%d:%s>", token.line, token.column, token.text);
                if (token.symbol.comment) {
                    sub = true;
                    xlexer.parse(in, this);
                    sub = false;
                }
            }
        } else {
            System.out.printf("%d:%d:%s", token.line, token.column, token.text);
        }
        return true;
    }

    public static void main(String[] args)
            throws Exception {
        new TrieTestML().run();
    }

}
