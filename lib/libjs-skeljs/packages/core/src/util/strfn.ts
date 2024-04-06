
export function breakStr(s: string, re: RegExp, min: number = 1) {
    var v: string[] = [];
    while (s.length >= min) {
        var lead = s.substring(0, min);
        s = s.substring(min);

        var pos = s.search(re);
        if (pos == -1) {
            v.push(lead + s);
            break;
        }

        var part = lead + s.substring(0, pos);
        v.push(part);
        s = s.substring(pos);
    }
    return v;
}
