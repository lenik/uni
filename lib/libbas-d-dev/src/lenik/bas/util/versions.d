#!/usr/bin/dprog -ivg

module lenik.bas.util.versions;

import std.array;
import std.algorithm : findSplit;
import std.ascii : isDigit;
import std.conv;                        /* parse */
import std.stdio;
import std.string;

bool versionNewer(string v1, string v2) {
    return versionCmp(v1, v2) > 0;
}

bool versionOlder(string v1, string v2) {
    return versionCmp(v1, v2) < 0;
}

int versionCmp(string v1, string v2) {
    if (v1 == v2)
        return 0;

    auto w1 = findSplit(v1, ".");
    auto w2 = findSplit(v2, ".");
    string a1 = w1[0];
    string a2 = w2[0];
    if (a1 != a2) {                     /* the "major" part is different. */
        if (!a1.empty && !a2.empty && a1[0].isDigit && a2[0].isDigit) {
            char[] c1 = a1.dup;
            char[] c2 = a2.dup;
            uint n1 = parse!uint(c1);
            uint n2 = parse!uint(c2);
            /* "123", "123e" after parse become: "", "e"  */
            if (n1 != n2)
                return n1 - n2;
            else
                return cmp(c1, c2);
        }
        /* non-integer version item. */
        return cmp(a1, a2);
    }

    /* Compare the "minor..." parts. */
    string t1 = w1[2];
    string t2 = w2[2];
    if (t1.empty)
        return -1;
    if (t2.empty)
        return 1;
    return versionCmp(t1, t2);
}

int main(string[] argv) {
    if (argv.length < 2) {
        writeln("Compare two versions");
        writefln("Syntax: %s VERSION-LIST", argv[0]);
        writeln("Returns: Index (1-based) of the latest version.");
        return -1;
    }

    string latest = argv[1];
    int latestIndex = 1;
    for (int i = 2; i < argv.length; i++) {
        string arg = argv[i];
        int cmp = versionCmp(latest, arg);
        if (cmp < 0) {
            /* arg is newer. */
            latest = arg;
            latestIndex = i;
        }
    }

    debug writefln("The latest version is %s.", latest);
    return latestIndex;
}
