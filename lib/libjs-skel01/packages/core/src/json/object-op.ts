
export function extendNull(context: any, defaults: any) {
    if (defaults == null)
        return;
    for (var k in defaults) {
        if (context[k] == null)
            context[k] = defaults[k];
    }
}
