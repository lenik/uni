package net.bodz.uni.shelj.av;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.shelj.av.ArgumentsParser.ScriptContext;

/**
 * Dumper util for the arguments language
 *
 * @label av-dump
 */
@ProgramName("avdump")
public class AvLoop
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        while ((line = reader.readLine()) != null) {
            CharStream in = CharStreams.fromString(line);
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

    public static void main(String[] args)
            throws Exception {
        new AvLoop().execute(args);
    }

}
