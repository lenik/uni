class VarRef {
    constructor(context, path) {
        this.context = context;
        this.path = path;
    }

    isComposite() {
        return this.path.includes(".");
    }

    isSimple() {
        return !this.isComposite();
    }

    deref() {
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
            } else {
                var head = remains.substr(0, dot);
                remains = remains.substr(dot + 1);
            }
            context = context[head];
        }
        return new VarRef(context, remains);
    }

    optim() {
        const d = this.deref();
        Object.assign(this, d);
    }

    get() {
        const d = this.deref();
        if (d.isSimple())
            return d.context[d.path];
        throw "null in the path.";
    }

    set(val) {
        const d = this.deref();
        if (d.isSimple())
            return d.context[d.path] = val;
        throw "null in the path.";
    }
}

export function resolvVar(path, context) {
    return new VarRef(context, path).get();
}

export function writeVar(path, newVal, start) {
    var d = new VarRef(path, context);
    return d.set(newVal);
}
