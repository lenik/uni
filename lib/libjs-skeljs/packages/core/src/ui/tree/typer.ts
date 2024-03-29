
import type { UiNode } from "../ui-node";

export interface NodeTypes {
    style?: any;
}

export type NodeTyperFunc = (node: UiNode<any>) => NodeTypes;

export function defaultTyper(node: UiNode<any>): NodeTypes {
    let types: NodeTypes = {};
    return types;
}
