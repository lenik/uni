#!/usr/bin/dprog -v

import std.c.stdlib;
import std.stdio;
import std.getopt;
import std.path;

import net.bodz.dog.dprog;              /* boDz D prOGram framework */

immutable RcsId rcsId = RcsId(r"$Id: - @VERSION@ @DATE@ @TIME@ - $");
string progname;

int loglevel;

int main(string[] args) {
    progname = stripExtension(baseName(args[0]));
    boot(args);
    return 0;
}

void boot(ref string[] args) {
    getopt(args,
        "v|verbose",    delegate { loglevel++; },
        "q|quiet",      delegate { loglevel--; },
        "h|help",       delegate { showHelp(); exit(0); },
        "version",      delegate { showVersion(); exit(0); }
    );
}

void showVersion() {
    alias writeln ln;
    ln("[" ~ progname ~ "] <?= TEXT ?>");
    ln("Written by <?= author ?>  Version 0." ~ rcsId.ver
        ~ "  Last updated at " ~ rcsId.date);
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
