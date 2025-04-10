package net.bodz.uni.site.model;

import java.io.File;
import java.nio.file.Path;

import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.i18n.dom.StrFn;

public class PatchProject
        extends Project {

    String version;

    public PatchProject(Section section, String name, Path directory) {
        super(section, name, directory);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public iString getDescription() {
        return StrFn.wrap(getName());
    }

}
