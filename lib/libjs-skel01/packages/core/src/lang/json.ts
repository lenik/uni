
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
                        let target = derefByPath(root, refPath);
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

export function derefByPath(node: any, path: string): any {
    if (path == null || path == '') return node;
    if (node == null)
        return undefined;

    let slash = path.indexOf('/');
    if (slash == -1)
        return node[path];
    let head = path.substring(0, slash);
    let next = node[head];
    if (next == null)
        return undefined;

    let tail = path.substring(slash + 1);
    return derefByPath(next, tail);
}
