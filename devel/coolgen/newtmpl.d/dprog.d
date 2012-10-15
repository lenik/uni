#!/usr/bin/dprog -v

import std.c.stdlib;
import std.stdio;
import std.getopt;
import std.path;

import net.bodz.dog.dprog;              /* boDz D prOGram framework */

immutable RcsId rcsId = RcsId(`$Id: - @VERSION@ @DATE@ @TIME@ - $`);
string progname = null;

int loglevel = 1;

void boot(ref string[] args) {
    if (progname == null)
        progname = stripExtension(baseName(args[0]));
    getopt(args,
        "v|verbose",    delegate { loglevel++; },
        "q|quiet",      delegate { loglevel--; },
        "h|help",       delegate { showHelp(); exit(0); },
        "version",      delegate { showVersion(); exit(0); }
    );
}

void showHelp() {
    alias writeln ln;
    showVersion();
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

void showVersion() {
    alias writeln ln;
    ln("[" ~ progname ~ "] <?= TEXT ?>");
    ln("Written by <?= author ?>  Version 0." ~ rcsId.ver
        ~ "  Last updated at " ~ rcsId.date);
}

int main(string[] args) {
    boot(args);
    writeln("Hello, world!");
    return 0;
}
