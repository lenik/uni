#!/usr/bin/dprog -ivg.

import std.array;
import std.stdio;
import std.string;

import lenik.bas.util.versions;         /* @link */

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
