
export function extendNull(ctx, opt) {
    if (opt == null)
        return;
    for (var k in opt) {
        if (ctx[k] == null)
            ctx[k] = opt[k];
    }
}
