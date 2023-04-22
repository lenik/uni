package net.bodz.uni.shelj.av;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.shelj.av.ArgumentsParser.ScriptContext;

/**
 * Dumper util for the arguments language
 *
 * @label av-dump
 */
@ProgramName("avdump")
public class AvDump
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (String name : args) {
            CharStream in = CharStreams.fromFileName(name);
            ArgumentsLexer lexer = new ArgumentsLexer(in);
            while (true) {
                Token tok = lexer.nextToken();
                int t = tok.getType();
                if (t == Token.EOF)
                    break;
                String displayName = lexer.getVocabulary().getDisplayName(t);
                System.out.println(displayName + ": '" + tok.getText() + "'");
            }
            lexer.reset();
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            ArgumentsParser parser = new ArgumentsParser(tokens);
            ScriptContext root = parser.script();

            List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
            String prettyTree = TreeUtils.toPrettyTree(root, ruleNamesList);

            System.out.println(prettyTree);
        }
    }

    class Listener
            implements
                ParseTreeListener {

        @Override
        public void visitTerminal(TerminalNode node) {
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
        }

    }

    public static void main(String[] args)
            throws Exception {
        new AvDump().execute(args);
    }

}
