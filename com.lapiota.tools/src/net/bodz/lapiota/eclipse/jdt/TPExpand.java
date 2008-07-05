package net.bodz.lapiota.eclipse.jdt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.cli.BatchProcessCLI;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.UnexpectedException;
import net.bodz.bas.lang.util.Classpath;
import net.bodz.bas.types.chained.CMap;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.util.Lapiota;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
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
        L.x.P("add boot-classpath: ", url);
        Classpath.addURL(url);
    }

    @Option(alias = "c", vnam = "FILE|DIR")
    protected void classpath(File file) throws IOException {
        URL url = file.toURI().toURL();
        L.x.P("add classpath: ", url);
        // classpaths.add(url);
        Classpath.addURL(url);
    }

    @Override
    protected int _cliflags() {
        return super._cliflags() & ~CLI_AUTOSTDIN;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ProcessResult process(File in, File out) throws Throwable {
        String src = Files.readAll(in, inputEncoding);
        char[] srcChars = src.toCharArray();

        Map<String, Object> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_SOURCE, "1.6");
        options.remove(JavaCore.COMPILER_TASK_TAGS);

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setCompilerOptions(options);
        parser.setBindingsRecovery(true);
        parser.setResolveBindings(true);
        parser.setSource(srcChars);

        // IProject project;

        String unitName = in.getName();
        parser.setUnitName(unitName);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        ASTRewrite rewrite = ASTRewrite.create(cu.getAST());
        ASTVisitor visitor = new Visitor(rewrite);

        cu.accept(visitor);

        Document doc = new Document(src);
        TextEdit edits = rewrite.rewriteAST(doc, null);
        edits.apply(doc);
        String dst = doc.get();

        // if (dst.equals(src))
        // return PROCESS_IGNORE;
        Files.write(out, dst, outputEncoding);
        return ProcessResult.autodiff();
    }

    class ASTFrameVisitor extends ASTVisitor2 {

        protected Set<String>          importPackages;

        /** TypeParameter, Class<?> */
        protected CMap<String, Object> typens;
        /** Method, List<Method> */
        protected CMap<String, Object> funns;
        /** Type, Field */
        protected CMap<String, Object> varns;

        protected int                  indent;
        private int                    tabsize = 2;

        public ASTFrameVisitor() {
            importPackages = new HashSet<String>();
            typens = new CMap<String, Object>();
            funns = new CMap<String, Object>();
            varns = new CMap<String, Object>();
        }

        public Object resolveImport2(String name) {
            Object imported = JavaUtil.resolveImport(name);
            if (imported == null && !name.contains(".")) {
                for (String p : importPackages)
                    if ((imported = JavaUtil.resolveType(p + "." + name, false)) != null)
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

        String indent() {
            return Strings.repeat(indent, ' ');
        }

        void enterScope() {
            L.d.P(indent(), "enter-t=", //
                    Strings.ellipse(typens.toString(), 100));
            L.d.P(indent(), "enter-f=", //
                    Strings.ellipse(funns.toString(), 100));
            L.d.P(indent(), "enter-v=", //
                    Strings.ellipse(varns.toString(), 100));
            typens.enterNew();
            funns.enterNew();
            varns.enterNew();
            if (!L.showDebug())
                indent += tabsize;
        }

        void leaveScope() {
            L.d.P(indent(), "leave-t=", //
                    Strings.ellipse(typens.toString(), 100));
            L.d.P(indent(), "leave-f=", //
                    Strings.ellipse(funns.toString(), 100));
            L.d.P(indent(), "leave-v=", //
                    Strings.ellipse(varns.toString(), 100));
            typens.leave();
            funns.leave();
            varns.leave();
            if (!L.showDebug())
                indent -= tabsize;
        }

        Type expandMajor(SimpleType type) {
            if (!(type.getName() instanceof SimpleName))
                return type;
            SimpleName name = (SimpleName) type.getName();
            Type expanded = expandMajor(name);
            return expanded == null ? type : expanded;
        }

        Type expandMajor(SimpleName name) {
            Object _parameter = typens.get(name.getIdentifier());
            if (_parameter instanceof TypeParameter) {
                TypeParameter parameter = (TypeParameter) _parameter;
                List<?> bounds = parameter.typeBounds();
                if (bounds.isEmpty()) {
                    SimpleName nmObject = name.getAST().newSimpleName("Object");
                    SimpleType tyObject = name.getAST().newSimpleType(nmObject);
                    return tyObject;
                } else {
                    Type bmajor = (Type) bounds.get(0);
                    while (bmajor.isParameterizedType())
                        bmajor = ((ParameterizedType) bmajor).getType();
                    return bmajor;
                }
            }
            return null;
        }

        @Override
        public void preVisit(ASTNode node) {
            if (!L.showDebug())
                return;
            String type = node.getClass().getSimpleName();
            L.x.p(Strings.repeat(indent, ' '));
            Map<?, ?> props = node.properties();
            L.x.pf("%s(%d/%d %d+%d %s): ", //
                    type, node.getNodeType(), node.getFlags(), //
                    node.getStartPosition(), node.getLength(), //
                    props.isEmpty() ? "" : props.toString());
            L.x.P(node);
            indent += tabsize;
            super.preVisit(node);
        }

        @Override
        public void postVisit(ASTNode node) {
            if (!L.showDebug())
                return;
            indent -= tabsize;
            super.postVisit(node);
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
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            enterScope();
            return super.visit(node);
        }

        @Override
        public void endVisit(TypeDeclaration node) {
            leaveScope();
            super.visit(node);
        }

        @Override
        public boolean visit(MethodDeclaration node) {
            enterScope();
            return super.visit(node);
        }

        @Override
        public void endVisit(MethodDeclaration node) {
            leaveScope();
            super.visit(node);
        }

        @Override
        public boolean visit(Block node) {
            enterScope();
            return super.visit(node);
        }

        @Override
        public void endVisit(Block node) {
            leaveScope();
            super.visit(node);
        }

        @Override
        public boolean visit(TypeParameter node) {
            SimpleName name = node.getName();
            typens.put(name.getIdentifier(), node);
            return super.visit(node);
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

    }

    class Visitor extends ASTFrameVisitor {

        protected ASTRewrite rewrite;
        protected ASTUtils   AU;

        public Visitor(ASTRewrite rewrite) {
            this.rewrite = rewrite;
            this.AU = new ASTUtils(rewrite.getAST());
        }

        /** &lt;T, ...> => EMPTY */
        @Override
        public boolean visit(TypeParameter node) {
            rewrite.remove(node, null);
            return super.visit(node);
        }

        /** Type&lt;T> => Type */
        @Override
        public boolean visit(ParameterizedType node) {
            Type type = node.getType();
            if (type instanceof SimpleType)
                type = expandMajor((SimpleType) type);
            L.d.P(indent(), "reduce ", node, " => ", type);
            rewrite.replace(node, type, null);
            return super.visit(node);
        }

        /** T => Type */
        @Override
        public boolean visit(SimpleType node) {
            Type extype = expandMajor(node);
            L.d.P(indent(), "resolved ", node, " => ", extype);
            if (extype != node) {
                rewrite.replace(node, extype, null);
            }
            return super.visit(node);
        }

        /** throws T -> throws Type */
        @Override
        public boolean visit(MethodDeclaration node) {
            for (Object _exTypeName : node.thrownExceptions()) {
                if (_exTypeName instanceof SimpleName) {
                    SimpleName exTypeName = (SimpleName) _exTypeName;
                    Type extype = expandMajor(exTypeName);
                    L.d.P(indent(), "resolved ", node, " => ", extype);
                    if (extype instanceof SimpleType) {
                        SimpleType sim = (SimpleType) extype;
                        rewrite.replace(exTypeName, sim.getName(), null);
                    }
                }
            }
            return super.visit(node);
        }

        /**
         * <pre>
         * for (itvar : iterable)
         *     BODY
         * =&gt;
         * Iterator iter_ID = iterable.iterator();
         * while (iter_ID.hasNext()) {
         *     itvar = (itvar_Type) iter_ID.next();
         *     BODY_rest
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(EnhancedForStatement node) {
            // AST ast0 = node.getAST();
            AST ast = rewrite.getAST();

            CompilationUnit cu = (CompilationUnit) node.getRoot();
            SingleVariableDeclaration _itvar = node.getParameter();
            Expression iterable = AU.copy(node.getExpression());
            Statement _body = node.getBody();

            VariableDeclarationStatement iterDecl;
            SimpleName _iterName = ast.newSimpleName("_iter" + (++iterIndex));
            {
                VariableDeclarationFragment iterDecl_f = ast
                        .newVariableDeclarationFragment();
                iterDecl_f.setName(AU.copy(_iterName));
                MethodInvocation iterable_iterator = ast.newMethodInvocation();
                iterable_iterator.setExpression(iterable);
                iterable_iterator.setName(ast.newSimpleName("iterator"));
                iterDecl_f.setInitializer(iterable_iterator);

                iterDecl = ast.newVariableDeclarationStatement(iterDecl_f);
                Type Iterator_ = AU.newImportedType(rewrite, cu,
                        "java.util.Iterator");
                iterDecl.setType(Iterator_);
            }

            WhileStatement while_ = ast.newWhileStatement();
            {
                MethodInvocation iter_hasNext = ast.newMethodInvocation();
                iter_hasNext.setExpression(AU.copy(_iterName));
                iter_hasNext.setName(ast.newSimpleName("hasNext"));

                MethodInvocation next_ = ast.newMethodInvocation();
                next_.setExpression(AU.copy(_iterName));
                next_.setName(ast.newSimpleName("next"));

                Type _type = _itvar.getType();
                VariableDeclarationFragment itvar_f = ast
                        .newVariableDeclarationFragment();
                itvar_f.setName(AU.copy(_itvar.getName()));
                CastExpression casted = ast.newCastExpression();
                casted.setExpression(next_);
                casted.setType(AU.copy(_type));
                itvar_f.setInitializer(casted);

                VariableDeclarationStatement itvar_Next = ast
                        .newVariableDeclarationStatement(itvar_f);
                itvar_Next.setType(AU.copy(_type));

                Block whileBody;
                Statement body = (Statement) rewrite.createMoveTarget(_body);
                if (body instanceof Block) {
                    whileBody = (Block) body;
                    ListRewrite statements = rewrite.getListRewrite(body,
                            Block.STATEMENTS_PROPERTY);
                    statements.insertFirst(itvar_Next, null);
                } else {
                    whileBody = ast.newBlock();
                    List<Statement> statements = whileBody.statements();
                    statements.add(0, itvar_Next);
                    statements.add(body);
                }

                while_.setExpression(iter_hasNext);
                while_.setBody(whileBody);
            }

            Block block2 = ast.newBlock();
            List<Statement> statements = block2.statements();
            statements.add(iterDecl);
            statements.add(while_);
            rewrite.replace(node, block2, null);
            return super.visit(node);
        }

        private int iterIndex = 0;

        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(AssertStatement node) {
            AST ast = node.getAST();
            Expression exp = AU.copy(node.getExpression());
            if (!(exp instanceof ParenthesizedExpression)) {
                ParenthesizedExpression _exp = ast.newParenthesizedExpression();
                _exp.setExpression(exp);
                exp = _exp;
            }
            Expression _msg = node.getMessage();

            // throw "null buffer".new AssertionError();
            // if (! (exp)) throw new AssertionError([msg]);
            IfStatement if_ = ast.newIfStatement();
            {
                PrefixExpression not = ast.newPrefixExpression();
                not.setOperator(Operator.NOT);
                not.setOperand(exp);
                if_.setExpression(not);

                ThrowStatement throw_ = ast.newThrowStatement();
                ClassInstanceCreation new_Error = ast
                        .newClassInstanceCreation();
                new_Error.setType(ast.newSimpleType(ast
                        .newSimpleName("AssertionError")));
                if (_msg != null)
                    new_Error.arguments().add(AU.copy(_msg));
                throw_.setExpression(new_Error);
                if_.setThenStatement(throw_);
            }

            // rewrite.remove(node, null);
            rewrite.replace(node, if_, null);
            return super.visit(node);
        }

        @Override
        protected boolean visitExpression(Expression e) {
            ITypeBinding b = e.resolveTypeBinding();
            if (b == null) {
                L.d.P(indent(), "no bind");
                return true;
            }
            L.d.P(indent(), "bind-fqn", b.getQualifiedName());
            L.d.P(indent(), "bind-bin", b.getBinaryName());
            L.d.P(indent(), "bind-bounds", b.getBound());
            L.d.P(indent(), "bind-erasure", b.getErasure());
            L.d.P(indent(), "bind-key", b.getKey());
            return true;
        }

    }

}
