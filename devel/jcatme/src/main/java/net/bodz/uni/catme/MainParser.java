package net.bodz.uni.catme;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Value;

import net.bodz.bas.c.java.io.capture.Processes;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.ICharIn;
import net.bodz.bas.io.StringCharIn;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.js.IScriptContext;
import net.bodz.uni.catme.js.PolyglotContext;
import net.bodz.uni.catme.lex.ILa1CharIn;
import net.bodz.uni.catme.lex.ITokenLexer;
import net.bodz.uni.catme.lex.La1CharInImpl;
import net.bodz.uni.catme.lex.NonspaceTokenLexer;
import net.bodz.uni.catme.trie.ITokenCallback;
import net.bodz.uni.catme.trie.TrieParser;

public class MainParser {

    static final Logger logger = LoggerFactory.getLogger(MainParser.class);

    public static final String VAR_PARSER = "Parser";

    CatMe app;
    boolean breakLines = false;

    IScriptContext scriptContext;
    Value commandDispatcher;

    /** main text stream (output) */
    public static final String TEXT = "text";
    Map<String, StringBuffer> streams = new HashMap<>();

    Appendable out = System.out;

    public MainParser(CatMe app)
            throws IOException {
        this.app = app;
    }

    public boolean initScriptContext() {
        if (this.scriptContext != null)
            return true;

        PolyglotContext scriptContext;
        scriptContext = PolyglotContext.createContext(app.scriptResolver);
        scriptContext.put(CatMe.VAR_APP, app);

        Value bindings = scriptContext.getBindings();
        scriptContext.put(CatMe.VAR_GLOBAL, bindings);

        scriptContext.put(MainParser.VAR_PARSER, this);

        try {
            scriptContext.include("./js/appInit.js");
            this.scriptContext = scriptContext;
            return true;
        } catch (Exception e) {
            logger.error("Failed to initialize app: " + e.getMessage(), e);
            return false;
        }
    }

    public IScriptContext getScriptContext() {
        if (!initScriptContext()) {
            throw new IllegalStateException("Can't initialize JavaScript engine.");
        }
        return scriptContext;
    }

    public void shell(String[] cmdarray)
            throws IOException, InterruptedException {
        Process process = Processes.shellExec(cmdarray);
        String result = Processes.iocap(process, "utf-8");
        out.append(result);
    }

    public Value getCommandDispatcher() {
        return commandDispatcher;
    }

    public void setCommandDispatcher(Value commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    public Appendable getOut() {
        return out;
    }

    boolean stopParseFrameSource = false;

    public void stop() {
        stopParseFrameSource = true;
    }

    public void parseFrameSource(IMutableFrame frame, final ICharIn in)
            throws IOException, ParseException {
        FileFrame ff = frame.getClosestFileFrame();

        class Callback
                implements
                    ITokenCallback<MySym> {

            boolean inComments;
            boolean singleLineComment = false;
            StringBuilder buf = new StringBuilder(1024);
            int textStart, textEnd;

            @Override
            public String toString() {
                return frame.toString();
            }

            @Override
            public boolean onToken(TrieParser<MySym> trieParser, int line, int column, StringBuilder cbuf, MySym symbol)
                    throws IOException, ParseException {
                frame.setLocation(line, column);
                if (symbol != null) {
                    switch (symbol.id) {
                    case MySym.ID_SL_OPENER:
                        singleLineComment = true;
                    case MySym.ID_OPENER:
                        inComments = true;
                        buf.append(cbuf);
                        textStart = buf.length();
                        textEnd = 0;

                        TrieParser<MySym> subTrie = ff.commentLexer.newParser(in, this).at(line, column);
                        subTrie.setBreakLines(symbol == MySym.SL_OPENER);
                        subTrie.parse();
                        if (textEnd <= 0)
                            textEnd = buf.length();
                        parseComments(frame, buf, textStart, textEnd, !singleLineComment);

                        inComments = false;
                        singleLineComment = false;
                        buf.setLength(0);
                        break;

                    case MySym.ID_ESCAPE:
                        // ctoks.add(token);
                        break;

                    case MySym.ID_CLOSER:
                        textEnd = buf.length();
                        buf.append(cbuf);
                        return false; // quit the sub-lang
                    }
                } else {
                    if (inComments) {
                        buf.append(cbuf);
                        if (singleLineComment)
                            return false; // quit the sub-lang
                    } else {
                        parseText(frame, cbuf);
                    }
                } // if token.isSymbol()
                return !stopParseFrameSource;
            }
        }

        TrieParser<MySym> trieParser = ff.lexer.newParser(in, new Callback()).at(0, 0);
        trieParser.setBreakLines(app.breakLines);
        trieParser.parse();
    }

    boolean parseComments(IFrame frame, StringBuilder cbuf, int textStart, int textEnd, boolean multiLine)
            throws IOException, ParseException {
        String trim = cbuf.substring(textStart, textEnd).trim();

        FileFrame ff = frame.getClosestFileFrame();
        boolean special = trim.startsWith(ff.escapePrefix);

        if (special) {
            int echoLines = frame.getEchoLines();
            if (echoLines != 0) {
                out.append(cbuf);
                if (echoLines != -1)
                    frame.setEchoLines(--echoLines);
            }
            parseInstruction(frame, trim);
        } else {
            parseText(frame, cbuf);
        }
        return special;
    }

    void parseInstruction(IFrame frame, String instruction)
            throws IOException, ParseException {
        // if (frame.isRemoveLeads())
        String indenter = trailingSpaceBuf.toString();
        frame.setIndenter(indenter);
        trailingSpaceBuf.setLength(0);

        ILa1CharIn in = new La1CharInImpl(new StringCharIn(instruction));

        FileFrame ff = frame.getClosestFileFrame();
        String escape = ff.getEscapePrefix();

        NonspaceTokenLexer nonspace = NonspaceTokenLexer.INSTANCE;
        int c;
        while ((c = in.look()) != -1) {
            if (Character.isWhitespace(c)) {
                in.read();
                continue;
            }
            String name = nonspace.lex(in);
            if (!name.startsWith(escape))
                throw new IllegalArgumentException( //
                        "Expected escape-seq(" + escape + ") before " + name);
            name = name.substring(1);

            String parenthesizedStr = "";
            int pos = name.indexOf('(');
            if (pos != -1) {
                if (!name.endsWith(")"))
                    throw new IllegalArgumentException("Unmatched parenthesis: " + name);
                parenthesizedStr = name.substring(pos + 1, name.length() - 1);
                name = name.substring(0, pos);
            }

            ICommand cmd = frame.getCommand(name);
            if (cmd == null)
                throw new IllegalArgumentException("Unknown command: " + name);

            ITokenLexer<List<?>> lexer = cmd.getArgumentsLexer();
            List<?> args = lexer.lex(in);

            if (cmd.isScript()) {
                String[] optv = parenthesizedStr.split("\\s+");
                if (commandDispatcher == null)
                    throw new IllegalUsageException("commandDispatcher wan't set.");
                else
                    commandDispatcher.execute(cmd, optv, args);
            } else {
                CommandOptions options = cmd.parseOptions(parenthesizedStr);
                Object[] argv = args.toArray();
                cmd.execute(frame, options, argv);
            }
        }
    }

    StringBuilder cbuf_alt = new StringBuilder(16384);
    StringBuilder trailingSpaceBuf = new StringBuilder();

    void parseText(IFrame frame, StringBuilder cbuf)
            throws IOException, FilterException {
        StringBuilder result = frame.fastFilter(frame, cbuf, cbuf_alt);

        if (frame.isRemoveLeads()) {
            if (trailingSpaceBuf.length() != 0) {
                out.append(trailingSpaceBuf);
                trailingSpaceBuf.setLength(0);
            }

            int n = result.length();
            int y = 0;
            L: for (int end = n - 1; end >= 0; end--) {
                switch (result.charAt(end)) {
                case ' ':
                case '\t':
                    y++;
                    continue L;
                case '\r':
                case '\n':
                    break L;
                }
            }

            if (y != 0) {
                trailingSpaceBuf.append(result.substring(n - y));
                result.setLength(n - y);
            }
        }

        if (app.debug)
            out.append("<" + result + ">");
        else {
            out.append(result);
        }

        result.setLength(0);
    }

}
