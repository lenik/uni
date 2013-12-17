module lenik.uni.mjpl.m2env;

import std.algorithm : sort, findSplit;
import std.array;
import std.file;
import std.path : baseName;
import std.process : environment;
import std.stdio;
import std.string;
import std.xml;

pragma(lib, "bas-d");
import lenik.bas.log : Log, Logger;
import lenik.bas.util.versions;

mixin Log!("m2env");

class M2Env {
    string MAVEN_HOME;
    string HOME;
    
    string m2confdir;
    string m2repodir;
    string userm2confdir;
    string userm2repodir;

    string repodirs[];
    
    this() {
        MAVEN_HOME = environment.get("MAVEN_HOME", "/usr/share/maven");
        HOME = environment.get("HOME", "/");

        config();

        if (exists(userm2repodir))
            repodirs ~= userm2repodir;
        else
            userm2repodir = null;
        
        if (exists(m2repodir))
            repodirs ~= m2repodir;
        else
            m2repodir = null;
    }
    
    void config() {
        if (exists("/etc/maven2/m2.conf")) {
            m2confdir = "/etc/maven2";
        } else {
            m2confdir = "/etc/maven";
        }
        m2repodir = "/usr/share/maven-repo";
        userm2confdir = HOME ~ "/.m2";
        userm2repodir = userm2confdir ~ "/repository";

        string m2conffile = m2confdir ~ "/m2.conf";
        if (exists(m2conffile)) {
            auto f = File(m2conffile, "rt");
            if (! f.error()) {
                string line;
                /* set maven.home default <dir> */
                while ((line = f.readln()) !is null) {
                    string words[] = line.split();
                    if (words.length < 4) continue;
                    if (words[0] != "set") continue;
                    if (words[1] != "maven.home") continue;
                    if (words[2] != "default") continue;

                    string arg = words[3];
                    arg = arg.replace("${user.home}", HOME);
                    MAVEN_HOME = arg;
                }
            }
            f.close();
        }

        string settingsfile = userm2confdir ~ "/settings.xml";
        if (exists(settingsfile)) {
            /* parse the xml ... */
            /* /settings/localRepository => ? */

            // string xml = cast(string) std.file.read(settingsfile);
            string xml = cast(string) settingsfile.read();
            // debug xml.check();          /* check for well-formedness */

            auto doc = new Document(xml);
            foreach (Element e; doc.elements)
                if (e.tag.name == "localRepository") {
                    userm2repodir = e.text;
                    break;
                }
        }
    }
    
    // string search(string dir, string base, string ver
    string resolveFQAI(string fqai) {
        string v[] = fqai.split(":");
        if (v.length < 3)
            throw new Exception("Bad FQAI: " ~ fqai);
        
        string groupId = v[0];
        string artifactId = v[1];
        string packaging;
        string version_;
        if (v.length == 3) {
            packaging = "jar";
            version_ = v[2];
        } else {
            packaging = v[2];
            version_ = v[3];
        }

        return resolve(groupId, artifactId, version_, packaging);
    }

    string resolve(string groupId, string artifactId, string versionRange,
                   string packaging = "jar") {
        string file;
        bool unique = versionRange.indexOf(',') == -1;
        string version_ = unique ? versionRange : null;
        
        foreach (string repodir; repodirs) {
            string repodir_ = repodir ~ '/';
            string ext = '.' ~ packaging;
            
            file = repodir_ ~ artifactId ~ ext;
            if (exists1(file)) return file;

            if (unique) {
                string avx = artifactId ~ '-' ~ version_ ~ ext;
                file = repodir_ ~ avx;
                if (exists1(file)) return file;
            }
            
            string groupdir_ = groupId.replace(".", "/") ~ '/';
            string dir = repodir_ ~ groupdir_ ~ artifactId;
            if (unique) {
                string avx = artifactId ~ '-' ~ version_ ~ ext;
                file = dir ~ '/' ~ version_ ~ '/' ~ avx;
                if (exists1(file)) return file;
            } else {
                file = findLatest(dir, artifactId, versionRange, ext);
                if (file != null) return file;
            }
        }
        return null;
    }

    bool exists1(string file) {
        if (exists(file)) {
            debug log.dbg("Resolved: " ~ file);
            return true;
        } else {
            return false;
        }
    }

    string findLatest(string dir, string base, string verFromTo, string ext) {
        auto split = findSplit(verFromTo, ",");
        string from = split[0].strip;
        string to = split[2].strip;

        int fromCmpMin;
        if (from.startsWith("["))
            fromCmpMin = 0;
        else if (from.startsWith("("))
            fromCmpMin = 1;
        else
            throw new Exception("Bad version range: from=" ~ from);
        from = from[1..$].strip;
        
        int toCmpMax;
        if (to.endsWith("]"))
            toCmpMax = 0;
        else if (to.endsWith(")"))
            toCmpMax = -1;
        else
            throw new Exception("Bad version range: to=" ~ to);
        to = to.chop().strip;
        
        debug writeln("find latest for " ~ dir ~ "/*/" ~ base ~ "-*" ~ ext);
        string pattern = base ~ "-*" ~ ext;

        string[] list;
        foreach (DirEntry entry; dirEntries(dir, SpanMode.shallow)) {
            if (! entry.isDir)          /* each version sit in a dir. */
                continue;

            string ver = entry.name.baseName;
            if (!from.empty && versionCmp(ver, from) < fromCmpMin)
                continue;
            if (!to.empty && versionCmp(ver, to) > toCmpMax)
                continue;
            auto ls = array(dirEntries(entry.name, pattern, SpanMode.shallow));
            if (ls.empty)               /* version without jar */
                continue;

            list ~= ver;
        }

        if (list.empty)
            return null;

        /* sort in descending version order. */
        sort!((a, b) => versionNewer(a,b)) (list);
        string latestVersion = list[0];
        
        dir ~= "/" ~ latestVersion;
        return dir ~ "/" ~ base ~ "-" ~ latestVersion ~ ext;
    }

    debug void dump() {
        writeln("HOME           = " ~ HOME);
        writeln("MAVEN_HOME     = " ~ MAVEN_HOME);
        writeln("m2conffdir     = " ~ m2confdir);
        writeln("m2repodir      = " ~ m2repodir);
        writeln("userm2conffdir = " ~ userm2confdir);
        writeln("userm2repodir  = " ~ userm2repodir);

        foreach (string repodir; repodirs)
            writeln("repo-dir: " ~ repodir);
    }
    
}

M2Env m2env;
static this() {
    m2env = new M2Env;
}
