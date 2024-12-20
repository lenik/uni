
export type UiNode<T> = [T, UiNodeMap<T>?, UiNodeOptions?] | T;

export type UiNodeMap<T> = {
    [path: string]: UiNode<T>
}

export type UiNodeOptions = {
    [key: string]: any

    icon?: string
    label?: string

    style?: CSSStyleDeclaration
    listStyle?: string
}

export type ExtractFunc<S, T>
    = (value: S, node: UiNode<S>) => UiNode<T>;

export function extract<S, T>(fn: ExtractFunc<S, T>, node: UiNode<S>): UiNode<T> {
    let val: S;
    let opts: UiNodeOptions | undefined;
    let children: UiNodeMap<S> | undefined;
    if (Array.isArray(node))
        [val, children, opts] = node;
    else
        val = node;

    let ex = fn(val, node);
    let exVal: T;
    let exOpts: UiNodeOptions | undefined;
    let exChildren0: UiNodeMap<T> | undefined;
    if (Array.isArray(ex))
        [exVal, exChildren0, exOpts] = ex;
    else
        exVal = ex;

    if (opts != null)
        if (exOpts != null)
            exOpts = { ...exOpts, ...opts };

    let exChildren: UiNodeMap<T> = {};
    if (children != null) {
        for (let key in children) {
            let child = children[key];
            let exChild = extract<S, T>(fn, child);
            exChildren[key] = exChild;
        }
    }
    if (exChildren0 != null && Object.keys(exChildren0).length)
        exChildren = { ...exChildren0, ...exChildren };

    ex = [exVal];

    if (exChildren != null && Object.keys(exChildren).length)
        ex[1] = exChildren;

    if (exOpts != null && Object.keys(exOpts).length)
        ex[2] = exOpts;
    return ex;
}


export class UiElement<T>{
    value: T
    label?: string
    icon?: string

    constructor(val: T, label?: string, icon?: string) {
        this.value = val;
        this.label = label;
        this.icon = icon;
    }
}

export type UiElementMap<T> = {
    [key: string]: UiElement<T>
};

export type UiDual<T> = T | UiElement<T>;

export function uiElementExtract<T>(dual: UiDual<T>): UiNode<T> {
    if (dual instanceof UiElement) {
        let { value, label, icon } = dual;
        let ex: UiNode<T> = [value];
        if (label != null || icon != null) {
            let opts: UiNodeOptions = {};
            if (label) opts.label = label;
            if (icon) opts.icon = icon;
            ex[2] = opts;
        }
        return ex;
    }
    return dual;
}
