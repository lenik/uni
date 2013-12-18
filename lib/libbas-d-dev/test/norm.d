#!/usr/bin/dprog -vig.

import std.stdio;
import lenik.bas.file;                  /* @link */
import std.path;

void main(string[] args) {
    foreach (a; args[1..$]) {
        string path = canonicalize(a);
        writeln(path);
    }
}

