
function breakStr(s, re, min) {
    if (min == null)
        min = 1;
    var v = [];
    while (s.length >= min) {
        var lead = s.substr(0, min);
        s = s.substr(min);

        var pos = s.search(re);
        if (pos == -1) {
            v.push(lead + s);
            break;
        }

        var part = lead + s.substr(0, pos);
        v.push(part);
        s = s.substr(pos);
    }
    return v;
}
