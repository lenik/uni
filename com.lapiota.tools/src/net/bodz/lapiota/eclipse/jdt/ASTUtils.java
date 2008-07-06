package net.bodz.lapiota.eclipse.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

public class ASTUtils {

    protected AST        ast;
    protected ASTRewrite rewrite;

    public ASTUtils(AST ast, ASTRewrite rewrite) {
        this.ast = ast;
        this.rewrite = rewrite;
    }

    public ASTUtils(ASTRewrite rewrite) {
        this(rewrite.getAST(), rewrite);
    }

    @SuppressWarnings("unchecked")
    public <T extends ASTNode> T copy(T node) {
        return (T) ASTNode.copySubtree(ast, node);
    }

    @SuppressWarnings("unchecked")
    public List copy(List list) {
        List copy = new ArrayList(list.size());
        for (Object o : list) {
            if (o instanceof ASTNode)
                o = copy((ASTNode) o);
            copy.add(o);
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public <T extends ASTNode> T copyRef(T node) {
        return (T) rewrite.createCopyTarget(node);
    }

    /** copyRef for re-resolv */
    public <T extends ASTNode> T copyRef2(T node) {
        return copy(node);
    }

    @SuppressWarnings("unchecked")
    public <T extends ASTNode> T moveRef(T node) {
        return (T) rewrite.createMoveTarget(node);
    }

    public Name newName(String name) {
        return ast.newName(name.split("\\."));
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

        ListRewrite imports = rewrite.getListRewrite(cu,
                CompilationUnit.IMPORTS_PROPERTY);
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

}
