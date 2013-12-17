module lenik.uni.mjpl.pom;

import std.array;
import std.file : exists, read;
import std.path : dirName, baseName;
import std.process : environment;
import std.regex : regex, replace, Captures;
import std.stdio;
import std.string;
import std.xml;

import lenik.bas.log;
import lenik.bas.util.versions;
import lenik.uni.mjpl.m2env : m2env;

mixin Log!("pom");

// scope:
//               compile     provided    runtime     test
// compile       *           -           runtime     -
// provided      provided    -           provided    -
// runtime       runtime     -           runtime     -
// test          test        -           test        -
// system
enum Scope { compile, provided, runtime, test, system }

class Pom {
    string file;
    string dir;

    Pom parent;
    string parentGroupId;
    string parentArtifactId;
    string parentVersion;
    
    string name;
    string description;
    
    private string _groupId;
    private string _artifactId;
    private string _packaging = "jar";
    private string _version;

    private string[string] _props;
    private Dependency[] _deps;
    private Dependency[string] _dmgmt;  /* dependency management */
    private string[] modules;
    
    this(string file) {
        this.file = file;
        this.dir = dirName(file);
        parse(file);
    }
    
    @property Pom localRoot() {
        if (parent is null)
            return this;

        if (dir.startsWith(parent.dir ~ "/"))
            return parent.localRoot;

        return this;
    }

    @property string id() {
        return groupId ~ ":" ~ artifactId;
    }

    @property string idVer() {
        return id ~ ":" ~ version_;
    }
    
    @property string groupId() {
        if (_groupId != null)
            return _groupId;
        if (parent !is null)
            return parent.groupId;
        return null;
    }

    @property string artifactId() {
        if (_artifactId != null)
            return _artifactId;
        if (parent !is null)
            return parent.artifactId;
        return null;
    }

    @property string packaging() {
        if (_packaging != null)
            return _packaging;
        if (parent !is null)
            return parent.packaging;
        return null;
    }

    @property string version_() {
        if (_version != null)
            return _version;
        if (parent !is null)
            return parent.version_;
        return null;
    }

    string property(string key) {
        if (key in _props)
            return _props[key];
        if (parent !is null)
            return parent.property(key);
        return null;
    }

    final Dependency dmgmt(Dependency dep) {
        return dmgmt(dep.key);
    }
    
    final Dependency dmgmt(string groupId, string artifactId) {
        string dmkey = groupId ~ ":" ~ artifactId;
        return dmgmt(dmkey);
    }

    Dependency dmgmt(string dmkey) {
        if (dmkey in _dmgmt)
            return _dmgmt[dmkey];
        if (parent !is null)
            return parent.dmgmt(dmkey);
        return null;
    }

    @property Dependency[string] dependencies() {
        Dependency[string] deps;
        fillDependencies(deps);
        return deps;
    }
    
    private void fillDependencies(ref Dependency[string] deps) {
        foreach (d; _deps)
            deps[d.key] = d;
        if (parent !is null)
            parent.fillDependencies(deps);
    }
    
    /* Evaluate ${...} variables: env.*, project.*, ... */
    static auto varRegex = regex(r"\$\{(.*?)\}", "g");
    string eval(string expr) {
        if (expr is null)
            return null;
        
        auto fn = delegate string (Captures!string m) {
            string var = m[1];
            if (var.startsWith("env.")) {
                auto envkey = var[4..$];
                auto envval = environment[envkey];
                return envval;
            }
            if (var.startsWith("project.")) {
                auto member = var[8..$];
                switch (member) {
                case "name":
                    return name;
                case "groupId":
                    return groupId;
                case "artifactId":
                    return artifactId;
                case "packaging":
                    return packaging;
                case "version":
                    return version_;
                case "parent.groupId":
                    return parentGroupId;
                case "parent.artifactId":
                    return parentArtifactId;
                case "parent.version":
                    return parentVersion;
                default:
                    return "<error: bad project var " ~ var ~ ">";
                }
            }
            auto val = property(var);
            if (val !is null)
                return val;
            return "<error: bad var " ~ var ~ ">";
        };
        
        string val = replace!(fn)(expr, varRegex);
        return val;
    }

private:

    void parse(string pomFile) {
        assert(pomFile);
        if (! exists(pomFile))
            throw new Exception("No such file: " ~ pomFile);
        
        string xml = cast(string) pomFile.read();

        /* BUGFIX CommentException */
        static auto killRegex = regex(r"<!--.*==.*-->", "g");
        xml = replace(xml, killRegex, "");
        
        debug(2) xml.check();
        
        auto project = new Document(xml);
        foreach (Element e1; project.elements) {
            switch (e1.tag.name) {
            case "name":
                name = e1.text; break;
            case "description":
                description = e1.text; break;
            case "groupId":
                _groupId = e1.text; break;
            case "artifactId":
                _artifactId = e1.text; break;
            case "packaging":
                _packaging = e1.text; break;
            case "version":
                _version = e1.text; break;
            case "properties":
                foreach (Element e2; e1.elements) {
                    string key = e2.tag.name;
                    string value = e2.text;
                    _props[key] = value;
                }
                break;
                
            case "parent":
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
                break;
                
            case "dependencies":
                _deps ~= parseDependencies(e1);
                break;

            case "dependencyManagement":
                Element e2 = e1.elements[0];
                assert(e2.tag.name == "dependencies");
                foreach (Dependency dm; parseDependencies(e2)) {
                    string dmkey = dm.groupId ~ ":" ~ dm.artifactId;
                    _dmgmt[dmkey] = dm;
                }
                break;

            case "modules":
                foreach (Element e2; e1.elements) {
                    assert(e2.tag.name == "module");
                    modules ~= e2.text;
                }
                break;
                
            default:
                /* build, plugins, profiles, etc. */
            } /* switch e1 */
        } /* foreach e1 */
    }

    Dependency[] parseDependencies(Element _deps_) {
        Dependency[] deps;
        foreach (Element _dep_; _deps_.elements) {
            assert(_dep_.tag.name == "dependency");
            Dependency dep = new Dependency;
            foreach (Element e; _dep_.elements) {
                switch (e.tag.name) {
                case "groupId":
                    dep.groupId = e.text; break;
                case "artifactId":
                    dep.artifactId = e.text; break;
                case "packaging":
                    dep.packaging = e.text; break;
                case "version":
                    dep.version_ = e.text; break;
                case "classifier":
                    dep.classifier = e.text; break;
                case "optional":
                    if (e.text == "true")
                        dep.optional = true;
                    break;
                case "scope":
                    switch (e.text) {
                    case "compile":
                    case "":
                        dep.scope_ = Scope.compile; break;
                    case "runtime":
                        dep.scope_ = Scope.runtime; break;
                    case "provided":
                        dep.scope_ = Scope.provided; break;
                    case "test":
                        dep.scope_ = Scope.test; break;
                    case "system":
                        dep.scope_ = Scope.system; break;
                    default:
                        throw new Exception("Bad scope: " ~ e.text);
                    }
                    break;
                default:
                    /* Ignore inclusions/exclusions, etc. */
                } /* switch <dependency>/* */
            }
            deps ~= dep;
        } /* foreach <dependencies>/* */
        return deps;
    }

    void expandVars() {
        foreach (string k, v; _props)
            _props[k] = eval(v);
        
        foreach (string k, Dependency dm; _dmgmt)
            dm.version_ = eval(dm.version_);
        
        foreach (Dependency dep; _deps) {
            dep.version_ = eval(dep.version_);
            if (dep.version_ is null) {
                auto dm = dmgmt(dep);
                if (dm is null)
                    log.warn("Unknown dependency version: %s:%s",
                             dep.groupId, dep.artifactId);
                else {
                    dep.version_ = dm.version_;
                    dep.scope_ = dm.scope_;
                }
            }
        }
    }
    
public:
    
    // Dependency depmgmt
    void dump() {
        static char minus(string s) {
            return s is null ? '-' : ' ';
        }
        
        writefln("GroupId             :%c%s", minus(_groupId), groupId);
        writefln("ArtifactId          :%c%s", minus(_artifactId), artifactId);
        writefln("Packaging           :%c%s", minus(_packaging), packaging);
        writefln("Version             :%c%s", minus(_version), version_);
        writeln("Name                : " ~ name);
        writeln("Description         : " ~ description);
        foreach (string k, v; _props)
            writefln("Property %s: %s", k, v);
        foreach (Dependency dep; _deps)
            writefln("Dependency: %s:%s:%s:%s (%s)",
                     dep.groupId, dep.artifactId, dep.packaging, dep.version_,
                     dep.scope_);
        foreach (Dependency dm; _dmgmt)
            writefln("Dependency-Management: %s:%s = %s",
                     dm.groupId, dm.artifactId, dm.version_);
        if (parent !is null) {
            writeln();
            writeln("Parent of the above POM: ");
            parent.dump();
        }
    }
    
}

class Dependency {

    Pom pom;
    
    string groupId;
    string artifactId;
    string packaging = "jar";
    string version_;
    string classifier;
    Scope scope_ = Scope.compile;
    bool optional;
    
    /* inclusions/exclusions... */
    @property string key() {
        return groupId ~ ":" ~ artifactId;
    }

    @property string str() {
        return groupId
            ~ ":" ~ artifactId
            ~ (classifier is null ? "" : ("/" ~ classifier))
            ~ ":" ~ packaging
            ~ ":" ~ version_;
    }
    
}

class PomManager {

    Pom[string] imap;                   /* id:ver => Pom */
    Pom[string] fmap;                   /* file => Pom */
    string[string] maxVers;             /* id => max-ver */
    
    Pom resolveFile(string file, bool cachedOnly = false){
        if (file in fmap)
            return fmap[file];

        if (cachedOnly)
            return null;

        Pom pom = new Pom(file);
        fmap[file] = pom;

        loadParents(pom);
        pom.expandVars();

        string id = pom.groupId ~ ":" ~ pom.artifactId;
        string idVer = id ~ ":" ~ pom.version_;
        imap[idVer] = pom;

        if (id !in maxVers)
            maxVers[id] = pom.version_;
        else if (versionNewer(pom.version_, maxVers[id]))
            maxVers[id] = pom.version_;
        
        return pom;
    }
    
    Pom resolveId(string groupId, string artifactId, string version_,
                  bool cachedOnly = false) {
        string idVer = groupId ~ ":" ~ artifactId ~ ":" ~ version_;
        
        if (idVer in imap)
            return imap[idVer];

        if (cachedOnly)
            return null;
        
        string file = m2env.resolve(groupId, artifactId, version_, "pom");
        if (file == null) {
            log.warn("Failed to resolve %s:%s:%s",
                     groupId, artifactId, version_);
            return null;
        }
        
        return resolveFile(file);
    }

    bool loadParents(Pom pom) {
        if (pom.parent !is null)        /* parent is already loaded. */
            return true;
        
        string groupId = pom.parentGroupId;
        string artifactId = pom.parentArtifactId;
        string version_ = pom.parentVersion;
        if (groupId is null && artifactId is null) /* this is the root */
            return true;


        /* Find in cache at the first. */
        Pom parent = resolveId(groupId, artifactId, version_, true);
        
        if (parent is null) {
            log.dbg("Load parent %s:%s:%s", groupId, artifactId, version_);

            /* search in local dirs  */
            string parentDir = dirName(pom.dir);
            if (parentDir !is null) {
                string parentFile = parentDir ~ "/pom.xml";
                if (exists(parentFile)) {
                    log.dbg("Found local file %s", parentFile);
                    parent = resolveFile(parentFile);
                }
            }

            /* otherwise, search the repo */
            if (parent is null) {
                parent = resolveId(groupId, artifactId, version_);
                if (parent is null) {
                    log.err("Can't resolve the parent.");
                    return false;
                } else {
                    log.dbg("Found in repo %s", parent.file);
                }
            }
        }
        
        pom.parent = parent;

        log.enter(); scope(exit) log.leave();
        return loadParents(parent);
    }

    void scanLocalModules(Pom pom) {
        log.dbg("Scan local modules in %s:%s:%s",
                pom.groupId, pom.artifactId, pom.version_);
        log.enter(); scope(exit) log.leave();

        foreach (m; pom.modules) {
            string moduleFile = format("%s/%s/pom.xml", pom.dir, m);
            if (exists(moduleFile)) {
                Pom localModule = resolveFile(moduleFile);
                scanLocalModules(localModule); /* scan recursive */
            } else {
                log.dbg("Ignored non-existed module: %s", m);
            }
        }
    }

    void loadDependencies(Pom pom) {
        log.dbg("Load dependencies in %s:%s:%s",
                pom.groupId, pom.artifactId, pom.version_);
        log.enter(); scope(exit) log.leave();
        
        foreach (dep; pom.dependencies) {
            if (dep.pom !is null)
                continue;
            
            switch (dep.scope_) {
            case Scope.compile:
            case Scope.runtime:
                break;
            case Scope.test:            /* just load it */
                break;
            case Scope.provided:        /* skip provided dependencies */
            case Scope.system:          /* skip system dependencies */
                continue;
            default:
                log.warn("Illegal scope: %s", dep.scope_);
                continue;
            }
            
            log.dbg("Load dependency %s:%s:%s (%s)",
                    dep.groupId, dep.artifactId, dep.version_, dep.scope_);

            Pom pom2 = resolveId(dep.groupId, dep.artifactId, dep.version_);
            if (pom2 is null)
                continue;

            if (! dep.optional)
                loadDependencies(pom2);
            
            dep.pom = pom2;
        }
    }

    string[] collectDependencies(Pom project, bool test) {
        string[string] useVers;
        string[] order;
        collectDependencies(project, project, test, useVers, order);

        string[] list;
        foreach (id; order) {
            string ver = useVers[id];
            list ~= id ~ ":" ~ ver;
        }
        return list;
    }
    
private:
    
    void collectDependencies(Pom project, Pom pom, bool test,
                             ref string[string] useVers, ref string[] order) {
        string id = pom.groupId ~ ":" ~ pom.artifactId;
        string ver;

        if (id in useVers)
            return;
        
        auto dm = project.dmgmt(id);
        if (dm !is null)
            ver = dm.version_;
        else
            ver = manager.maxVers[id]; /* assert non-null */

        useVers[id] = ver;
        order ~= id;
        
        foreach (d; pom.dependencies) {
            if (d.packaging != "jar")   /* only collect jars. */
                continue;
            
            switch (d.scope_) {
            case Scope.compile:
            case Scope.runtime:
                break;
                
            case Scope.test:
                if (test)
                    break;
                else
                    continue;
                
            case Scope.provided:
            case Scope.system:
            default:
                continue;
            }

            if (d.pom !is null)
                collectDependencies(project, d.pom, test, useVers, order);
        }
    }
    
}

// mixin global(PomManager, manager);
PomManager manager;
static this() {
    manager = new PomManager;
}
