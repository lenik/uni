package net.bodz.lapiota.eclipse.jdt;

public class ASTUtils {

    // @SuppressWarnings("unchecked")
    // public static void repl(ASTNode old, ASTNode node) {
    // // old.getAST();
    // ASTNode parent = old.getParent();
    // if (parent == null)
    // throw new IllegalArgumentException("must be child node: " + old);
    // if (node.getParent() != null) {
    // ASTNode copy = ASTNode.copySubtree(node.getAST(), node);
    // node = copy;
    // }
    // StructuralPropertyDescriptor location = old.getLocationInParent();
    // if (location.isChildListProperty()) {
    // List list = (List) parent.getStructuralProperty(location);
    // int index = list.indexOf(old);
    // assert index != -1;
    // list.set(index, node);
    // } else {
    // parent.setStructuralProperty(location, node);
    // }
    // }

}
