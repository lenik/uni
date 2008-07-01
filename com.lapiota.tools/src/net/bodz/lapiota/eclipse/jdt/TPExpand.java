package net.bodz.lapiota.eclipse.jdt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.cli.BatchProcessCLI;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.Caller;
import net.bodz.bas.lang.err.UnexpectedException;
import net.bodz.bas.lang.util.Classpath;
import net.bodz.bas.types.chained.CMap;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.util.Lapiota;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

@Doc("Remove Java 5 Generics from the java source files")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@RunInfo(init = { Lapiota.class },

_load = { "findcp|eclipse*/plugins/org.eclipse.jdt.core_*",
        "findcp|eclipse*/plugins/org.eclipse.text_*", },

load = { "findcp|eclipse*/plugins/org.eclipse.equinox.common_*",
        "findcp|eclipse*/plugins/org.eclipse.core.resources_*",
        "findcp|eclipse*/plugins/org.eclipse.core.jobs_*",
        "findcp|eclipse*/plugins/org.eclipse.core.runtime_*",
        "findcp|eclipse*/plugins/org.eclipse.osgi_*",
        "findcp|eclipse*/plugins/org.eclipse.core.contenttype_*",
        "findcp|eclipse*/plugins/org.eclipse.equinox.preferences_*", })
public class TPExpand extends BatchProcessCLI {

    @Option(alias = "b", vnam = "FILE|DIR")
    protected void bootClasspath(File file) throws IOException {
        URL url = file.toURI().toURL();
        _log2("add boot-classpath: " + url);
        Classpath.addURL(url);
    }

    @Option(alias = "c", vnam = "FILE|DIR")
    protected void classpath(File file) throws IOException {
        URL url = file.toURI().toURL();
        _log2("add classpath: " + url);
        // classpaths.add(url);
        Classpath.addURL(url);
    }

    @Override
    protected int _cliflags() {
        return super._cliflags() & ~CLI_AUTOSTDIN;
    }

    @Override
    protected int process(File in, File out) throws Throwable {
        String src = Files.readAll(in, inputEncoding);
        char[] srcChars = src.toCharArray();

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(srcChars);

        CompilationUnit root = (CompilationUnit) parser.createAST(null);

        ASTRewrite rewrite = ASTRewrite.create(root.getAST());
        ASTVisitor visitor = new Visitor(rewrite);

        root.accept(visitor);

        Document doc = new Document(src);
        TextEdit edits = rewrite.rewriteAST(doc, null);
        edits.apply(doc);
        String dst = doc.get();

        if (dst.equals(src))
            return PROCESS_IGNORE;
        Files.write(out, dst, outputEncoding);
        return PROCESS_EDIT;
    }

    static Class<?> resolveType(String name, boolean guessInner) {
        ClassLoader loader = Caller.getCallerClassLoader();
        while (true) {
            try {
                return Class.forName(name, false, loader);
            } catch (ClassNotFoundException e) {
            }
            if (!guessInner)
                return null;
            int dot = name.lastIndexOf('.');
            if (dot == -1)
                return null;
            name = name.substring(0, dot) + "$" + name.substring(dot + 1);
        }
    }

    static Object resolveImport(String name) {
        Class<?> clazz = resolveType(name, true);
        if (clazz != null)
            return clazz;
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return null;
        String member = name.substring(dot + 1);
        name = name.substring(0, dot);
        clazz = resolveType(name, true);
        if (clazz == null)
            return null;
        try {
            return clazz.getField(member);
        } catch (NoSuchFieldException e) {
        }
        List<Method> byname = new ArrayList<Method>();
        for (Method method : clazz.getMethods())
            if (method.getName().equals(member))
                byname.add(method);
        if (!byname.isEmpty())
            return byname;
        return null;
    }

    public class Visitor extends ASTVisitor {

        ASTRewrite           rewrite;

        Set<String>          importPackages;

        /** TypeParameter, Class<?> */
        CMap<String, Object> typens;
        /** Method, List<Method> */
        CMap<String, Object> funns;
        /** Type, Field */
        CMap<String, Object> varns;

        public Visitor(ASTRewrite rewrite) {
            this.rewrite = rewrite;
            importPackages = new HashSet<String>();
            typens = new CMap<String, Object>();
            funns = new CMap<String, Object>();
            varns = new CMap<String, Object>();
        }

        public Object resolveImport2(String name) {
            Object imported = resolveImport(name);
            if (imported == null && !name.contains(".")) {
                for (String p : importPackages)
                    if ((imported = resolveType(p + "." + name, false)) != null)
                        break;
            }
            if (imported instanceof Class || imported == null)
                typens.put(name, imported);
            else if (imported instanceof List)
                funns.put(name, imported);
            else if (imported instanceof Field)
                varns.put(name, imported);
            else
                throw new UnexpectedException();
            return imported;
        }

        private int indent;
        private int tabsize = 2;

        String indent() {
            return Strings.repeat(indent, ' ');
        }

        void enterScope() {
            _log3(indent(), "enter-t=", typens);
            _log3(indent(), "enter-f=", typens);
            _log3(indent(), "enter-v=", varns);
            typens.enterNew();
            funns.enterNew();
            varns.enterNew();
            if (_verbose < 3)
                indent += tabsize;
        }

        void leaveScope() {
            _log3(indent(), "leave-t=", typens);
            _log3(indent(), "leave-f=", typens);
            _log3(indent(), "leave-v=", varns);
            typens.leave();
            funns.leave();
            varns.leave();
            if (_verbose < 3)
                indent -= tabsize;
        }

        Type expandMajor(SimpleType type) {
            if (!(type.getName() instanceof SimpleName))
                return type;
            SimpleName name = (SimpleName) type.getName();
            Object _parameter = typens.get(name.getIdentifier());
            if (_parameter instanceof TypeParameter) {
                TypeParameter parameter = (TypeParameter) _parameter;
                List<?> bounds = parameter.typeBounds();
                _log3(indent() + "bounds=" + bounds);
                if (bounds.isEmpty()) {
                    SimpleName nmObject = type.getAST().newSimpleName("Object");
                    SimpleType tyObject = type.getAST().newSimpleType(nmObject);
                    return tyObject;
                } else {
                    Type bmajor = (Type) bounds.get(0);
                    while (bmajor.isParameterizedType())
                        bmajor = ((ParameterizedType) bmajor).getType();
                    return bmajor;
                }
            }
            return type;
        }

        @Override
        public void preVisit(ASTNode node) {
            if (_verbose < 3)
                return;
            String type = node.getClass().getSimpleName();
            _p(Strings.repeat(indent, ' '));
            Map<?, ?> props = node.properties();
            _pf("%s(%d/%d %d+%d %s): ", //
                    type, node.getNodeType(), node.getFlags(), //
                    node.getStartPosition(), node.getLength(), //
                    props.isEmpty() ? "" : props.toString());
            _P(node);
            indent += tabsize;
        }

        @Override
        public void postVisit(ASTNode node) {
            if (_verbose < 3)
                return;
            indent -= tabsize;
        }

        @Override
        public boolean visit(ImportDeclaration node) {
            if (node.isStatic())
                return true;
            Name name = node.getName();
            String fqn = node.getName().getFullyQualifiedName();
            Object imported = resolveImport2(fqn);
            if (imported == null) {
                Package _package = Package.getPackage(fqn);
                if (_package == null)
                    throw new RuntimeException("can't import " + fqn);
                importPackages.add(fqn);
            } else {
                SimpleName sname = name.isSimpleName() ? ((SimpleName) name)
                        : ((QualifiedName) name).getName();
                typens.put(sname.getIdentifier(), imported);
            }
            return true;
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            enterScope();
            return super.visit(node);
        }

        @Override
        public void endVisit(TypeDeclaration node) {
            leaveScope();
            super.endVisit(node);
        }

        @Override
        public boolean visit(MethodDeclaration node) {
            enterScope();
            return super.visit(node);
        }

        @Override
        public void endVisit(MethodDeclaration node) {
            leaveScope();
            super.endVisit(node);
        }

        @Override
        public boolean visit(Block node) {
            enterScope();
            return super.visit(node);
        }

        @Override
        public void endVisit(Block node) {
            leaveScope();
            super.endVisit(node);
        }

        @Override
        public boolean visit(TypeParameter node) {
            SimpleName name = node.getName();
            typens.put(name.getIdentifier(), node);
            rewrite.remove(node, null);
            return false; // super.visit(node);
        }

        @Override
        public boolean visit(SingleVariableDeclaration node) {
            SimpleName name = node.getName();
            Type type = node.getType();
            node.isVarargs();
            varns.put(name.getIdentifier(), type);
            return super.visit(node);
        }

        @Override
        public boolean visit(VariableDeclarationStatement node) {
            Type type = node.getType();
            for (Object _fragment : node.fragments()) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment) _fragment;
                SimpleName name = fragment.getName();
                varns.put(name.getIdentifier(), type);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(VariableDeclarationExpression node) {
            Type type = node.getType();
            for (Object _fragment : node.fragments()) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment) _fragment;
                SimpleName name = fragment.getName();
                varns.put(name.getIdentifier(), type);
            }
            return super.visit(node);
        }

        /** type of typeparameter is always simple */
        @Override
        public boolean visit(SimpleType node) {
            Type extype = expandMajor(node);
            _log3(indent() + "resolved " + node + " => " + extype);
            if (extype != node) {
                rewrite.replace(node, extype, null);
            }
            return true; // super.visit(node);
        }

        @Override
        public boolean visit(ParameterizedType node) {
            Type type = node.getType();
            if (type instanceof SimpleType)
                type = expandMajor((SimpleType) type);
            _log3(indent() + "reduce " + node + " => " + type);
            rewrite.replace(node, type, null);
            return super.visit(node);
        }

    }

}
