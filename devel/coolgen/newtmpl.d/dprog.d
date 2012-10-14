#!/usr/bin/dprog -v

import std.stdio;
import std.getopt;
import std.log;

import net.bodz.dog;                    /* boDz D prOGram framework */

static immutable RCSID ID(r"$Id: - @VERSION@ @DATE@ @TIME@ - $");

int loglevel;

int main(string[] args) {
    boot(args);
}

void boot(ref string[] args) {
    getopts(args,
        "v|verbose",    { loglevel++; },
        "q|quiet",      { loglevel--; },
        "help",         { help(); exit(0); },
        "version",      { version(); exit(0); }
    );
}

void version() {
    alias writeln ln;
    ln("[" ~ progname ~ "] <?= description >");
    ln("Written by <?= author ?>  Version 0." ~ ID.version
        ~ "  Last updated at " ~ ID.date);
}

void help() {
    alias writeln ln;
    version();
    ln();
    ln("Syntax: ");
    ln("    " ~ progname ~ " [OPTIONS] ...");
    ln();
    ln("Options: ");
    ln("    -q, --quiet             repeat to get less info");
    ln("    -v, --verbose           repeat to get more info");
    ln("    -h, --help              show this help page");
    ln("        --version           print the version info");
}
