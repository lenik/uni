
export class VarRef {

    path: string
    context: any

    constructor(context: any, path: string) {
        this.context = context;
        this.path = path;
    }

    isComposite(): boolean {
        return this.path.includes(".");
    }

    isSimple(): boolean {
        return !this.isComposite();
    }

    deref(): VarRef {
        var context = this.context;
        if (context == null) context = window;
        var offset = 0;
        var remains = this.path;
        while (context != null) {
            var dot = remains.indexOf(".", offset);
            var head;
            if (dot == -1) {
                // head = remains;
                // remains = null;
                break;
            }
            head = remains.substring(0, dot);
            remains = remains.substring(dot + 1);
            context = context[head];
        }
        return new VarRef(context, remains);
    }

    optim(): void {
        let d = this.deref();
        Object.assign(this, d);
    }

    get(): any {
        const d: VarRef = this.deref();
        if (d.isSimple())
            return d.context[d.path];
        throw "null in the path.";
    }

    set(val: any): any {
        const d: VarRef = this.deref();
        if (d.isSimple())
            return d.context[d.path] = val;
        throw "null in the path.";
    }
}

export function resolveVar(path: string, context: any) {
    return new VarRef(context, path).get();
}

export function writeVar(path: string, newVal: any, start: any) {
    var d = new VarRef(path, start);
    return d.set(newVal);
}


export function wsSepStr(..._args: (any)[]) {
    let s = '';
    let i = 0;
    for (let a of _args) {
        if (i++ != 0) s += ' ';
        s += a;
    }
    return s;
}

function _rawInvokeStr(fn: string, sep: string, ..._args: (any)[]) {
    let s = fn + '(';
    let prev: string | null = null;
    if (typeof _args[0] == 'string')
        [prev, ..._args] = _args;

    let i = 0;
    for (let a of _args) {
        if (i++ != 0) s += sep;
        s += a;
    }
    s += ')';
    if (prev != null) s += sep + prev;
    return s;
}

export const rawInvokeStr = (fn: string, ..._args: (any)[]) => _rawInvokeStr(fn, ', ', ..._args);
export const wsRawInvokeStr = (fn: string, ..._args: (any)[]) => _rawInvokeStr(fn, ' ', ..._args);
