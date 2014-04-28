package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.bodz.bas.c.java.io.FileData;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;
import net.bodz.bas.text.att.AttributedText;
import net.bodz.lily.site.LilyStartSite;
import net.bodz.uni.site.util.DebControl;
import net.bodz.uni.site.util.DebControlParser;

/**
 * @label The Uni Site
 * @title Uni - Deveoper's Tools
 * @copyright (ↄ) Copyleft 2004-2014 Lenik
 */
public class Site
        extends LilyStartSite
        implements IPathDispatchable {

    static final Logger logger = LoggerFactory.getLogger(Site.class);

    /** Root directory of the uni project. */
    File root;

    public Map<String, Section> sectionMap = new TreeMap<>();
    public List<String> news;

    public String googleId;
    public String baiduId;

    public Site() {
        scan("/mnt/istore/projects/uni");
    }

    void scan(String rootPath) {
        root = new File(rootPath);
        for (File sectionDir : root.listFiles()) {
            if (!sectionDir.isDirectory())
                continue;

            File contentFile = new File(sectionDir, ".Content");
            if (!contentFile.exists())
                continue;

            String name = sectionDir.getName();
            Section section = new Section(name);
            sectionMap.put(name, section);

            AttributedText att = new AttributedText();
            try {
                String content = FileData.readString(contentFile);
                AttributedText.fn.parse(att, content);
            } catch (Exception e) {
                logger.errorf(e, "Failed to process %s.", contentFile);
                continue;
            }

            String label = att.getAttribute("label");
            // section.setLabel(label);

            String text = att.getText();
            if (text != null) {
                iString description = iString.fn.parseParaLangString(text);
                section.setDescription(description);
            }

            scanSection(section, sectionDir);
        }
    }

    void scanSection(Section section, File sectionDir) {
        for (File projectDir : sectionDir.listFiles()) {
            if (!projectDir.isDirectory())
                continue;

            String name = projectDir.getName();

            // A debian project.
            File controlFile = new File(projectDir, "debian/control");
            if (controlFile.exists()) {
                try {
                    String controlStr = FileData.readString(controlFile);
                    DebControl debControl = new DebControlParser().parse(controlStr);
                    DebProject project = new DebProject(name);
                    project.setDebControl(debControl);
                    section.addProject(project);
                } catch (IOException e) {
                    logger.errorf(e, "Failed to process %s.", controlFile);
                }
                continue;
            }

            // A Maven-based Java project.
            File pomFile = new File(projectDir, "pom.xml");
            if (pomFile.exists()) {
                // ...
                continue;
            }

            // Other, skip it.
        }
    }

    @Override
    public IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens)
            throws PathDispatchException {
        String token = tokens.peek();
        Section section = sectionMap.get(token);
        if (section == null)
            return null;
        else
            return PathArrival.shift(previous, section, tokens);
    }

}
