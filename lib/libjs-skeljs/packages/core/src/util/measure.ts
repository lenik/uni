
export function parseLen(val: number | string | null | undefined,
    percentBase: number, defaultValue: number): number {

    if (val == null)
        return defaultValue;

    if (typeof (val) == 'number')
        return val;

    let str = String(val);
    if (str.endsWith("%")) {
        let chopped = str.substring(0, str.length - 1);
        let ratio = Number(chopped) / 100.0;
        return ratio * percentBase;
    }
    return Number(str);
}
