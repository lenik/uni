package net.bodz.lapiota.eclipse.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.sio.ICharOut;
import net.bodz.bas.sio.Stdio;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.VFS;

/**
 * ASTDump description
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class ASTDump
        extends JdtBasicCLI {

    /**
     * Dump to the specified file.
     *
     * @option -o =FILE
     */
    protected ICharOut out = Stdio.cout;

    private int parserLevel = AST.JLS3;

    /**
     * Version number of JLS (Java Language Specification)
     *
     * @option =VERNUM
     */
    @SuppressWarnings("deprecation")
    protected void jlsVersion(int ver) {
        if (ver == 2)
            parserLevel = AST.JLS2;
        else if (ver == 3)
            parserLevel = AST.JLS3;
        else
            throw new IllegalArgumentException("invalid version: " + ver);
    }

    protected void dumpJavaFile(IFile javaFile)
            throws Exception {
        if (javaFile == null)
            _help();

        logger.info("load file ", javaFile);
        char[] javaSource = javaFile.tooling()._for(StreamReading.class).readString().toCharArray();

        ASTParser astParser = ASTParser.newParser(parserLevel);
        Map<String, Object> options = JavaCore.getOptions();
        // options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
        // options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform",
        // "1.6");
        options.put("org.eclipse.jdt.core.compiler.source", "1.6");
        astParser.setCompilerOptions(options);

        if (logger.isInfoEnabled(/* 1 */)) {
            List<String> keys = new ArrayList<String>(options.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                Object val = options.get(key);
                System.out.printf("%20s=%s\n", key, val);
            }
        }

        astParser.setSource(javaSource);

        CompilationUnit root = (CompilationUnit) astParser.createAST(null);

        // ASTVisitor av = new ASTDumpVisitor(this);
        ASTVisitor visitor = new Visitor();
        root.accept(visitor);
    }

    public class Visitor
            extends ASTVisitor {

        private int tabsize = 2;
        private int indent;

        @Override
        public void preVisit(ASTNode node) {
            String type = node.getClass().getSimpleName();
            logger.mesg(Strings.repeat(indent, ' '));
            Map<?, ?> props = node.properties();
            logger.mesgf("%s(%d/%d %d+%d %s): ", //
                    type, node.getNodeType(), node.getFlags(), //
                    node.getStartPosition(), node.getLength(), //
                    props.isEmpty() ? "" : props.toString());
            logger.mesg(node);
            indent += tabsize;
        }

        @Override
        public void postVisit(ASTNode node) {
            indent -= tabsize;
        }

    }

    public static void main(String[] args)
            throws Exception {
        new ASTDump().execute(args);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (String pathname : expandWildcards(args)) {
            IFile file = VFS.resolve(pathname);
            dumpJavaFile(file);
        }
    }

}
