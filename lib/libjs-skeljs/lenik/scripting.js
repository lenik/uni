
export function resolvVar(path, start) {
    if (start == null)
        start = window;
    var offset = 0;
    while (start != null) {
        var dot = path.indexOf('.', offset);
        if (dot == -1)
            break;
        var head = path.substr(0, dot);
        path = path.substr(dot + 1);
        start = start[head];
    }
    return { ctx: start, path: path, value: start[path] };
}

export function writeVar(path, value, start) {
    var v = resolvVar(path, start);
    v.ctx[v.path] = value;
}
