
export const defaultPropertyDelim = '.';
export const refKeyDelim = '/';

export function pathPropertyGet(context: any, path: string, delim = defaultPropertyDelim): any {
    if (path == null || path == '') return context;
    if (context == null)
        return undefined;

    let slash = path.indexOf(delim);
    if (slash == -1)
        return context[path];

    let head = path.substring(0, slash);
    let child = context[head];
    if (child == null)
        return undefined;

    let tail = path.substring(slash + delim.length);
    return pathPropertyGet(child, tail);
}

export type PropertyFactoryFn = (context: any, propertyName: string, contextPath?: string) => any;

function defaultFactory(context: any, propertyName: string, contextPath?: string) {
    return {} as any;
}

/**
 * @returns [ leaf-context-path, leaf-context ]
 */
export function pathPropertySet(context: any, path: string, newValue: any, delim = defaultPropertyDelim, factory = defaultFactory, contextPath?: string): any {
    if (context == null)
        throw new Error('null context.');
    if (path == null || path == '') return context;

    let slash = path.indexOf(delim);
    if (slash == -1) {
        context[path] = newValue;
        return [contextPath, context]
    } else {
        let head = path.substring(0, slash);
        let child = context[head];
        if (child == null)
            child = context[head] = factory(context, head, contextPath);

        let childPath = contextPath == null ? head : contextPath + delim + head;
        let tail = path.substring(slash + delim.length);
        return pathPropertySet(child, tail, newValue, delim, factory, childPath);
    }
}

export function parseJson(json: string): any {
    let val = JSON.parse(json);
    wireUp(val);
    return val;
}

export function toJson(val: any): string {
    let flat = flatten(val);
    return JSON.stringify(flat);
}

export function flatten(o: any, dict: Map<any, string> = new Map(), path?: string): any {
    if (typeof o != 'object')
        return o;

    let out: any = {};
    for (let key in o) {
        let val = o[key];
        switch (typeof val) {
            case 'object':
                let sharedPath = dict.get(val);
                if (sharedPath != null)
                    out[key] = `<ref:${sharedPath}>`;
                else {
                    let childPath = path != null ? path + "/" + key : key;
                    dict.set(val, childPath);
                    let flatVal = flatten(val, dict, childPath);
                    out[key] = flatVal;
                }
                break;

            default:
                out[key] = val;
        }
    }
    return out;
}

export function wireUp(o: any, root: any = o) {
    if (typeof o == 'object')
        for (let key in o) {
            let val = o[key];
            switch (typeof val) {
                case 'string':
                    if (val.startsWith("<ref:")
                        && val.endsWith(">")) {
                        let refPath = val.substring(5, val.length - 1);
                        let target = pathPropertyGet(root, refPath, refKeyDelim);
                        o[key] = target;
                    }
                    break;

                case 'object':
                    wireUp(val, root);
                    break;
            }
        }
    return o;
}
