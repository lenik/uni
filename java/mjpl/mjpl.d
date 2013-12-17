#!/usr/bin/dprog -vi
module mjpl;

import std.c.stdlib : exit;
import std.algorithm : sort;
import std.conv : to;
import std.file;
import std.getopt;
import std.path : baseName;
import std.process;
import std.stdio;
import std.string;
import std.zip;

pragma(lib, "bas-d");
import lenik.bas.log;
// import lenik.bas.program;

import lenik.uni.mjpl.m2env : m2env;             /* @link */
import lenik.uni.mjpl.pom : Pom, Scope, manager; /* @link */

mixin Log!("mjpl");

/* TODO:

  - using scope to determine which dependencies are required.
  
  - implement inclusion/exclusions in the dependencies/management

  - getopt for java/extra options

  - Main-Class aliases in manifest or somewhere.
  
*/
Pom project;
string[] classpaths;

int main(string[] argv) {
    string pomfile;
    bool runApp = true;
    
    bool opt_dependencies;
    bool opt_programs;
    bool opt_services;
    string opt_serviceType = null;
    
    int i;
 L: for (i = 1; i < argv.length; i++) {
        string arg = argv[i];
        bool more = i < argv.length - 1;
        
        if (arg.startsWith("-")) {
            switch (arg) {
            case "-p": case "--project":
                if (!more) break;
                pomfile = argv[++i];
                continue;
                
            case "-L": case "--libdir":
                if (!more) break;
                m2env.repodirs ~= argv[++i];
                continue;

            case "-d": case "--dependencies":
                opt_dependencies = true;
                runApp = false;
                continue;
                
            case "-l": case "--list":
                opt_programs = true;
                runApp = false;
                continue;
            
            case "-s": case "--services":
                opt_services = true;
                if (more) opt_serviceType = argv[++i];
                runApp = false;
                continue;
                
            case "-h":
            case "--help":
                showHelp(); continue;
            
            case "-q":
            case "--quiet":
                log.maxLevel--; continue;

            case "-v":
            case  "--verbose":
                log.maxLevel++; continue;

            case "--":
                i++;
                break L;
                
            default:
            }
            log.err("Bad option or missing arg: " ~ arg);
            return -1;
        } /* arg: -* */
        break;                          /* stop at the first non-option. */
    }
    argv = argv[i..$];

    if (pomfile is null)
        pomfile = "pom.xml";            /* find pom.xml in current dir */
    if (! exists(pomfile)) {
        log.err("No such file: %s", pomfile);
        return -1;
    }
    
    project = manager.resolveFile(pomfile);
    
    manager.scanLocalModules(project.localRoot);
    manager.loadDependencies(project);
    
    if (project is null) {
        log.err("Project is unknown.");
        return -1;
    }

    /* Collect the dependencies. */
    auto deps = manager.collectDependencies(project, false);
    foreach (idVer; deps) {
        auto split = idVer.split(":");
        string groupId = split[0];
        string artifactId = split[1];
        string version_ = split[2];
        
        if (opt_dependencies)
            log.info("Dependency for %s:%s:%s", groupId, artifactId, version_);

        bool workDir = false;
        Pom dpom = manager.resolveId(groupId, artifactId, version_);

        if (dpom !is null) {            /* There is a pom.xml */
            string classesDir = dpom.dir ~ "/target/classes/";
            string testClassesDir = dpom.dir ~ "/target/test-classes/";

            if (exists(classesDir)) {    /* We have src/target */
                workDir = true;
                classpaths ~= classesDir;
                if (opt_dependencies)
                    writeln(classesDir);
            }
            if (exists(testClassesDir)) {
                workDir = true;
                classpaths ~= testClassesDir;
                if (opt_dependencies)
                    writeln(testClassesDir);
            }
        }
        
        if (! workDir) {                /* In the repo, but no src code. */
            string jar = m2env.resolve(groupId, artifactId, version_, "jar");
            classpaths ~= jar;
            if (opt_dependencies)
                writeln(jar);
        }
    }

    string[][string] providers;
    scanImpls(opt_serviceType, providers);
    
    if (opt_services) {
        writeln("Services: ");
        foreach (k; sort(providers.keys)) {
            if (opt_serviceType is null)
                writeln(" -- " ~ k);
            foreach (provider; providers[k])
                writeln("    " ~ provider);
        }
    }
    
    string[string] programMap;
    foreach (p; providers["net.bodz.bas.program.IProgram"]) {
        auto lastDot = p.lastIndexOf('.');
        string tail;
        if (lastDot == -1)
            tail = cast(string) p;
        else
            tail = cast(string) p[lastDot+1..$];
        string alias_ = tail.toLower;
        programMap[alias_] = p;
    }
    
    if (opt_programs) {
        writeln("Programs:  ");
        foreach (k, v; programMap)
            writefln("    %s: %s", k, v);
    }
    
    if (! runApp)
        return 0;
    if (argv.length == 0) {
        log.err("Program name isn't specified.");
        return -1;
    }
    
    string program = argv[0];
    argv = argv[1..$];                  /* shift argv */
    if (program in programMap)
        program = programMap[program];
    
    string JAVA = "java";               /* which java... */
    string CLASSPATH = "";
    foreach (p; classpaths) {
        if (CLASSPATH.length != 0)
            CLASSPATH ~= ':';
        CLASSPATH ~= p;
    }
    
    string[] javaargs = [ JAVA, "-cp", CLASSPATH ];
    javaargs ~= program;
    javaargs ~= argv;

    log.info("Exec: %s", join(javaargs, " "));
    execvp(JAVA, javaargs);
    return -1;
}

void scanImpls(string type, ref string[][string] providers) {
    foreach (path; classpaths) {
        if (! exists(path)) {
            log.err("Non-existed classpath: %s", path);
            continue;
        }
        
        if (isDir(path)) {
            log.info("DIR: %s", path);
            
            string servicesDir = path ~ "/META-INF/services";
            if (! exists(servicesDir))
                continue;
            
            foreach (e; dirEntries(servicesDir, SpanMode.shallow)) {
                
                string fqcn = baseName(e.name);
                if (type is null || type == fqcn) {
                    log.info("  Service %s", fqcn);

                    auto f = File(e.name, "rt");
                    string ln;
                    while (!f.error && (ln = f.readln) !is null) {
                        ln = ln.strip;
                        if (ln.length == 0) /* skip empty lines */
                            continue;
                        log.dbg("    Provider %s", ln);
                        providers[fqcn] ~= ln;
                    }
                    f.close;
                }
            }
        } else {                        /* a jar/zip file */
            log.info("JAR: %s", path);
            
            void[] data = read(path);
            ZipArchive ar = new ZipArchive(data);
            string prefix = "META-INF/services/";
            foreach (e; ar.directory) {
                if (! e.name.startsWith(prefix))
                    continue;
                
                string fqcn = e.name[prefix.length..$];
                if (type is null || type == fqcn) {
                    log.info("  Service %s", fqcn);
                    
                    string content = cast(string) e.expandedData;
                    foreach (ln; content.split("\n")) {
                        ln = ln.strip;
                        if (ln.length == 0) /* skip empty lines */
                            continue;
                        
                        log.dbg("    Provider %s", ln);
                        providers[fqcn] ~= ln;
                    }
                }
            }
        }
    }
}

void showVersion() {
    writeln("Maven-based Java Application Launcher");
    writeln("Written by Lenik, Version: 1.0.0");
}

void showHelp() {
    showVersion();
    writeln();
    writeln("Syntax:");
    writeln("    mjpl [OPTIONS] [--] <main-class or alias> [ARGS...]");
    writeln();
    writeln("Options:");
    writeln("    -L, --libdir=PATH   Where to search the libraries");
    writeln("    -p, --project=FILE  Specify the start pom.xml path");
    writeln("    -d, --dependencies  List dependencies in full path");
    writeln("    -l, --list          List application aliases");
    writeln("    -h, --help          Show this help page");
    writeln("    -q, --quiet         Repeat to get less info");
    writeln("    -v, --verbose       Repeat to get more info");
    writeln("    --version           Print the version info");
    exit(1);
}
