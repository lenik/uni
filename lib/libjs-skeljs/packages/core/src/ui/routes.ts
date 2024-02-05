
import type { RouteRecordRaw, RouteComponent } from "vue-router";

import type { UiDual, UiElementMap, UiNode } from "./ui-node";
import { UiElement, extract, uiElementExtract } from "./ui-node";

export type CNode = UiNode<RouteComponent>;

export function routeRecordsFromTree(root: CNode, rootPath?: string): RouteRecordRaw[] {
    let items: RouteRecordRaw[] = [];
    convert(items, rootPath || '/', root);
    return items;
}

function convert(items: RouteRecordRaw[], path: string, node: CNode) {
    let val, children;
    if (Array.isArray(node)) {
        [val, children] = node;
    } else {
        val = node;
    }

    let item = {
        path: path,
        component: val
    };
    items.push(item);

    if (path.endsWith('/'))
        path = path.substring(0, path.length - 1);

    if (children != null)
        for (let k in children) {
            let child = children[k];
            let childPath = path + '/' + k;
            convert(items, childPath, child);
        }
}

interface HasName {
    __name?: string;
}

export function ui<Named extends HasName>(val: Named, label?: string, icon?: string): UiElementMap<Named> {
    let el = new UiElement<Named>(val, label, icon);
    let k = val.__name!;
    let map: UiElementMap<Named> = {};
    map[k] = el;
    return map;
}

export function uiExtract<T>(node: UiNode<UiDual<T>>): UiNode<T> {
    return extract<UiDual<T>, T>(uiElementExtract, node);
}
