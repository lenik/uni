package net.bodz.uni.catme;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Value;

import net.bodz.bas.c.java.io.capture.Processes;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.bas.io.ICharIn;
import net.bodz.bas.io.StringCharIn;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.js.IScriptContext;
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

    IScriptContext scriptContext;
    Value commandDispatcher;

    /** main text stream (output) */
    public static final String TEXT = "text";
    Map<String, StringBuffer> streams = new HashMap<>();

    Appendable out = System.out;

    public MainParser(CatMe app, IScriptContext scriptContext)
            throws IOException {
        this.app = app;
        this.scriptContext = scriptContext;
    }

    public IScriptContext getScriptContext() {
        return scriptContext;
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
            public boolean onToken(TrieParser<MySym> parser, int line, int column, StringBuilder cbuf, MySym symbol)
                    throws IOException, ParseException {
                System.err.println("<token " + cbuf.toString().trim() + ">");
                frame.setLocation(line, column);
                if (symbol != null) {
                    switch (symbol.id) {
                    case MySym.SIMPLE_OPENER:
                        singleLineComment = true;
                    case MySym.OPENER:
                        inComments = true;
                        buf.append(cbuf);
                        textStart = buf.length();
                        textEnd = 0;

                        ff.commentLexer.newParser(in, this).at(line, column).parse();
                        if (textEnd <= 0)
                            textEnd = buf.length();
                        frame.processComments(buf, textStart, textEnd, !singleLineComment);

                        inComments = false;
                        singleLineComment = false;
                        buf.setLength(0);
                        break;

                    case MySym.ESCAPE:
                        // ctoks.add(token);
                        break;

                    case MySym.CLOSER:
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
                        frame.processText(cbuf);
                    }
                } // if token.isSymbol()
                return !stopParseFrameSource;
            }
        }

        ff.lexer.newParser(in, new Callback()).at(0, 0).parse();
    }

    void parseInstruction(IFrame frame, String instruction)
            throws IOException, ParseException {
        if (commandDispatcher == null)
            throw new IllegalUsageException("commandDispatcher wan't set.");

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
                commandDispatcher.execute(cmd, optv, args);
            } else {
                CommandOptions options = cmd.parseOptions(parenthesizedStr);
                Object[] argv = args.toArray();
                cmd.execute(frame, options, argv);
            }
        }
    }

}
