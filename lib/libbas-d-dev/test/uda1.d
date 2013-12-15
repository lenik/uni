#!/usr/bin/dprog -vig.

import std.stdio;
import std.string;
import lenik.bas.reflect;               /* @link */

class User : IReflection {

    @(30)
    @("The full name")
    string name;

    @("How old in years")
    int age;

    @("Print a hello message.")
    protected string sayHello(string guest) {
        writeln("Hello, " ~ guest);
        return "Lenik";
    }

    @("Another attr")
    protected string sayHello(string guest1, string guest2) {
        writeln("Hello, " ~ guest1 ~ " and " ~ guest2);
        return "Lenik";
    }
    
    mixin reflection_impl;
}

void main() {
    User u = new User;
    Class _class = u.getClass();
    writeln("class name: " ~ _class.name);
    writeln("  simple name: " ~ _class.simpleName);
    writeln("  module name: " ~ _class.moduleName);

    foreach (name, f; _class.fields) {
        writeln(f.protection ~ " field " ~ name);
        foreach (Variant uda; f.udas) {
            writeln("    attr: " ~ to!string(uda));
        }
    }

    foreach (name, g; _class.methodGroups) {
        foreach (m; g.methods) {
            write(m.protection ~ " method "
                    ~ m.returnType ~ " " ~ name ~ "(");
            foreach (p; m.parameterTypes)
                write(p ~ ", ");
            writeln(")");
            foreach (Variant uda; m.udas) {
                writeln("    attr: " ~ to!string(uda));
            }
        }
    }
}
