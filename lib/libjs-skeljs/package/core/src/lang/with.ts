
export function _with(context: any, expr: string): any {
    let js = '';
    js = '() => {\n';
    js += '    const {';
    let i = 0;
    for (let k in context) {
        if (i++)
            js += ', ';
        js += k;
    }
    js += '} = context;\n';
    js += '    return (' + expr + ');';
    js += '}';
    const compiled = eval(js);
    const val = compiled();
    return val;
}
