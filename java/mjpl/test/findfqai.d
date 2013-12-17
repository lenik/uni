#!/usr/bin/dprog -vig.

import std.stdio;
import lenik.uni.mjpl.m2env;            /* @link */
 
int main(string[] argv) {
    M2Env env = new M2Env;
    
    foreach (string arg; argv[1..$]) {
        string file = env.resolveFQAI(arg);
        if (file == null)
            writeln("Can't resolve " ~ arg);
        else
            writeln("Resolved " ~ arg ~ ": " ~ file);
    }

    return 0;
}
