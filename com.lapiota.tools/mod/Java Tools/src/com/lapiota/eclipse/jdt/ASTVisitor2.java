package com.lapiota.eclipse.jdt;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

class ASTVisitor2 extends ASTVisitor {

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
