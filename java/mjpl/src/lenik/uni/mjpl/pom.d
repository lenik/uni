#!/usr/bin/dprog -iv

import std.array;
import std.stdio;
import std.string;
import std.xml;

# scope:
#               compile     provided    runtime     test
# compile       *           -           runtime     -
# provided      provided    -           provided    -
# runtime       runtime     -           runtime     -
# test          test        -           test        -
enum Scope { COMPILE, PROVIDED, RUNTIME, TEST, }

class Dependency {
    string groupId;
    string artifactId;
    string packaging = "jar";
    string version_;
    Scope scope_ = "compile";
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

    string[string] props;
    Dependency[] deps;

    this(M2Env env) {
        assert(env);
        this.env = env;
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
                for (Element e2; e1.elements) {
                    string key = e2.tag.name;
                    string value = e2.text;
                }
                break;
                
            case "parent":
                string parentGroupId;
                string parentArtifactId;
                string parentVersion;
                for (Element e2; e1.elements) {
                    switch (e2.tag.name) {
                    case "groupId":
                        parentGroupId = e2.text; break;
                    case "artifactId":
                        parentArtifactId = e2.text; break;
                    case "version":
                        parentVersion = e2.text; break;
                    }
                }
                string parentFile = env.resolve(parentGroupId, parentArtifactId,
                                                "pom", parentVersion);
                if (parentFile == null)
                    throw new Exception("Can't find parent pom for: "
                                        ~ parentGroupId
                                        ~ ":" ~ parentArtifactId
                                        ~ ":" ~ parentVersion);
                
                parent = new Pom(env, parentFile);
                break;
                
            case "dependencies":
                for (Element e2; e1.elements) {
                    assert(e2.tag.name == "dependency");
                    Dependency dep = new Dependency;
                    for (Element e3; e2.elements) {
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
                            dep.scope_ = e3.text; break;
                        }
                    }
                    deps ~= dep;
                }
                break;
            } /* switch e1 */
        } /* foreach e1 */
    } /* load() */

    
}
