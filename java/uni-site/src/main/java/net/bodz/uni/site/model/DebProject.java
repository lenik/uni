package net.bodz.uni.site.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.PathResource;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.uni.site.util.DebControl;

public class DebProject
        extends Project {

    private DebControl debControl;
    private Set<String> amIncludes = new HashSet<String>();

    public DebProject(Section section, String name, Path directory) {
        super(section, name, directory);
        Path am = directory.resolve("Makefile.am");
        if (Files.exists(am))
            for (String line : new PathResource(am).to(StreamReading.class).lines()) {
                if (line.startsWith("include "))
                    amIncludes.add(line.substring(8).trim());
            }
    }

    @Override
    public boolean isPrivate() {
        return amIncludes.contains("libauto/private.am");
    }

    public DebControl getDebControl() {
        return debControl;
    }

    public void setDebControl(DebControl debControl) {
        this.debControl = debControl;
    }

    public Map<String, String> getInfo() {
        Map<String, String> pkg1 = getDebControl().getFirstPackage();
        return pkg1;
    }

    public String getArchitecture() {
        String arch = getInfo().get("Architecture");
        return arch;
    }

}
