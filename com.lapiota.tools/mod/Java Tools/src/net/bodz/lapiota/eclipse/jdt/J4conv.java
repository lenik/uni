package net.bodz.lapiota.eclipse.jdt;

import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.INCREMENT;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.NOT;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.dnb.JavaAnnotation;
import net.bodz.bas.dnb.JavaEnum;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.UnexpectedException;
import net.bodz.bas.lang.util.Classpath;
import net.bodz.bas.types.chained.CMap;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

@Doc("Remove Java 5 Generics from the java source files")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@RunInfo(

_load = { "findcp|eclipse*/plugins/org.eclipse.jdt.core_*",
        "findcp|eclipse*/plugins/org.eclipse.text_*", },

load = { "findcp|eclipse*/plugins/org.eclipse.equinox.common_*",
        "findcp|eclipse*/plugins/org.eclipse.core.resources_*",
        "findcp|eclipse*/plugins/org.eclipse.core.jobs_*",
        "findcp|eclipse*/plugins/org.eclipse.core.runtime_*",
        "findcp|eclipse*/plugins/org.eclipse.osgi_*",
        "findcp|eclipse*/plugins/org.eclipse.core.contenttype_*",
        "findcp|eclipse*/plugins/org.eclipse.equinox.preferences_*", })
public class J4conv extends BatchProcessCLI {

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

    // aliases
    static class FMT extends DefaultCodeFormatterConstants {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ProcessResult doFileEdit(File in, File out) throws Throwable {
        if (!"java".equals(Files.getExtension(in)))
            return null;

        String src = Files.readAll(in, inputEncoding);
        char[] srcChars = src.toCharArray();

        Map options = JavaCore.getOptions();
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
        TextEdit edit = rewrite.rewriteAST(doc, null);
        edit.apply(doc);
        String dst = doc.get();

        // format...
        boolean format = true;
        if (format) {
            Map fopts = DefaultCodeFormatterConstants
                    .getEclipseDefaultSettings();
            fopts.put(FMT.FORMATTER_INDENTATION_SIZE, 4);
            fopts.put(FMT.FORMATTER_TAB_CHAR, JavaCore.SPACE);
            fopts.put(FMT.FORMATTER_ALIGN_TYPE_MEMBERS_ON_COLUMNS, FMT.TRUE);

            CodeFormatter formatter = ToolFactory.createCodeFormatter(fopts);
            String lineSeparator = System.getProperty("line.separator");
            doc = new Document(dst);
            // dst = "class A {}";
            edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, //
                    dst, 0, dst.length(), 0, lineSeparator);
            if (edit != null) {
                edit.apply(doc);
                dst = doc.get();
            } else {
                L.m.p("[FERR] ", in);
            }
        }

        Files.write(out, dst, outputEncoding);
        return ProcessResult.compareAndSave();
    }

    class ASTFrameVisitor extends ASTVisitor2 {

        protected ASTRewrite           rewrite;
        protected AST                  ast;
        protected ASTUtils             AU;

        protected CompilationUnit      unit;

        protected Set<String>          importPackages;
        protected Map<String, String>  statics;

        /** TypeParameter, Class<?> */
        protected CMap<String, Object> typens;
        // /** Method, List<Method> */
        // protected CMap<String, Object> funns;
        /** Type */
        protected CMap<String, Type>   varns;

        protected int                  indent;
        private int                    tabsize = 2;

        public ASTFrameVisitor(ASTRewrite rewrite) {
            this.rewrite = rewrite;
            this.ast = rewrite.getAST();
            this.AU = new ASTUtils(rewrite.getAST(), rewrite);
        }

        @Override
        public boolean visit(CompilationUnit node) {
            unit = node;
            importPackages = new HashSet<String>();
            statics = new HashMap<String, String>();
            typens = new CMap<String, Object>();
            // funns = new CMap<String, Object>();
            varns = new CMap<String, Type>();
            indent = 0;
            return super.visit(node);
        }

        @Override
        public boolean visit(ImportDeclaration node) {
            Name name = node.getName();
            String fqn = node.getName().getFullyQualifiedName();
            if (node.isStatic()) {
                int dot = fqn.lastIndexOf('.');
                assert dot != -1;
                String clazz = fqn.substring(0, dot);
                AU.addImport(unit, clazz);

                // NOT ACCURACY
                String member = fqn.substring(dot + 1);
                dot = clazz.lastIndexOf('.');
                if (dot != -1)
                    clazz = clazz.substring(dot + 1);
                String access = clazz + "." + member;
                statics.put(member, access);

                rewrite.remove(node, null);
                return super.visit(node);
            }
            Object imported = resolveImport2(fqn);
            if (imported == null) {
                Package _package = Package.getPackage(fqn);
                if (_package == null)
                    throw new RuntimeException("can't import " + fqn);
                importPackages.add(fqn);
            } else if (imported instanceof Class) {
                SimpleName sname = name.isSimpleName() ? ((SimpleName) name)
                        : ((QualifiedName) name).getName();
                typens.put(sname.getIdentifier(), imported);
            }
            return super.visit(node);
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
            // else if (imported instanceof List)
            // funns.put(name, imported);
            // else if (imported instanceof Field)
            // varns.put(name, imported);
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
            // L.d.P(indent(), "enter-f=", //
            // Strings.ellipse(funns.toString(), 100));
            L.d.P(indent(), "enter-v=", //
                    Strings.ellipse(varns.toString(), 100));
            typens.enterNew();
            // funns.enterNew();
            varns.enterNew();
            if (!L.showDebug())
                indent += tabsize;
        }

        void leaveScope() {
            L.d.P(indent(), "leave-t=", //
                    Strings.ellipse(typens.toString(), 100));
            // L.d.P(indent(), "leave-f=", //
            // Strings.ellipse(funns.toString(), 100));
            L.d.P(indent(), "leave-v=", //
                    Strings.ellipse(varns.toString(), 100));
            typens.leave();
            // funns.leave();
            varns.leave();
            if (!L.showDebug())
                indent -= tabsize;
        }

        Type expandMajor(Type type) {
            // if (type instanceof ParameterizedType)
            // type = ((ParameterizedType) type).getType();
            if (type instanceof SimpleType)
                return expandMajor((SimpleType) type);
            return type;
        }

        Type expandMajor(SimpleType type) {
            if (!(type.getName() instanceof SimpleName))
                return type;
            SimpleName name = (SimpleName) type.getName();
            Type expanded = expandMajor(name);
            if (expanded == null)
                return type;
            L.d.P(indent(), "expand ", type, " => ", expanded);
            return expanded;
        }

        Type expandMajor(SimpleName name) {
            Object _parameter = typens.get(name.getIdentifier());
            if (_parameter instanceof TypeParameter) {
                TypeParameter parameter = (TypeParameter) _parameter;
                List<?> bounds = parameter.typeBounds();
                if (bounds.isEmpty()) {
                    return AU.newType(Object.class);
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
            if (node.isVarargs())
                type = ast.newArrayType(AU.copy(type));
            varns.put(name.getIdentifier(), type);
            return super.visit(node);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(VariableDeclarationStatement node) {
            Type type = node.getType();
            List<VariableDeclarationFragment> fragments = node.fragments();
            for (VariableDeclarationFragment fragment : fragments) {
                SimpleName name = fragment.getName();
                varns.put(name.getIdentifier(), type);
            }
            return super.visit(node);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(VariableDeclarationExpression node) {
            Type type = node.getType();
            List<VariableDeclarationFragment> fragments = node.fragments();
            for (VariableDeclarationFragment fragment : fragments) {
                SimpleName name = fragment.getName();
                varns.put(name.getIdentifier(), type);
            }
            return super.visit(node);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(FieldDeclaration node) {
            Type type = node.getType();
            List<VariableDeclarationFragment> fragments = node.fragments();
            for (VariableDeclarationFragment fragment : fragments) {
                SimpleName name = fragment.getName();
                varns.put(name.getIdentifier(), type);
            }
            return super.visit(node);
        }

    }

    class Visitor extends ASTFrameVisitor {

        private int forIndex     = 0;
        private int forIterIndex = 0;

        public Visitor(ASTRewrite rewrite) {
            super(rewrite);
        }

        @Override
        public boolean visit(CompilationUnit node) {
            forIndex = 0;
            forIterIndex = 0;
            return super.visit(node);
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
            Type type = expandMajor(node.getType());
            rewrite.replace(node, type, null);
            return super.visit(node);
        }

        /** T => Type */
        @Override
        public boolean visit(SimpleType node) {
            Type extype = expandMajor(node);
            if (extype != node)
                rewrite.replace(node, extype, null);
            return super.visit(node);
        }

        @Override
        public boolean visit(SimpleName node) {
            ASTNode parent = node.getParent();
            do {
                if (parent instanceof Name)
                    return super.visit(node);
                if (parent instanceof FieldAccess)
                    break;
                return super.visit(node);
            } while (false);
            // matched
            String access = statics.get(node.getIdentifier());
            if (access != null) {
                int dot = access.lastIndexOf('.');
                String clazz = access.substring(0, dot);
                String name = access.substring(dot + 1);
                FieldAccess faccess = ast.newFieldAccess();
                faccess.setExpression(ast.newSimpleName(clazz));
                faccess.setName(ast.newSimpleName(name));
                // faccess.accept(this);
                rewrite.replace(node, faccess, null);
                return false;
            }
            return super.visit(node);
        }

        /** throws T -> throws Type */
        @SuppressWarnings("unchecked")
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
            if (node.isVarargs()) {
                List params = node.parameters();
                SingleVariableDeclaration last = (SingleVariableDeclaration) params
                        .get(params.size() - 1);
                SingleVariableDeclaration last2 = ast
                        .newSingleVariableDeclaration();
                Type _type = expandMajor(last.getType());
                ArrayType type = ast.newArrayType(AU.copy(_type));
                last2.setType(type);
                last2.setVarargs(false);
                last2.setName(AU.moveRef(last.getName()));
                last2.modifiers().addAll(AU.copy(last.modifiers()));
                rewrite.replace(last, last2, null);
            }
            return super.visit(node);
        }

        /**
         * <pre>
         * invoke(fixed-arguments, T vararg1, T vararg2, ...)
         * =&gt;
         * invoke(fixed-arguments, new T[] { vararg1, vararg2, ... })
         * </pre>
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(MethodInvocation node) {
            if (node.getExpression() == null) {
                // imported static method?
                String name = node.getName().getIdentifier();
                String access = statics.get(name);
                if (access != null) {
                    MethodInvocation destatic = AU.copyRef2(node);
                    int dot = access.lastIndexOf('.');
                    String clazz = access.substring(0, dot);
                    name = access.substring(dot + 1);
                    destatic.setExpression(ast.newSimpleName(clazz));
                    destatic.setName(ast.newSimpleName(name));
                    destatic.accept(this);
                    rewrite.replace(node, destatic, null);
                    return false;
                }
            }
            IMethodBinding binding = node.resolveMethodBinding();
            if (binding != null && binding.isVarargs()) {
                ITypeBinding[] args = binding.getParameterTypes();
                int varoff = args.length - 1;
                ITypeBinding vararg = args[varoff];
                ListRewrite vargs = rewrite.getListRewrite(node,
                        MethodInvocation.ARGUMENTS_PROPERTY);
                ArrayCreation newArray = ast.newArrayCreation();
                // new_Array.setType(vararg.getTypeBounds())
                ArrayInitializer initArray = ast.newArrayInitializer();
                List initList = initArray.expressions();
                List callList = node.arguments();
                for (int i = varoff; i < callList.size(); i++) {
                    ASTNode callArg = (ASTNode) callList.get(i);
                    initList.add(AU.moveRef(callArg));
                    vargs.remove(callArg, null);
                }
                newArray.setInitializer(initArray);
                vargs.insertLast(newArray, null);
            }
            return super.visit(node);
        }

        /**
         * <pre>
         * for (itvar : iterable)
         *     BODY
         * =&gt;
         * for (int index_ID = 0; index_ID &lt; iterable.length; index_ID++) {
         *     itvar = (itvar_Type) iterable[index_ID];
         *     BODY_rest
         * }
         *
         * (or)
         *
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
            SingleVariableDeclaration _itvar = node.getParameter();
            Type _type = _itvar.getType(); // expandMajor
            Expression iterable = AU.copyRef2(node.getExpression());
            Statement _body = node.getBody();

            boolean isArray = false;
            if (iterable instanceof SimpleName) {
                Type type = varns.get(((SimpleName) iterable).getIdentifier());
                if (type != null) {
                    isArray = type.isArrayType();
                }
            }

            if (isArray) {
                SimpleName _array = (SimpleName) iterable;
                ForStatement _for = ast.newForStatement();
                SimpleName _indexName = ast.newSimpleName("_index"
                        + (++forIndex));
                List<Expression> inits = _for.initializers();
                {
                    VariableDeclarationFragment forInit_f = ast
                            .newVariableDeclarationFragment();
                    forInit_f.setName(AU.copy(_indexName));
                    forInit_f.setInitializer(ast.newNumberLiteral("0"));
                    VariableDeclarationExpression forInit = ast
                            .newVariableDeclarationExpression(forInit_f);
                    inits.add(forInit);
                }
                List<Expression> updaters = _for.updaters();
                {
                    PrefixExpression forUpdate = ast.newPrefixExpression();
                    forUpdate.setOperator(INCREMENT);
                    forUpdate.setOperand(AU.copy(_indexName));
                    updaters.add(forUpdate);
                }
                InfixExpression forTest = ast.newInfixExpression();
                {
                    forTest.setOperator(LESS);
                    forTest.setLeftOperand(AU.copy(_indexName));
                    FieldAccess arrayLength = ast.newFieldAccess();
                    arrayLength.setExpression(AU.copy(_array));
                    arrayLength.setName(ast.newSimpleName("length"));
                    forTest.setRightOperand(arrayLength);
                    _for.setExpression(forTest);
                }
                VariableDeclarationStatement itvar;
                {
                    VariableDeclarationFragment itvar_f = ast
                            .newVariableDeclarationFragment();
                    itvar_f.setName(AU.copy(_itvar.getName()));
                    ArrayAccess arrayAccess = ast.newArrayAccess();
                    arrayAccess.setArray(AU.copy(_array));
                    arrayAccess.setIndex(AU.copy(_indexName));
                    itvar_f.setInitializer(arrayAccess);
                    itvar = ast.newVariableDeclarationStatement(itvar_f);
                    itvar.setType(AU.copy(_type));
                }
                Block forBody;
                if (_body instanceof Block) {
                    forBody = AU.copyRef2((Block) _body);
                    List<Statement> statements = forBody.statements();
                    // ListRewrite statements = rewrite.getListRewrite(_body,
                    // Block.STATEMENTS_PROPERTY);
                    // statements.insertFirst(itvar, null);
                    statements.add(0, itvar);
                } else {
                    forBody = ast.newBlock();
                    List<Statement> statements = forBody.statements();
                    statements.add(0, itvar);
                    statements.add(AU.copyRef2(_body));
                }
                _for.setBody(forBody);
                _for.accept(this);
                rewrite.replace(node, _for, null);
            } else {
                VariableDeclarationStatement iterDecl;
                SimpleName _iterName = ast.newSimpleName("_iter"
                        + (++forIterIndex));
                {
                    VariableDeclarationFragment iterDecl_f = ast
                            .newVariableDeclarationFragment();
                    iterDecl_f.setName(AU.copy(_iterName));
                    MethodInvocation iterable_iterator = ast
                            .newMethodInvocation();
                    iterable_iterator.setExpression(iterable);
                    iterable_iterator.setName(ast.newSimpleName("iterator"));
                    iterDecl_f.setInitializer(iterable_iterator);

                    iterDecl = ast.newVariableDeclarationStatement(iterDecl_f);
                    iterDecl.setType(AU.newImportedType(unit, Iterator.class));
                }

                WhileStatement while_ = ast.newWhileStatement();
                {
                    MethodInvocation iter_hasNext = ast.newMethodInvocation();
                    iter_hasNext.setExpression(AU.copy(_iterName));
                    iter_hasNext.setName(ast.newSimpleName("hasNext"));

                    MethodInvocation next_ = ast.newMethodInvocation();
                    next_.setExpression(AU.copy(_iterName));
                    next_.setName(ast.newSimpleName("next"));

                    VariableDeclarationFragment itvar_f = ast
                            .newVariableDeclarationFragment();
                    itvar_f.setName(AU.copyRef(_itvar.getName()));
                    CastExpression casted = ast.newCastExpression();
                    casted.setExpression(next_);
                    casted.setType(AU.copy(_type));
                    itvar_f.setInitializer(casted);

                    VariableDeclarationStatement itvar_Next = ast
                            .newVariableDeclarationStatement(itvar_f);
                    itvar_Next.setType(AU.copy(_type));
                    itvar_Next.modifiers().addAll(AU.copy(_itvar.modifiers()));

                    Block whileBody;
                    if (_body instanceof Block) {
                        whileBody = AU.copyRef2((Block) _body);
                        // ListRewrite statements =
                        // rewrite.getListRewrite(_body,
                        // Block.STATEMENTS_PROPERTY);
                        // statements.insertFirst(itvar_Next, null);
                        List<Statement> statements = whileBody.statements();
                        statements.add(0, itvar_Next);
                    } else {
                        whileBody = ast.newBlock();
                        List<Statement> statements = whileBody.statements();
                        statements.add(0, itvar_Next);
                        statements.add(AU.copyRef2(_body));
                    }
                    while_.setExpression(iter_hasNext);
                    while_.setBody(whileBody);
                }

                Block block2 = ast.newBlock();
                List<Statement> statements = block2.statements();
                statements.add(iterDecl);
                statements.add(while_);
                block2.accept(this);
                rewrite.replace(node, block2, null);
            }
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

        /**
         * <pre>
         * assert exp [: msg];
         * =&gt;
         * if (! (exp)) throw new AssertionError([msg]);
         * </pre>
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(AssertStatement node) {
            Expression exp = AU.moveRef(node.getExpression());
            if (!(exp instanceof ParenthesizedExpression)) {
                ParenthesizedExpression _exp = ast.newParenthesizedExpression();
                _exp.setExpression(exp);
                exp = _exp;
            }
            Expression _msg = node.getMessage();

            IfStatement if_ = ast.newIfStatement();
            {
                PrefixExpression not = ast.newPrefixExpression();
                not.setOperator(NOT);
                not.setOperand(exp);
                if_.setExpression(not);

                ThrowStatement throw_ = ast.newThrowStatement();
                ClassInstanceCreation new_Error = ast
                        .newClassInstanceCreation();
                new_Error.setType(AU.newType(AssertionError.class));
                if (_msg != null)
                    new_Error.arguments().add(AU.moveRef(_msg));
                throw_.setExpression(new_Error);
                if_.setThenStatement(throw_);
            }

            // rewrite.remove(node, null);
            rewrite.replace(node, if_, null);
            return super.visit(node);
        }

        /**
         * <pre>
         * &#064;interface A
         * =&gt;
         * class A extends JavaAnnotation
         * </pre>
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(AnnotationTypeDeclaration aTypeDecl) {
            TypeDeclaration cTypeDecl = ast.newTypeDeclaration();
            cTypeDecl.setInterface(false);
            cTypeDecl.setName(AU.copyRef2(aTypeDecl.getName()));
            cTypeDecl.modifiers().addAll(AU.copy(aTypeDecl.modifiers()));
            cTypeDecl.setSuperclassType(AU.newImportedType(unit,
                    JavaAnnotation.class));

            List<BodyDeclaration> aBody = aTypeDecl.bodyDeclarations();
            List<BodyDeclaration> cBody = cTypeDecl.bodyDeclarations();
            List<FieldDeclaration> cFields = new ArrayList<FieldDeclaration>();
            List<MethodDeclaration> cMethods = new ArrayList<MethodDeclaration>();
            for (BodyDeclaration a : aBody) {
                if (a instanceof AnnotationTypeMemberDeclaration) {
                    AnnotationTypeMemberDeclaration aDecl = (AnnotationTypeMemberDeclaration) a;
                    for (BodyDeclaration cDecl : wrapAMember(aDecl)) {
                        if (cDecl instanceof FieldDeclaration)
                            cFields.add((FieldDeclaration) cDecl);
                        else
                            cMethods.add((MethodDeclaration) cDecl);
                    }
                } else
                    cBody.add(AU.copyRef2(a));
            }
            for (FieldDeclaration decl : cFields)
                cBody.add(decl);
            for (MethodDeclaration decl : cMethods)
                cBody.add(decl);

            Javadoc javadoc = aTypeDecl.getJavadoc();
            if (javadoc != null)
                cTypeDecl.setJavadoc(AU.moveRef(javadoc));

            cTypeDecl.accept(this);
            rewrite.replace(aTypeDecl, cTypeDecl, null);
            return super.visit(aTypeDecl);
        }

        @SuppressWarnings("unchecked")
        public List<BodyDeclaration> wrapAMember(
                AnnotationTypeMemberDeclaration aDecl) {
            Type _type = expandMajor(aDecl.getType());
            SimpleName _name = aDecl.getName();
            Expression _default = aDecl.getDefault();
            Javadoc _javadoc = aDecl.getJavadoc();

            FieldDeclaration cField;
            {
                VariableDeclarationFragment cFieldFrag = ast
                        .newVariableDeclarationFragment();
                cFieldFrag.setName(AU.copyRef2(_name));
                if (_default != null) {
                    cFieldFrag.setInitializer(AU.copyRef2(_default));
                }
                cField = ast.newFieldDeclaration(cFieldFrag);
                cField.setType(AU.copyRef2(_type));
                AU.addModifiers(cField, Modifier.PRIVATE);
            }
            String ucName = Strings.ucfirst(_name.getIdentifier());
            MethodDeclaration cGetter = ast.newMethodDeclaration();
            { /* return FIELD; */
                cGetter.setReturnType2(AU.copyRef2(_type));
                cGetter.setName(AU.copy(_name)); // ast.newSimpleName("get" +
                // ucName));
                Block block = ast.newBlock();
                List<Statement> statements = block.statements();
                ReturnStatement returnField = ast.newReturnStatement();
                returnField.setExpression(AU.copyRef2(_name));
                statements.add(returnField);
                cGetter.setBody(block);
                AU.addModifiers(cGetter, Modifier.PUBLIC | Modifier.FINAL);
            }
            MethodDeclaration cSetter = ast.newMethodDeclaration();
            { /* this.FIELD = newval; */
                cSetter.setName(ast.newSimpleName("set" + ucName));

                List<SingleVariableDeclaration> cSetterArgs = cSetter
                        .parameters();
                SingleVariableDeclaration setArgDecl = ast
                        .newSingleVariableDeclaration();
                setArgDecl.setType(AU.copyRef2(_type));
                SimpleName newval = ast.newSimpleName("newval");
                setArgDecl.setName(AU.copy(newval));
                cSetterArgs.add(setArgDecl);

                Block block = ast.newBlock();
                List<Statement> statements = block.statements();
                Assignment assignment = ast.newAssignment();
                FieldAccess thisField = ast.newFieldAccess();
                thisField.setExpression(ast.newThisExpression());
                thisField.setName(AU.copyRef2(_name));
                assignment.setLeftHandSide(thisField);
                assignment.setRightHandSide(AU.copy(newval));
                statements.add(ast.newExpressionStatement(assignment));
                cSetter.setBody(block);
                AU.addModifiers(cSetter, Modifier.PUBLIC | Modifier.FINAL);
            }

            if (_javadoc != null) {
                // cField.setJavadoc(AU.copyRef(_javadoc));
                cGetter.setJavadoc(AU.copyRef(_javadoc));
                cSetter.setJavadoc(AU.copyRef(_javadoc));
            }

            List<BodyDeclaration> cDecls = new ArrayList<BodyDeclaration>(3);
            cDecls.add(cField);
            cDecls.add(cGetter);
            // cDecls.add(cSetter);
            return cDecls;
        }

        @Override
        protected boolean visitAnnotation(Annotation a) {
            rewrite.remove(a, null);
            return super.visitAnnotation(a);
        }

        /**
         * <pre>
         * enum E {
         *   Constants, ...;
         *   // E(args) { ... }
         *   // BODY
         * }
         * =&gt;
         * class C extends JavaEnum {
         *
         *     // if no ctors defined, then implied one.
         *     private C(String name, int ordinal) {
         *         super(name, ordinal);
         *     }
         *
         *     private C(String name, int ordinal, Object args) {
         *         super(name, ordinal);
         *         // CtorBody
         *     }
         *
         *     // BODY
         *
         *     public static final C Constants;
         *
         *     protected static void valueOf(String name) {
         *         return valueOf(ENUM.class, name);
         *     }
         *
         *     public static ENUM[] values() {
         *         return _values(ENUM.class);
         *     }
         *
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean visit(EnumDeclaration eTypeDecl) {
            String typeName = eTypeDecl.getName().getIdentifier(); // must be
            Type superType = AU.newImportedType(unit, JavaEnum.class);
            // simple
            TypeDeclaration cTypeDecl = ast.newTypeDeclaration();
            {
                cTypeDecl.setName(ast.newSimpleName(typeName));
                cTypeDecl.setSuperclassType(AU.copy(superType));
                AU.addModifiers(cTypeDecl, Modifier.PUBLIC | Modifier.FINAL);
            }

            List<BodyDeclaration> eBody = eTypeDecl.bodyDeclarations();
            List<BodyDeclaration> cBody = cTypeDecl.bodyDeclarations();
            int ctors = 0;
            for (BodyDeclaration eDecl : eBody) {
                BodyDeclaration cDecl = null;
                if (eDecl instanceof MethodDeclaration) {
                    MethodDeclaration eDeclM = (MethodDeclaration) eDecl;
                    if (eDeclM.isConstructor()) {
                        ctors++;
                        cDecl = AU.copy(eDecl);
                        MethodDeclaration cDeclM = (MethodDeclaration) cDecl;
                        prefixCtorChain(cDeclM, //
                                AU.newType(String.class), "name", //
                                AU.newType(int.class), "index");
                    }
                }
                if (cDecl == null)
                    cDecl = AU.copyRef2(eDecl);
                cBody.add(cDecl);
            }
            if (ctors == 0) {
                // implied ctor(name, oridnal): super(name, ordinal);
                MethodDeclaration impliedCtor = ast.newMethodDeclaration();
                impliedCtor.setName(ast.newSimpleName(typeName));
                impliedCtor.setConstructor(true);
                AU.addModifiers(impliedCtor, Modifier.PRIVATE);
                prefixCtorChain(impliedCtor, //
                        AU.newType(String.class), "name", //
                        AU.newType(int.class), "index");
                cBody.add(0, impliedCtor);
            }

            List<EnumConstantDeclaration> eConsts = eTypeDecl.enumConstants();
            int ordinal = 0;
            for (EnumConstantDeclaration eConst : eConsts) {
                SimpleName _name = eConst.getName();
                List<Expression> args = eConst.arguments();
                AnonymousClassDeclaration anonDecl = eConst
                        .getAnonymousClassDeclaration();

                VariableDeclarationFragment cConst_f = ast
                        .newVariableDeclarationFragment();
                {
                    cConst_f.setName(AU.copy(_name));
                    ClassInstanceCreation _new = ast.newClassInstanceCreation();
                    _new.setType(AU.newType(typeName));
                    List<Expression> _newArgs = _new.arguments();
                    StringLiteral nameQuoted = ast.newStringLiteral();
                    nameQuoted.setLiteralValue(_name.getIdentifier());
                    _newArgs.add(nameQuoted);
                    _newArgs.add(ast.newNumberLiteral("" + (++ordinal)));
                    for (Expression arg : args)
                        _newArgs.add(AU.copy(arg));
                    if (anonDecl != null)
                        _new.setAnonymousClassDeclaration(AU.copy(anonDecl));
                    cConst_f.setInitializer(_new);
                }
                FieldDeclaration cConst = ast.newFieldDeclaration(cConst_f);
                {
                    AU.addModifiers(cConst, //
                            Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
                    cConst.setType(AU.newType(typeName));
                }
                cBody.add(cConst);
            }

            MethodDeclaration valueOf_f = ast.newMethodDeclaration();
            {
                AU.addModifiers(valueOf_f, Modifier.PUBLIC | Modifier.STATIC);
                valueOf_f.setReturnType2(AU.newType(typeName));
                List<SingleVariableDeclaration> parameters = valueOf_f
                        .parameters();
                {
                    SingleVariableDeclaration varName = ast
                            .newSingleVariableDeclaration();
                    varName.setType(AU.newType(String.class));
                    varName.setName(ast.newSimpleName("name"));
                    parameters.add(varName);
                }
                Block block = valueOf_f.getBody();
                if (block == null)
                    valueOf_f.setBody(block = ast.newBlock());
                List<Statement> statements = block.statements();
                {
                    MethodInvocation super_valueOf = ast.newMethodInvocation();
                    super_valueOf.setExpression(ast.newSimpleName( //
                            JavaEnum.class.getSimpleName()));
                    super_valueOf.setName(ast.newSimpleName("valueOf"));
                    List<Expression> arguments = super_valueOf.arguments();
                    arguments.add(AU.newTypeLiteral(AU.newType(typeName)));
                    arguments.add(ast.newSimpleName("name"));
                    statements.add(ast.newExpressionStatement(super_valueOf));
                }
                cBody.add(valueOf_f);
            }
            MethodDeclaration values_f = ast.newMethodDeclaration();
            {
                AU.addModifiers(values_f, Modifier.PUBLIC | Modifier.STATIC);
                values_f.setReturnType2(ast.newArrayType(AU.newType(typeName)));
                Block block = values_f.getBody();
                if (block == null)
                    values_f.setBody(block = ast.newBlock());
                List<Statement> statements = block.statements();
                {
                    MethodInvocation super_values = ast.newMethodInvocation();
                    super_values.setExpression(ast.newSimpleName( //
                            JavaEnum.class.getSimpleName()));
                    super_values.setName(ast.newSimpleName("_values"));
                    List<Expression> arguments = super_values.arguments();
                    arguments.add(AU.newTypeLiteral(AU.newType(typeName)));
                    statements.add(ast.newExpressionStatement(super_values));
                }
                cBody.add(values_f);
            }
            cTypeDecl.accept(this);
            rewrite.replace(eTypeDecl, cTypeDecl, null);
            return false; // super.visit(eTypeDecl);
        }

        @SuppressWarnings("unchecked")
        void prefixCtorChain(MethodDeclaration ctor, Object... prefixes) {
            assert ctor.isConstructor();
            List<SingleVariableDeclaration> parameters = ctor.parameters();
            {
                int insert = 0;
                for (int i = 0; i < prefixes.length; i += 2) {
                    Type type = (Type) prefixes[i];
                    String name = (String) prefixes[i + 1];
                    SingleVariableDeclaration var = ast
                            .newSingleVariableDeclaration();
                    var.setType(AU.copy(type));
                    var.setName(ast.newSimpleName(name));
                    parameters.add(insert++, var);
                }
            }
            Block block = ctor.getBody();
            if (block == null)
                ctor.setBody(block = ast.newBlock());
            List<Statement> body = block.statements();
            {
                SuperConstructorInvocation superCtorInvoke = ast
                        .newSuperConstructorInvocation();
                List<Expression> arguments = superCtorInvoke.arguments();
                int insert = 0;
                for (int i = 0; i < prefixes.length; i += 2) {
                    String name = (String) prefixes[i + 1];
                    arguments.add(insert++, ast.newSimpleName(name));
                }
                body.add(0, superCtorInvoke);
            }
        }

    }

}
