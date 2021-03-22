package net.bodz.uni.catme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;

import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.bas.io.ICharIn;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.impl.TreeOutImpl;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.js.IScriptContext;
import net.bodz.uni.catme.trie.ITokenCallback;
import net.bodz.uni.catme.trie.Token;
import net.bodz.uni.catme.trie.TrieParser;

public class MainParser {

    static final Logger logger = LoggerFactory.getLogger(MainParser.class);

    public static final String VAR_PARSER = MainParser.class.getSimpleName();

    CatMe app;

    /** main text stream (output) */
    public static final String TEXT = "text";
    Map<String, StringBuffer> streams = new HashMap<>();

    public Set<String> imported = new HashSet<>();

    IScriptContext scriptContext;
    Value cmdlineParser;

    ITreeOut out = TreeOutImpl.from(Stdio.cout);

    public MainParser(CatMe app, IScriptContext scriptContext)
            throws IOException {
        this.app = app;
        this.scriptContext = scriptContext;
    }

    public Object eval(String code)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        return scriptContext.eval(code);
    }

    public Object eval(String code, String fileName)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        return scriptContext.eval(code, fileName);
    }

    public Value getCmdlineParser() {
        return cmdlineParser;
    }

    public void setCmdlineParser(Value cmdlineParser) {
        this.cmdlineParser = cmdlineParser;
    }

    public ITreeOut getOut() {
        return out;
    }

    void parse(IFrame frame, final ICharIn in)
            throws IOException, ParseException {
        FileFrame ff = frame.getClosestFileFrame();

        class Callback
                implements
                    ITokenCallback<MySym> {

            boolean inComments;
            boolean singleLine = false;
            StringBuilder buf = new StringBuilder();
            int textStart, textEnd;

            @Override
            public boolean onToken(TrieParser<MySym> parser, Token<MySym> token)
                    throws IOException, ParseException {
                if (token.isSymbol()) {
                    switch (token.symbol.id) {
                    case MySym.SIMPLE_OPENER:
                        singleLine = true;
                    case MySym.OPENER:
                        inComments = true;
                        buf.append(token.text);
                        textStart = buf.length();
                        textEnd = 0;

                        ff.commentLexer.parse(in, this);
                        if (textEnd <= 0)
                            textEnd = buf.length();
                        frame.processComments(buf.toString(), textStart, textEnd, !singleLine);

                        inComments = false;
                        singleLine = false;
                        buf.setLength(0);
                        break;

                    case MySym.ESCAPE:
                        // ctoks.add(token);
                        break;

                    case MySym.CLOSER:
                        textEnd = buf.length();
                        buf.append(token.text);
                        return false;

                    default:
                        assert false;
                    }
                } else {
                    if (inComments) {
                        buf.append(token.text);
                        if (singleLine)
                            return false;
                    } else {
                        frame.processText(token.text);
                    }
                }
                return true;
            }
        }

        ff.lexer.parse(in, new Callback());
    }

    void parseInstruction(IFrame frame, String instruction)
            throws IOException, ParseException {
        if (cmdlineParser == null)
            throw new IllegalStateException("cmdlineParser wasn't set.");

        String[] args = StringQuoted.split(instruction);
        List<String> list = new ArrayList<>();
        for (String arg : args)
            list.add(arg);

        try {
            cmdlineParser.invokeMember("apply", null, list);
        } catch (Exception e) {
            logger.error("Failed to parse at js side: " + e.getMessage(), e);
            while (frame != null) {
                if (frame.isFile()) {
                    FileFrame f = (FileFrame) frame;
                    logger.error("    " + f.file);
                } else {
                    logger.error("    * " + frame);
                }
                frame = frame.getParent();
            }
            throw e;
        }
    }

}
