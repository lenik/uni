package net.bodz.uni.c.jdt;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import net.bodz.bas.err.IllegalUsageException;

public class ASTUtils {

    protected AST ast;
    protected ASTRewrite rewrite;

    public ASTUtils(AST ast, ASTRewrite rewrite) {
        this.ast = ast;
        this.rewrite = rewrite;
    }

    public ASTUtils(ASTRewrite rewrite) {
        this(rewrite.getAST(), rewrite);
    }

    public <T extends ASTNode> T copy(T node) {
        return (T) ASTNode.copySubtree(ast, node);
    }

    public List<ASTNode> copy(List<?> list) {
        List<ASTNode> copy = new ArrayList<ASTNode>(list.size());
        for (Object o : list) {
            if (o instanceof ASTNode) {
                ASTNode nodeCopy = copy((ASTNode) o);
                copy.add(nodeCopy);
            } else
                throw new IllegalUsageException();
        }
        return copy;
    }

    public <T extends ASTNode> T copyRef(T node) {
        return (T) rewrite.createCopyTarget(node);
    }

    /** copyRef for re-resolv */
    public <T extends ASTNode> T copyRef2(T node) {
        return copy(node);
    }

    public <T extends ASTNode> T moveRef(T node) {
        return (T) rewrite.createMoveTarget(node);
    }

    public Name newName(String name) {
        return ast.newName(name.split("\\."));
    }

    public TypeLiteral newTypeLiteral(Type type) {
        TypeLiteral literal = ast.newTypeLiteral();
        literal.setType(type);
        return literal;
    }

    public Type newType(Class<?> type) {
        if (type.isPrimitive()) {
            Code code = PrimitiveType.toCode(type.getSimpleName());
            return ast.newPrimitiveType(code);
        }
        return ast.newSimpleType(ast.newSimpleName(type.getSimpleName()));
    }

    public Type newType(String name) {
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return ast.newSimpleType(ast.newSimpleName(name));
        SimpleName term = ast.newSimpleName(name.substring(dot + 1));
        name = name.substring(0, dot);
        return ast.newQualifiedType(newType(name), term);
    }

    public Type newImportedType(CompilationUnit unit, Class<?> clazz) {
        return newImportedType(unit, clazz.getName());
    }

    public Type newImportedType(CompilationUnit unit, String name) {
        AST ast = unit.getAST();
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return ast.newSimpleType(ast.newSimpleName(name));
        SimpleName term = ast.newSimpleName(name.substring(dot + 1));
        Type type = ast.newSimpleType(term);
        addImport(unit, name);
        return type;
    }

    public void addImport(CompilationUnit unit, Class<?> clazz) {
        addImport(unit, clazz.getName());
    }

    public void addImport(CompilationUnit cu, String name) {
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return;
        String _package = name.substring(0, dot);
        if ("java.lang".equals(_package))
            return;

        ListRewrite imports = rewrite.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
        for (Object o : imports.getOriginalList()) {
            ImportDeclaration id = (ImportDeclaration) o;
            String fqn = id.getName().getFullyQualifiedName();
            if (fqn.equals(name))
                return;
        }
        for (Object o : imports.getRewrittenList()) {
            // ?? is rewritten includes the original list?
            ImportDeclaration id = (ImportDeclaration) o;
            String fqn = id.getName().getFullyQualifiedName();
            if (fqn.equals(name))
                return;
        }
        AST ast = rewrite.getAST();
        ImportDeclaration id = ast.newImportDeclaration();
        id.setName(newName(name));
        imports.insertLast(id, null);
    }

    public void addModifiers(BodyDeclaration decl, int mod) {
        addModifiers(decl.modifiers(), mod);
    }

    public void addModifiers(List<IExtendedModifier> mods, int mod) {
        if (Modifier.isPublic(mod))
            mods.add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
        if (Modifier.isProtected(mod))
            mods.add(ast.newModifier(ModifierKeyword.PROTECTED_KEYWORD));
        if (Modifier.isPrivate(mod))
            mods.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
        if (Modifier.isAbstract(mod))
            mods.add(ast.newModifier(ModifierKeyword.ABSTRACT_KEYWORD));
        if (Modifier.isStatic(mod))
            mods.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
        if (Modifier.isFinal(mod))
            mods.add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));
        if (Modifier.isNative(mod))
            mods.add(ast.newModifier(ModifierKeyword.NATIVE_KEYWORD));
        if (Modifier.isTransient(mod))
            mods.add(ast.newModifier(ModifierKeyword.TRANSIENT_KEYWORD));
        if (Modifier.isVolatile(mod))
            mods.add(ast.newModifier(ModifierKeyword.VOLATILE_KEYWORD));
        if (Modifier.isSynchronized(mod))
            mods.add(ast.newModifier(ModifierKeyword.SYNCHRONIZED_KEYWORD));
        if (Modifier.isStrict(mod))
            mods.add(ast.newModifier(ModifierKeyword.STRICTFP_KEYWORD));
    }

}
