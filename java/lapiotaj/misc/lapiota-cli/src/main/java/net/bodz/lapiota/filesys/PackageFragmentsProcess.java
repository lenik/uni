package net.bodz.lapiota.filesys;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.bodz.bas.c.java.io.FileFinder;
import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.cli.plugin.AbstractCLIPlugin;
import net.bodz.bas.cli.plugin.CLIPlugin;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIException;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.util.iter.Iterables;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;

/**
 * Batch package-fragment (in *.jar or directory format) process
 */
// @BootInfo(syslibs = { "dom4j", "jaxen" })
@ProgramName("jars")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class PackageFragmentsProcess
        extends BatchEditCLI {

    /**
     * @option -F =pretty|compact
     */
    @ParseBy(OutputFormatParser.class)
    protected OutputFormat outputFormat = new OutputFormat();

    private List<Action> actions = new ArrayList<Action>();

    /**
     * Add an action
     *
     * @option -a =ACTION=PARAM[...]
     */
    protected void action(Action action)
            throws CLIException {
        L.debug(CLINLS.getString("PackageFragmentsProcess.action_"), action);
        actions.add(action);
    }

    public PackageFragmentsProcess() {
        plugins.registerCategory(CLINLS.getString("PackageFragmentsProcess.action"), Action.class);
        plugins.register("list", Lister.class, this);
        plugins.register("cat", Cat.class, this);
        // actionPoint.register("mani", ManiCat.class, this);
        plugins.register("pointref", PointRef.class, this);
        plugins.register("grep", Grep.class, this);
        plugins.register("px", PartialExtract.class, this);
    }

    @Override
    protected EditResult doEdit(File in)
            throws Exception {
        if (in.isDirectory()) { // dir
            for (Action act : actions) {
                act.doDirectoryArg(in);
            }
            return EditResult.pass("dir");
        } else { // .jar
            for (Action act : actions) {
                JarFile jar;
                try {
                    jar = new JarFile(in);
                } catch (IOException e) {
                    return null;
                }
                act.doJarArg(jar);
            }
            return EditResult.pass("jar");
        }
    }

    public static void main(String[] args)
            throws Exception {
        new PackageFragmentsProcess().execute(args);
    }

    static interface Action
            extends CLIPlugin {

        void doDirectoryArg(File dir)
                throws Exception;

        void doJarArg(JarFile jar)
                throws Exception;

    }

    static abstract class AbstractAction
            extends AbstractCLIPlugin
            implements Action {
    }

    /**
     * List all files in dirs and archives
     */
    class Lister
            extends AbstractAction {

        public Lister() {
        }

        @Override
        public void doDirectoryArg(File dir)
                throws Exception {
            int maxDepth = parameters().getRecursive();
            FileFinder finder = new FileFinder(maxDepth, dir);
            for (File f : finder.listFiles()) {
                String path = f.getPath();
                if (f.isDirectory())
                    path += "/";
                list(path);
            }
        }

        @Override
        public void doJarArg(JarFile jar)
                throws Exception {
            for (JarEntry e : Iterates.once(jar.entries())) {
                String ename = e.getName();
                list(ename);
            }
        }

        void list(String entry) {
            System.out.println(entry);
        }

    }

    abstract class _EntryAction
            extends AbstractAction {

        protected abstract String getPath();

        protected boolean handleNull() {
            return false;
        }

        public abstract void handle(URL url, String content)
                throws Exception;

        public void handle(URL url, String[] list)
                throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void doDirectoryArg(File dir)
                throws Exception {
            String path = getPath();
            File file = Files.canoniOf(dir, path);
            doDirectoryEntry(file);
        }

        public void doDirectoryEntry(File file)
                throws Exception {
            URL url = Files.getURL(file);
            if (file.isDirectory())
                handle(url, file.list());
            else {
                String content = null;
                if (file.canRead())
                    content = Files.readAll(file, parameters().getInputEncoding());
                else if (!handleNull())
                    return;
                handle(url, content);
            }
        }

        @Override
        public void doJarArg(JarFile jar)
                throws Exception {
            String path = getPath();
            JarEntry entry = jar.getJarEntry(path);
            doJarEntry(jar, entry);
        }

        public void doJarEntry(JarFile jar, JarEntry entry)
                throws Exception {
            String path = entry.getName();
            File _file = Files.canoniOf(jar.getName());
            String uri = "jar:" + _file.toURI() + "!/" + path;
            URL url = new URL(uri);

            if (entry == null) {
                if (handleNull())
                    handle(url, (String) null);
            } else if (entry.isDirectory()) {
                List<String> buf = new ArrayList<String>();
                for (JarEntry e : Iterables.otp(jar.entries())) {
                    String ename = e.getName();
                    if (ename.startsWith(path))
                        buf.add(ename);
                }
                String[] list = buf.toArray(new String[0]);
                handle(url, list);
            } else {
                InputStream in = jar.getInputStream(entry);
                String content = Files.readAll(in, parameters().getInputEncoding());
                in.close();
                handle(url, content);
            }
        }
    }

    static String MANIFEST = "META-INF/MANIFEST.MF";

    /**
     * =[FILE] dump specified file
     */
    class Cat
            extends _EntryAction {

        private String path;

        public Cat() {
        }

        public Cat(String[] args) {
            if (args.length == 0)
                path = MANIFEST;
            else
                path = args[0];
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void handle(URL url, String content)
                throws Exception {
            System.out.println(url + ":");
            System.out.println(content);
            System.out.println();
        }
    }

    /**
     * =PATTERN[,FILE], search by regexp
     */
    class Grep
            extends _EntryAction {

        /**
         * Trim the output lines
         *
         * @option
         */
        private boolean trim;

        private String path;
        private FilenameFilter pathFilter;
        private Pattern pattern;

        public Grep() {
        }

        public Grep(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(CLINLS.getString("PackageFragmentsProcess.grepShortHelp"));

            int flags = 0;
            if (parameters().isIgnoreCase())
                flags |= Pattern.CASE_INSENSITIVE;
            pattern = Pattern.compile(args[0], flags);

            String s = args.length < 2 ? "*" : args[1];
            if (s.contains("*") || s.contains("?"))
                pathFilter = new GlobFilter(s);
            else
                path = s;
        }

        @Override
        protected String getPath() {
            return path;
        }

        @Override
        public void doDirectoryArg(File dir)
                throws Exception {
            if (pathFilter == null)
                super.doDirectoryArg(dir);
            else {
                for (File f : dir.listFiles(pathFilter)) {
                    if (f.isFile())
                        super.doDirectoryEntry(f);
                    else
                        doDirectoryArg(f);
                }
            }
        }

        @Override
        public void doJarArg(JarFile jar)
                throws Exception {
            if (pathFilter == null)
                super.doJarArg(jar);
            else {
                for (JarEntry e : Iterables.otp(jar.entries())) {
                    if (pathFilter.accept(null, e.getName()))
                        doJarEntry(jar, e);
                }
            }
        }

        @Override
        public void handle(URL url, String[] list)
                throws Exception {
            int lineNo = 0;
            int matchedLines = 0;
            for (String line : list) {
                lineNo++;
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    if (matchedLines++ == 0)
                        L.mesg(url, ": ");
                    if (trim)
                        line = line.trim();
                    _stdout.printf("%4d %s\n", lineNo, line);
                }
            }
        }

        @Override
        public void handle(URL url, String content)
                throws Exception {
            BufferedReader in = new BufferedReader(new StringReader(content));
            String line;
            int lineNo = 0;
            int matchedLines = 0;
            while ((line = in.readLine()) != null) {
                lineNo++;
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    if (matchedLines++ == 0) {
                        L.mesg(url, ": ");
                    }
                    if (trim)
                        line = line.trim();
                    _stdout.printf("%4d %s\n", lineNo, line);
                }
            }
            in.close();
        }
    }

    abstract class XpathSearch<Ct>
            extends _EntryAction {

        private String path;
        private String critarg;
        private Ct criteria;

        public XpathSearch() {
        }

        public XpathSearch(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(getClass().getSimpleName() + "(" + getCriteriaVnam() + ", [XML="
                        + getDefaultName() + "])");
            criteria = parseCriteria(critarg = args[0]);

            if (args.length == 1)
                path = getDefaultName();
            else
                path = args[1];
        }

        protected abstract Ct parseCriteria(String criteria);

        protected abstract String getCriteriaVnam();

        protected String getXpath() {
            return String.valueOf(criteria);
        }

        protected String getDefaultName() {
            return "plugin.xml";
        }

        protected abstract String getShortText(Node node);

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void handle(URL url, String content)
                throws Exception {
            // DocumentFactory factory = DocumentFactory.getInstance();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new StringReader(content));

            String _xpath = getXpath();
            XPath extensionOfPoint = doc.createXPath(_xpath);
            for (Object node : extensionOfPoint.selectNodes(doc)) {
                if (!(node instanceof Node)) {
                    L.debug(CLINLS.getString("PackageFragmentsProcess.skipNode"), node);
                    continue;
                }
                Element ext = (Element) node;
                L.info(CLINLS.getString("PackageFragmentsProcess.extension"), url);
                String shortText = getShortText(ext);
                _stdout.println(critarg + ": " + shortText);
                if (L.isInfoEnabled(1)) {
                    String xmlenc = doc.getXMLEncoding();
                    if (xmlenc != null)
                        outputFormat.setEncoding(xmlenc);
                    OutputStream out = System.out;
                    XMLWriter writer = new XMLWriter(out, outputFormat);
                    writer.write(doc);
                }
            }
        }
    }

    /**
     * =extension-point[,XML], dump plugin extension of specified point
     */
    class PointRef
            extends XpathSearch<String> {

        public PointRef() {
        }

        public PointRef(String[] args) {
            super(args);
        }

        @Override
        protected String parseCriteria(String criteria) {
            return CLINLS.getString("PackageFragmentsProcess.extensionCriteria") + criteria + "\"]";
        }

        @Override
        protected String getCriteriaVnam() {
            return "pointId";
        }

        @Override
        protected String getShortText(Node node) {
            if (!(node instanceof Element))
                return CLINLS.getString("PackageFragmentsProcess.unexpected");
            Element elm = (Element) node;
            Attribute id = elm.attribute("id");
            if (id == null)
                return null;
            return id.getText();
        }
    }

    abstract class _JarAction
            extends AbstractAction {

        @Override
        public void doDirectoryArg(File dir)
                throws Exception {
            L.info(CLINLS.getString("PackageFragmentsProcess.skippedDir"), dir);
        }

    }

    /**
     * Extract partial file in the archive
     */
    class PartialExtract
            extends _JarAction {

        /**
         * include these filenames
         *
         * @option =WILDCARDS
         */
        @ParseBy(GlobParser.class)
        Pattern includeFile;
        /**
         * exclude these filenames
         *
         * @option =WILDCARDS
         */
        @ParseBy(GlobParser.class)
        Pattern excludeFile;

        /**
         * include these pathnames
         *
         * @option =REGEXP
         */
        Pattern includePath;
        /**
         * exclude these pathnames
         *
         * @option =REGEXP
         */
        Pattern excludePath;

        /**
         * @option
         */
        boolean includeImage;
        /**
         * @option
         */
        boolean excludeImage;

        /**
         * @option
         */
        boolean flatExtract;

        public PartialExtract() {
        }

        @Override
        public void doJarArg(JarFile jar)
                throws Exception {
            File jarFile = Files.canoniOf(jar.getName());
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String ename = entry.getName();
                if (match(ename)) {
                    File start = jarFile.getParentFile();
                    assert start != null;
                    String destname = ename;
                    if (!flatExtract) {
                        if (!destname.startsWith("/"))
                            destname = "/" + destname;
                        destname = FilePath.stripExtension(jarFile) + destname;
                    }
                    File dest = getOutputFile(destname, start);
                    File destdir = dest.getParentFile();
                    destdir.mkdirs(); // return false if already exists.
                    L.info(CLINLS.getString("PackageFragmentsProcess.extract"), dest);
                    InputStream in = jar.getInputStream(entry);
                    try {
                        Files.copy(in, dest);
                    } finally {
                        in.close();
                    }
                }
            }
        }

        boolean match(String path) {
            File fileName = new File(path);
            if (includeFile != null)
                if (!includeFile.matcher(fileName.getName()).matches())
                    return false;
            if (excludeFile != null)
                if (excludeFile.matcher(fileName.getName()).matches())
                    return false;
            if (includePath != null)
                if (!includePath.matcher(path).find())
                    return false;
            if (excludePath != null)
                if (excludePath.matcher(path).find())
                    return false;
            String ext = FilePath.getExtension(fileName).toLowerCase();
            boolean isImage = imageExtensions.contains(ext);
            if (includeImage && !isImage)
                return false;
            if (excludeImage && isImage)
                return false;
            return true;
        }
    }

    static Set<String> imageExtensions;
    static {
        imageExtensions = new HashSet<String>();
        String[] exts = { "bmp", "ico", "gif", "jpg", "jpeg", "png" };
        for (String ext : exts)
            imageExtensions.add(ext);
    }

}
