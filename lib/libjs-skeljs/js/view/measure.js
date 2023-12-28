
export function parseLen(val, percentBase, defaultValue) {
    if (val == null)
        return defaultValue;

    if (typeof (val) == 'number')
        return val;

    const str = String(val);
    if (str.endsWith("%")) {
        var ratio = str.substring(0, str.length - 1) / 100.0;
        return ratio * percentBase;
    }
    return str;
}
