#!/usr/bin/dprog -ivg.

import std.file : exists, read;
import std.stdio;
import std.string;

import lenik.uni.mjpl.m2env;            /* @link */
import lenik.uni.mjpl.pom;              /* @link */

int main(string[] argv) {
    if (argv.length <= 1) {
        writeln("POM core rewrite");
        writefln("Syntax: %s <pom-file>", argv[0]);
        return 1;
    }

    // if (!lenik.uni.mjpl.pom.exists(argv[1])) {
    if (!exists(argv[1])) {
        writeln("POM file isn't existed: " ~ argv[1]);
        return 1;
    }
    
    M2Env env = new M2Env;
    
    string pomfile = argv[1];
    Pom pom = new Pom(env, pomfile);
    // pom.dump();
    return 0;
}
