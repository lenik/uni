package net.bodz.lapiota.filesys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.io.FileFinder;
import net.bodz.bas.io.Files;
import net.bodz.bas.types.parsers.WildcardsParser;
import net.bodz.bas.types.util.Iterates;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;
import net.bodz.lapiota.wrappers.BatchEditCLI;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

@BootInfo(syslibs = { "dom4j", "jaxen" })
@Doc("Batch package-fragment (in *.jar or directory format) process")
@ProgramName("jars")
@RcsKeywords(id = "$Id$")
@Version( { 0, 0 })
public class PackageFragmentsProcess extends BatchEditCLI {

    @Option(alias = "F", vnam = "pretty|compact")
    @ParseBy(OutputFormatParser.class)
    protected OutputFormat outputFormat = new OutputFormat();

    private List<Action>   actions      = new ArrayList<Action>();

    @Option(alias = "a", vnam = "ACTION=PARAM[,...]", doc = "add an action")
    protected void action(Action action) throws CLIException {
        L.debug(CLINLS.getString("PackageFragmentsProcess.action_"), action); //$NON-NLS-1$
        actions.add(action);
    }

    public PackageFragmentsProcess() {
        plugins.registerCategory(CLINLS.getString("PackageFragmentsProcess.action"), Action.class); //$NON-NLS-1$
        plugins.register("list", Lister.class, this); //$NON-NLS-1$
        plugins.register("cat", Cat.class, this); //$NON-NLS-1$
        // actionPoint.register("mani", ManiCat.class, this);
        plugins.register("pointref", PointRef.class, this); //$NON-NLS-1$
        plugins.register("grep", Grep.class, this); //$NON-NLS-1$
        plugins.register("px", PartialExtract.class, this); //$NON-NLS-1$
    }

    @Override
    protected EditResult doEdit(File in) throws Throwable {
        if (in.isDirectory()) { // dir
            for (Action act : actions) {
                act.doDirectory(in);
            }
            return EditResult.pass("dir"); //$NON-NLS-1$
        } else { // .jar
            for (Action act : actions) {
                JarFile jar;
                try {
                    jar = new JarFile(in);
                } catch (IOException e) {
                    return null;
                }
                act.doJar(jar);
            }
            return EditResult.pass("jar"); //$NON-NLS-1$
        }
    }

    public static void main(String[] args) throws Throwable {
        new PackageFragmentsProcess().run(args);
    }

    static interface Action extends CLIPlugin {

        void doDirectory(File dir) throws Throwable;

        void doJar(JarFile jar) throws Throwable;

    }

    static abstract class _Action extends _CLIPlugin implements Action {
    }

    @Doc("list all files in dirs and archives")
    class Lister extends _Action {

        public Lister() {
        }

        @Override
        public void doDirectory(File dir) throws Throwable {
            int maxDepth = parameters().getRecursive();
            FileFinder finder = new FileFinder(maxDepth, dir);
            for (File f : finder.listFiles()) {
                String path = f.getPath();
                if (f.isDirectory())
                    path += "/"; //$NON-NLS-1$
                list(path);
            }
        }

        @Override
        public void doJar(JarFile jar) throws Throwable {
            for (JarEntry e : Iterates.iterate(jar.entries())) {
                String ename = e.getName();
                list(ename);
            }
        }

        void list(String entry) {
            System.out.println(entry);
        }

    }

    abstract class _EntryAction extends _Action {

        protected abstract String getPath();

        protected boolean handleNull() {
            return false;
        }

        public abstract void handle(URL url, String content) throws Throwable;

        public void handle(URL url, String[] list) throws Throwable {
            throw new UnsupportedOperationException();
        }

        @Override
        public void doDirectory(File dir) throws Throwable {
            String path = getPath();
            File file = Files.canoniOf(dir, path);
            URL url = Files.getURL(file);
            if (file.isDirectory()) {
                handle(url, file.list());
                return;
            }
            String content = null;
            if (file.canRead())
                content = Files.readAll(file, parameters().getInputEncoding());
            else if (!handleNull())
                return;
            handle(url, content);
        }

        @Override
        public void doJar(JarFile jar) throws Throwable {
            String path = getPath();
            JarEntry entry = jar.getJarEntry(path);
            File _file = Files.canoniOf(jar.getName());
            String uri = "jar:" + _file.toURI() + "!/" + path; //$NON-NLS-1$ //$NON-NLS-2$
            URL url = new URL(uri);

            if (entry == null) {
                if (handleNull())
                    handle(url, (String) null);
                return;
            }
            if (entry.isDirectory()) {
                List<String> buf = new ArrayList<String>();
                for (JarEntry e : Iterates.iterate(jar.entries())) {
                    String ename = e.getName();
                    if (ename.startsWith(path))
                        buf.add(ename);
                }
                String[] list = buf.toArray(new String[0]);
                handle(url, list);
                return;
            }
            InputStream in = jar.getInputStream(entry);
            String content = Files.readAll(in, parameters().getInputEncoding());
            in.close();
            handle(url, content);
        }

    }

    static String MANIFEST = "META-INF/MANIFEST.MF"; //$NON-NLS-1$

    @Doc("=[FILE] dump specified file")
    class Cat extends _EntryAction {

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
        public void handle(URL url, String content) throws Throwable {
            System.out.println(url + ":"); //$NON-NLS-1$
            System.out.println(content);
            System.out.println();
        }
    }

    @Doc("=PATTERN[,FILE], search by regexp")
    class Grep extends _EntryAction {

        @Option(doc = "trim the output lines")
        private boolean trim;

        private String  path;
        private Pattern pattern;

        public Grep() {
        }

        public Grep(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(CLINLS
                        .getString("PackageFragmentsProcess.grepShortHelp")); //$NON-NLS-1$

            int flags = 0;
            if (parameters().isIgnoreCase())
                flags |= Pattern.CASE_INSENSITIVE;
            pattern = Pattern.compile(args[0], flags);

            if (args.length == 1)
                path = "plugin.xml"; //$NON-NLS-1$
            else
                path = args[1];
        }

        @Override
        protected String getPath() {
            return path;
        }

        @Override
        public void handle(URL url, String content) throws Throwable {
            BufferedReader in = new BufferedReader(new StringReader(content));
            String line;
            int lineNo = 0;
            int matchedLines = 0;
            while ((line = in.readLine()) != null) {
                lineNo++;
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    if (matchedLines++ == 0) {
                        L.mesg(url, ": "); //$NON-NLS-1$
                    }
                    if (trim)
                        line = line.trim();
                    _stdout.printf("%4d %s\n", lineNo, line); //$NON-NLS-1$
                }
            }
            in.close();
        }
    }

    abstract class XpathSearch<Ct> extends _EntryAction {

        private String path;
        private String critarg;
        private Ct     criteria;

        public XpathSearch() {
        }

        public XpathSearch(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(getClass().getSimpleName()
                        + "(" + getCriteriaVnam() + ", [XML=" //$NON-NLS-2$
                        + getDefaultName() + "])"); //$NON-NLS-1$
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
            return "plugin.xml"; //$NON-NLS-1$
        }

        protected abstract String getShortText(Node node);

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void handle(URL url, String content) throws Throwable {
            // DocumentFactory factory = DocumentFactory.getInstance();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new StringReader(content));

            String _xpath = getXpath();
            XPath extensionOfPoint = doc.createXPath(_xpath);
            for (Object node : extensionOfPoint.selectNodes(doc)) {
                if (!(node instanceof Node)) {
                    L.debug(CLINLS.getString("PackageFragmentsProcess.skipNode"), node); //$NON-NLS-1$
                    continue;
                }
                Element ext = (Element) node;
                L.info(CLINLS.getString("PackageFragmentsProcess.extension"), url); //$NON-NLS-1$
                String shortText = getShortText(ext);
                _stdout.println(critarg + ": " + shortText); //$NON-NLS-1$
                if (L.showDetail()) {
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

    @Doc("=extension-point[,XML], dump plugin extension of specified point")
    class PointRef extends XpathSearch<String> {

        public PointRef() {
        }

        public PointRef(String[] args) {
            super(args);
        }

        @Override
        protected String parseCriteria(String criteria) {
            return CLINLS.getString("PackageFragmentsProcess.extensionCriteria") + criteria + "\"]"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        @Override
        protected String getCriteriaVnam() {
            return "pointId"; //$NON-NLS-1$
        }

        @Override
        protected String getShortText(Node node) {
            if (!(node instanceof Element))
                return CLINLS.getString("PackageFragmentsProcess.unexpected"); //$NON-NLS-1$
            Element elm = (Element) node;
            Attribute id = elm.attribute("id"); //$NON-NLS-1$
            if (id == null)
                return null;
            return id.getText();
        }
    }

    abstract class _JarAction extends _Action {

        @Override
        public void doDirectory(File dir) throws Throwable {
            L.detail(CLINLS.getString("PackageFragmentsProcess.skippedDir"), dir); //$NON-NLS-1$
        }

    }

    @Doc("Extract partial file in the archive")
    class PartialExtract extends _JarAction {

        @Option(vnam = "WILDCARDS", doc = "include these filenames")
        @ParseBy(WildcardsParser.class)
        Pattern includeFile;
        @Option(vnam = "WILDCARDS", doc = "exclude these filenames")
        @ParseBy(WildcardsParser.class)
        Pattern excludeFile;

        @Option(vnam = "REGEXP", doc = "include these pathnames")
        Pattern includePath;
        @Option(vnam = "REGEXP", doc = "exclude these pathnames")
        Pattern excludePath;

        @Option
        boolean includeImage;
        @Option
        boolean excludeImage;

        @Option
        boolean flatExtract;

        public PartialExtract() {
        }

        @Override
        public void doJar(JarFile jar) throws Throwable {
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
                        if (!destname.startsWith("/")) //$NON-NLS-1$
                            destname = "/" + destname; //$NON-NLS-1$
                        destname = Files.getName(jarFile) + destname;
                    }
                    File dest = getOutputFile(destname, start);
                    File destdir = dest.getParentFile();
                    destdir.mkdirs(); // return false if already exists.
                    L.detail(CLINLS.getString("PackageFragmentsProcess.extract"), dest); //$NON-NLS-1$
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
            String ext = Files.getExtension(fileName).toLowerCase();
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
        String[] exts = { "bmp", "ico", "gif", "jpg", "jpeg", "png" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        for (String ext : exts)
            imageExtensions.add(ext);
    }

}
