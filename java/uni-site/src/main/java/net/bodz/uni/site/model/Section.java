package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;

import net.bodz.bas.c.java.nio.FileData;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.textmap.I18nTextMapDocLoader;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.PathResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.content.AbstractXjdocContent;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;
import net.bodz.bas.site.org.ICrawlable;
import net.bodz.bas.site.org.ICrawler;
import net.bodz.bas.std.rfc.http.ICacheControl;
import net.bodz.bas.t.variant.IVariantMap;
import net.bodz.mda.xjdoc.model.IMutableElementDoc;
import net.bodz.uni.site.UniSite;
import net.bodz.uni.site.util.DebControl;
import net.bodz.uni.site.util.DebControlParser;

import section.iface;

public class Section
        extends AbstractXjdocContent
        implements IPathDispatchable,
                   ICrawlable {

    static final Logger logger = LoggerFactory.getLogger(Section.class);

    private UniSite site;
    private String name;
    private Path directory;
    private Path docFile;
    private Map<String, Project> projectMap;

    public Section(UniSite site, String name, Path directory) {
        this.site = site;
        this.name = name;
        this.directory = directory;

        docFile = directory.resolve(name + ".itm");
        if (Files.notExists(docFile)) {
            docFile = directory.resolve("." + name + ".itm");
            if (Files.notExists(docFile))
                docFile = null;
        }

        this.projectMap = new TreeMap<>();
    }

    public void load() {
        try (Stream<Path> stream = Files.list(directory)) {
            stream.forEach(this::loadProject);
        } catch (IOException e) {
            logger.error("Error listing " + directory);
        }
    }

    public void loadProject(Path projectDir) {
        if (!Files.isDirectory(projectDir))
            return;

        String name = projectDir.getFileName().toString();

        // A debian project.
        Path controlFile = projectDir.resolve("debian/control");
        if (Files.exists(controlFile)) {
            String controlStr;
            try {
                controlStr = FileData.readString(controlFile);
            } catch (IOException e) {
                logger.errorf(e, "Failed to read the control file %s.", controlFile);
                return;
            }
            DebControl debControl = new DebControlParser().parse(controlStr);
            DebProject project = new DebProject(this, name, projectDir);
            project.setDebControl(debControl);
            addProject(project);
            return;
        }

        // A patch project.
        Path origVersionFile = projectDir.resolve("version");
        if (Files.exists(origVersionFile)) {
            String origVersion;
            try {
                origVersion = FileData.readString(origVersionFile);
            } catch (IOException e) {
                logger.errorf(e, "Failed to read the version file %s.", controlFile);
                return;
            }
            PatchProject project = new PatchProject(this, name, projectDir);
            project.setVersion(origVersion);
            addProject(project);
            return;
        }

        // A Maven-based Java project.
        Path pomFile = projectDir.resolve("pom.xml");
        if (Files.exists(pomFile)) {
            // ...
            return;
        }

        // Other, skip it.
    }

    @Override
    protected IMutableElementDoc loadXjdoc()
            throws ParseException, IOException {
        if (Files.notExists(docFile))
            throw new IOException("No doc file: " + docFile);
        IMutableElementDoc doc = I18nTextMapDocLoader.load(new PathResource(docFile));
        return doc;
    }

    public UniSite getSite() {
        return site;
    }

    @Override
    public String getName() {
        return name;
    }

    public Path getDirectory() {
        return directory;
    }

    public Path getDocFile() {
        return docFile;
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

    /** ⇱ Implementation Of {@link ICacheControl}. */
    /* _____________________________ */static iface __CACHE__;

    @Override
    public Integer getMaxAge() {
        return 3600 * 12;
    }

    /** ⇱ Implementation Of {@link IPathDispatchable}. */
    /* _____________________________ */static iface __DISPATCH__;

    @Override
    public IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens, IVariantMap<String> q)
            throws PathDispatchException {
        String token = tokens.peek();
        if (token == null)
            return null;

        Project project = projectMap.get(token);
        if (project == null)
            return null;
        else
            return PathArrival.shift(previous, this, project, tokens);
    }

    @Override
    public void crawlableIntrospect(ICrawler crawler) {
        for (Entry<String, Project> entry : projectMap.entrySet())
            crawler.follow(entry.getKey() + "/", entry.getValue());
    }

}
