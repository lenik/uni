module lenik.bas.str;

string substr(string s, int from) {
    return cast(string) s[from..$];
}

string substr(string s, int from, int to) {
    return cast(string) s[from..to];
}
