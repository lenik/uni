package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Assert;

import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.PathResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.site.DefaultUniSite;
import net.bodz.uni.site.UniSite;

public class GenerateProjectItmFiles
        extends Assert {

    static final Logger logger = LoggerFactory.getLogger(GenerateProjectItmFiles.class);

    public static void main(String[] args)
            throws IOException {
        UniSite site = new DefaultUniSite().getTarget();
        for (Section section : site.getSectionMap().values()) {
            for (Project project : section.getProjects()) {
                Path docFile = project.getDirectory().resolve(project.getName() + ".itm");
                PathResource file = new PathResource(docFile);
                ITreeOut out = file.newTreeOut();
                out.println("@label");
                out.enter();
                out.println(project.getName());
                out.println("<p lang=\"zh-cn\">" + project.getName());
                out.leave();
                out.println();

                out.println("@text");
                out.enter();
                String description = project.getDescription().toString();
                out.println(description.replace("\n", "\n    "));
                out.println();
                out.println("<p lang=\"zh-cn\">");
                out.println("没有描述。");
                out.leave();
                out.flush();
                out.close();
            }
        }
    }

}
