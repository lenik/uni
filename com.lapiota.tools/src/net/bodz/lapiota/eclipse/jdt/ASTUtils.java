package net.bodz.lapiota.eclipse.jdt;

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

    protected AST ast;

    public ASTUtils(AST ast) {
        this.ast = ast;
    }

    @SuppressWarnings("unchecked")
    public <T extends ASTNode> T copy(T node) {
        return (T) ASTNode.copySubtree(ast, node);
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

    public Type newImportedType(ASTRewrite rewrite, CompilationUnit cu,
            String name) {
        AST ast = cu.getAST();
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return ast.newSimpleType(ast.newSimpleName(name));
        SimpleName term = ast.newSimpleName(name.substring(dot + 1));
        Type type = ast.newSimpleType(term);
        addImport(rewrite, cu, name);
        return type;
    }

    public void addImport(ASTRewrite rewrite, CompilationUnit cu, String name) {
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
