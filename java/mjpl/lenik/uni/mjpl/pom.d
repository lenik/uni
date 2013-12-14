#!/usr/bin/dprog -ivg...
module lenik.uni.mjpl.pom;

import std.array;
import std.file : exists, read;
import std.path : dirName, baseName;
import std.stdio;
import std.string;
import std.xml;

import lenik.uni.mjpl.m2env : M2Env;    /* @link */

// scope:
//               compile     provided    runtime     test
// compile       *           -           runtime     -
// provided      provided    -           provided    -
// runtime       runtime     -           runtime     -
// test          test        -           test        -
enum Scope { compile, provided, runtime, test }

class Dependency {
    string groupId;
    string artifactId;
    string packaging = "jar";
    string version_;
    Scope scope_ = Scope.compile;
    string path;                        /* resolved path. */
}

class Pom {
    M2Env env;
    string file;

    Pom parent;
    string name;
    string description;
    
    string groupId;
    string artifactId;
    string packaging = "jar";
    string version_;

    string[string] properties;
    Dependency[] dependencies = [];

    this(M2Env env, string pomfile) {
        assert(env);
        assert(pomfile);
        this.env = env;
        load(pomfile);
    }
    
    void load(string pomfile) {
        assert(pomfile);
        //if (! exists(pomfile))
        //    throw new Exception("File " ~ pomfile ~ " isn't existed.");
        
        string xml = cast(string) pomfile.read();
        debug xml.check();

        auto project = new Document(xml);
        foreach (Element e1; project.elements) {
            switch (e1.tag.name) {
            case "name":
                name = e1.text; break;
            case "description":
                description = e1.text; break;
            case "groupId":
                groupId = e1.text; break;
            case "artifactId":
                artifactId = e1.text; break;
            case "packaging":
                packaging = e1.text; break;
            case "version":
                version_ = e1.text; break;
            case "properties":
                foreach (Element e2; e1.elements) {
                    string key = e2.tag.name;
                    string value = e2.text;
                    properties[key] = value;
                }
                break;
                
            case "parent":
                string parentGroupId;
                string parentArtifactId;
                string parentVersion;
                foreach (Element e2; e1.elements) {
                    switch (e2.tag.name) {
                    case "groupId":
                        parentGroupId = e2.text; break;
                    case "artifactId":
                        parentArtifactId = e2.text; break;
                    case "version":
                        parentVersion = e2.text; break;
                    default:
                    }
                }
                
                string parentFile;

                string pomDir = dirName(pomfile);
                string parentPomXml = pomDir ~ "/../pom.xml";
                if (exists(parentPomXml))
                    parentFile = parentPomXml;
                else
                    parentFile = env.resolve(parentGroupId, parentArtifactId,
                                             parentVersion, "pom");
                
                if (parentFile is null)
                    throw new Exception("Can't find parent pom for: "
                                        ~ parentGroupId
                                        ~ ":" ~ parentArtifactId
                                        ~ ":" ~ parentVersion);
                parent = new Pom(env, parentFile);

                /* copy default attributes from parent. */
                if (groupId is null)
                    groupId = parent.groupId;
                if (version_ is null)
                    version_ = parent.version_;
                break;
                
            case "dependencies":
                foreach (Element e2; e1.elements) {
                    assert(e2.tag.name == "dependency");
                    Dependency dep = new Dependency;
                    foreach (Element e3; e2.elements) {
                        switch (e3.tag.name) {
                        case "groupId":
                            dep.groupId = e3.text; break;
                        case "artifactId":
                            dep.artifactId = e3.text; break;
                        case "packaging":
                            dep.packaging = e3.text; break;
                        case "version":
                            dep.version_ = e3.text; break;
                        case "scope":
                            switch (e3.text) {
                            case "compile":
                            case "":
                                dep.scope_ = Scope.compile; break;
                            case "runtime":
                                dep.scope_ = Scope.runtime; break;
                            case "provided":
                                dep.scope_ = Scope.provided; break;
                            case "test":
                                dep.scope_ = Scope.test; break;
                            default:
                                throw new Exception("Bad scope: " ~ e3.text);
                            }
                            break;
                            
                        default:
                            /* inclusions/exclusions, etc. */
                        } /* switch <dependency>/* */
                    }
                    dependencies ~= dep;
                } /* switch <dependencies>/* */
                break;
                
            default:
                /* build, plugins, profiles, etc. */
            } /* switch e1 */
        } /* foreach e1 */
    } /* load() */

    /* evaluate property values */
    void eval(string expr) {
        // ${...} variable: env.*, project.*, ...
        
    }
    
    void dump() {
        writeln("GroupId             : " ~ groupId);
        writeln("ArtifactId          : " ~ artifactId);
        writeln("Packaging           : " ~ packaging);
        writeln("Version             : " ~ version_);
        writeln("Name                : " ~ name);
        writeln("Description         : " ~ description);
        foreach (string k, v; properties)
            writefln("Property %s: %s", k, v);
        foreach (Dependency dep; dependencies)
            writefln("Dependency: %s:%s:%s:%s (%s)",
                     dep.groupId, dep.artifactId, dep.packaging, dep.version_,
                     dep.scope_);

        if (parent !is null) {
            writeln();
            writeln("Parent of the above POM: ");
            parent.dump();
        }
    }
}
