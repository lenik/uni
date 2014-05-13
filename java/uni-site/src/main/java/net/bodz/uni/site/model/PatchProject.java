package net.bodz.uni.site.model;

import java.io.File;

import net.bodz.bas.i18n.dom.iString;

public class PatchProject
        extends Project {

    String version;

    public PatchProject(Section section, String name, File directory) {
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
        return iString.fn.val(getName());
    }

}
