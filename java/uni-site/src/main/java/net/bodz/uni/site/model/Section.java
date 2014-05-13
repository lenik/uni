package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import net.bodz.bas.c.java.io.FileData;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;
import net.bodz.bas.text.textmap.I18nTextMapDocLoader;
import net.bodz.mda.xjdoc.FlatfDocLoader;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.AbstractXjdocElement;
import net.bodz.uni.site.util.DebControl;
import net.bodz.uni.site.util.DebControlParser;

public class Section
        extends AbstractXjdocElement
        implements IPathDispatchable {

    static final Logger logger = LoggerFactory.getLogger(Section.class);
    static FlatfDocLoader flatfDocLoader = new FlatfDocLoader();

    private Site site;
    private String name;
    private File directory;
    private Map<String, Project> projectMap;

    public Section(Site site, String name, File directory) {
        this.site = site;
        this.name = name;
        this.directory = directory;
        this.projectMap = new TreeMap<>();
    }

    public void load() {
        for (File projectDir : directory.listFiles()) {
            if (!projectDir.isDirectory())
                continue;

            String name = projectDir.getName();

            // A debian project.
            File controlFile = new File(projectDir, "debian/control");
            if (controlFile.exists()) {
                String controlStr;
                try {
                    controlStr = FileData.readString(controlFile);
                } catch (IOException e) {
                    logger.errorf(e, "Failed to read the control file %s.", controlFile);
                    continue;
                }
                DebControl debControl = new DebControlParser().parse(controlStr);
                DebProject project = new DebProject(this, name, projectDir);
                project.setDebControl(debControl);
                addProject(project);
                continue;
            }

            // A patch project.
            File origVersionFile = new File(projectDir, "version");
            if (origVersionFile.exists()) {
                String origVersion;
                try {
                    origVersion = FileData.readString(origVersionFile);
                } catch (IOException e) {
                    logger.errorf(e, "Failed to read the version file %s.", controlFile);
                    continue;
                }
                PatchProject project = new PatchProject(this, name, projectDir);
                project.setVersion(origVersion);
                addProject(project);
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
    protected IElementDoc loadXjdoc()
            throws ParseException, IOException {
        File contentFile = new File(directory, ".Content");
        // flatfDocLoader.load(new FileResource(contentFile));
        IElementDoc doc = I18nTextMapDocLoader.load(new FileResource(contentFile));
        return doc;
    }

    public Site getSite() {
        return site;
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<Project> getProjects() {
        return projectMap.values();
    }

    public void addProject(Project project) {
        if (project == null)
            throw new NullPointerException("project");
        String name = project.getName();
        projectMap.put(name, project);
    }

    /** ⇱ Implementation Of {@link IPathDispatchable}. */
/* _____________________________ */static section.iface __DISPATCH__;

    @Override
    public IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens)
            throws PathDispatchException {
        String token = tokens.peek();
        Project project = projectMap.get(token);
        if (project == null)
            return null;
        else
            return PathArrival.shift(previous, project, tokens);
    }

}
