package net.bodz.uni.shelj.av;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * Raw language dumper
 *
 * @label A label
 */
@ProgramName("rawdump")
public class RawDump
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(isr);
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Line: " + line);

            CharStream charStream = CharStreams.fromString(line);
            RawLexer lexer = new RawLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            RawParser parser = new RawParser(tokens);
            ParseTree tree = parser.parse();

            List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
            String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);

            System.out.println(prettyTree);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new RawDump().execute(args);
    }

}
