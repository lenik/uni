
function _with(context, expr) {
    var js = '';
    js = '() => {\n';
    js += '    const {';
    var i = 0;
    for (var k in context) {
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

