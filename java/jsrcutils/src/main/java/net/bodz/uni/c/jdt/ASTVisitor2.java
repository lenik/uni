package net.bodz.uni.c.jdt;

import org.eclipse.jdt.core.dom.*;

public class ASTVisitor2
        extends ASTVisitor {

    protected boolean visitExpression(Expression e) {
        return true;
    }

    protected boolean visitAnnotation(Annotation a) {
        return true;
    }

    @Override
    public boolean visit(ThisExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperFieldAccess node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(FieldAccess node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(Assignment node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(ParenthesizedExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(ArrayCreation node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(ArrayAccess node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(InfixExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(InstanceofExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(PostfixExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(PrefixExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(CastExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        if (!visitExpression(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        if (!visitAnnotation(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        if (!visitAnnotation(node))
            return false;
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        if (!visitAnnotation(node))
            return false;
        return super.visit(node);
    }

}
