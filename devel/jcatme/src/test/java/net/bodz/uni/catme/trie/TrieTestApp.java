package net.bodz.uni.catme.trie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.c.loader.ClassResource;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.res.builtin.URLResource;

public class TrieTestApp
        implements
            ITokenCallback<Symbol> {

    TrieLexer<Symbol> lexer;
    TrieLexer<Symbol> xlexer;

    public TrieTestApp() {
        Map<String, String> map = new HashMap<>();
        map.put("/*", "comment-start");
        map.put("//", "slcomment");
        map.put("#", "sharp");
        map.put("<!--", "xml-start");
        map.put("<![CDATA[", "cdata-start");
        map.put("--", "sql-comment");
        map.put("\\", "escape");

        Map<String, String> xmap = new HashMap<>();
        xmap.put("*/", "comment-end");
        xmap.put("-->", "xml-end");
        xmap.put("]]>", "cdata-end");

        lexer = convert(map);
        xlexer = convert(xmap);
    }

    TrieLexer<Symbol> convert(Map<String, String> map) {
        TrieLexer<Symbol> lexer = new TrieLexer<>();
        int id = 1;
        for (String k : map.keySet()) {
            String name = map.get(k);
            Symbol kw = new Symbol(id++, name);
            lexer.declare(k, kw);
        }
        return lexer;
    }

    void run1()
            throws Exception {
        URLResource res = ClassResource.getData(TrieTestApp.class, "1");
        String source = res.read().readString();
        for (Token<Symbol> token : lexer.tokenize(source)) {
            if (token.isSymbol()) {
                System.out.printf("<%d:%d:%s>", token.line, token.column, token.text);
            } else {
                System.out.printf("%d:%d:%s", token.line, token.column, token.text);
            }
        }
    }

    void run2()
            throws Exception {
        URLResource res = ClassResource.getData(TrieTestApp.class, "1");
        String source = res.read().readString();
        lexer.newParser(source, this).parse();
    }

    @Override
    public boolean onToken(TrieParser<Symbol> parser, int line, int column, StringBuilder cbuf, Symbol symbol)
            throws IOException, ParseException {
        if (symbol != null) {
            System.out.printf("<%d:%d:%s>", line, column, cbuf);
        } else {
            System.out.printf("%d:%d:%s", line, column, cbuf);
        }
        return true;
    }

    public static void main(String[] args)
            throws Exception {
        new TrieTestApp().run2();
    }

}
