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
import lenik.bas.file : canonicalize;
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
                maxLogLevel--; continue;

            case "-v":
            case  "--verbose":
                maxLogLevel++; continue;

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
    pomfile = canonicalize(pomfile);
    log.info("Load project pom: %s", pomfile);
    
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
    string[string] programNames;
    string[string] programTypes;
    string[string] tailmap;
    scanImpls(opt_serviceType, providers, programNames, programTypes, tailmap);
    
    if (opt_services) {
        writeln("Services: ");
        foreach (k; sort(providers.keys)) {
            if (opt_serviceType is null)
                writeln("Service for: " ~ k);
            foreach (provider; providers[k])
                writeln("    " ~ provider);
        }
    }
    
    foreach (fqcn; providers["net.bodz.bas.program.IProgram"]) {
        auto lastDot = fqcn.lastIndexOf('.');
        string tail;
        if (lastDot == -1)
            tail = cast(string) fqcn;
        else
            tail = cast(string) fqcn[lastDot+1..$];
        string alias_ = tail.toLower();

        if (alias_ !in programNames)
            programNames[alias_] = fqcn;
    }
    
    if (opt_programs) {
        writeln("Programs:  ");
        string[] aliases = programNames.keys;
        sort(aliases);
        foreach (k; aliases)
            writefln("    %s: %s", k, programNames[k]);
    }
    
    if (! runApp)
        return 0;
    if (argv.length == 0) {
        log.err("Program name isn't specified.");
        return -1;
    }
    
    string program = argv[0];
    argv = argv[1..$];                  /* shift argv */
    if (program in programNames)
        program = programNames[program];
    
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

void scanImpls(string serviceType,
               ref string[][string] providers,
               ref string[string] programNames,
               ref string[string] programTypes,
               ref string[string] tailmap) {
    foreach (path; classpaths) {
        if (! exists(path)) {
            log.err("Non-existed classpath: %s", path);
            continue;
        }
        
        if (isDir(path)) {
            log.info("DIR: %s", path);
            
            string servicesDir = path ~ "/META-INF/services";
            if (exists(servicesDir)) {
                foreach (e; dirEntries(servicesDir, SpanMode.shallow)) {
                    string fqcn = baseName(e.name);
                    if (serviceType is null || serviceType == fqcn) {
                        log.info("  Service %s", fqcn);
                        char[] content = cast(char[]) read(e.name);
                        auto list = parseServices(content);
                        providers[fqcn] ~= list;
                    }
                }
            }
            
            string programsFile = path ~ "/META-INF/programs";
            if (exists(programsFile)) {
                char[] content = cast(char[]) read(programsFile);
                parsePrograms(content, programNames, programTypes);
            }
            
        } else {                        /* a jar/zip file */
            log.info("JAR: %s", path);
            
            void[] data = read(path);
            ZipArchive ar = new ZipArchive(data);
            
            foreach (e; ar.directory) {
                if (e.name.startsWith("META-INF/services/")) {
                    string fqcn = e.name["META-INF/services/".length .. $];

                    if (serviceType is null || serviceType == fqcn) {
                        log.info("  Service %s", fqcn);
                    
                        char[] content = cast(char[]) e.expandedData;
                        auto list = parseServices(content);
                        providers[fqcn] ~= list;
                    }
                }

                if (e.name == "META-INF/programs") {
                    char[] content = cast(char[]) e.expandedData;
                    parsePrograms(content, programNames, programTypes);
                }
            } /* for ar.directory */
        } /* if dir/jar */
    } /* for classpath */
}

string[] parseServices(char[] content) {
    string[] list;
    foreach (ln; content.split("\n")) {
        ln = ln.strip();
        if (ln.length == 0) /* skip empty lines */
            continue;

        log.dbg("    Service Provider %s", ln);
        list ~= cast(string) ln;
    }
    return list;
}

void parsePrograms(char[] content,
                   ref string[string] programNames,
                   ref string[string] programTypes) {
    foreach (ln; content.split("\n")) {
        ln = ln.strip();
        if (ln.length == 0) /* skip empty lines */
            continue;

        /* alias = runner fqcn */
        auto eq = ln.indexOf('=');
        string alias_ = cast(string) ln[0..eq];

        string remain = cast(string) ln[eq+1..$];
        remain = remain.strip();
        auto spc = remain.indexOf(' ');
        string runner = cast(string) remain[0..spc].strip();
        string fqcn = cast(string) remain[spc+1..$].strip();

        programNames[alias_] = fqcn;
        programTypes[alias_] = runner;
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
