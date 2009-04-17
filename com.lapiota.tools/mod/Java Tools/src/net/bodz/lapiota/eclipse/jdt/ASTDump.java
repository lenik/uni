package net.bodz.lapiota.eclipse.jdt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.io.Files;
import net.bodz.bas.types.util.CompatMethods;
import net.bodz.bas.types.util.Strings;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

@Doc("ASTDump description")
@RcsKeywords(id = "$Id$")
@Version( { 0, 0 })
public class ASTDump extends JdtBasicCLI {

    @Option(alias = "o", vnam = "FILE", doc = "dump to the specified file")
    protected CharOut out         = CharOuts.stdout;

    private int       parserLevel = AST.JLS3;

    @SuppressWarnings("deprecation")
    @Option(vnam = "VERNUM", doc = "version number of JLS (Java Language Specification)")
    protected void jlsVersion(int ver) {
        if (ver == 2)
            parserLevel = AST.JLS2;
        else if (ver == 3)
            parserLevel = AST.JLS3;
        else
            throw new IllegalArgumentException("invalid version: " + ver);
    }

    public static void main(String[] args) throws Throwable {
        new ASTDump().run(args);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doFileArgument(File file) throws Throwable {
        if (file == null)
            _help();

        L.info("load file ", file);
        char[] src = Files.readAll(file).toCharArray();

        ASTParser parser = ASTParser.newParser(parserLevel);
        Map<String, Object> options = JavaCore.getOptions();
        // options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
        // options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform",
        // "1.6");
        options.put("org.eclipse.jdt.core.compiler.source", "1.6");
        parser.setCompilerOptions(options);

        if (L.showDetail()) {
            List<String> keys = new ArrayList<String>(options.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                Object val = options.get(key);
                System.out.printf("%20s=%s\n", key, val);
            }
        }

        parser.setSource(src);

        CompilationUnit root = (CompilationUnit) parser.createAST(null);

        // ASTVisitor av = new ASTDumpVisitor(this);
        ASTVisitor visitor = CompatMethods.newInstance(Visitor.class, this);
        root.accept(visitor);
    }

    public class Visitor extends ASTVisitor {

        private int tabsize = 2;
        private int indent;

        @Override
        public void preVisit(ASTNode node) {
            String type = node.getClass().getSimpleName();
            L.nmesg(Strings.repeat(indent, ' '));
            Map<?, ?> props = node.properties();
            L.fmesg("%s(%d/%d %d+%d %s): ", //
                    type, node.getNodeType(), node.getFlags(), //
                    node.getStartPosition(), node.getLength(), //
                    props.isEmpty() ? "" : props.toString());
            L.mesg(node);
            indent += tabsize;
        }

        @Override
        public void postVisit(ASTNode node) {
            indent -= tabsize;
        }

    }

}
