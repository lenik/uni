package net.bodz.lapiota.filesys;

import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.util.regex.PatternTraits;
import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.cli.plugin.AbstractCLIPlugin;
import net.bodz.bas.cli.plugin.ICLIPlugin;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIAccessor;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.cli.skel.FileHandler;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.lang.TypeParameter;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.IFilenameFilter;
import net.bodz.bas.vfs.util.find.FileFinder;

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
    protected OutputFormat outputFormat = new OutputFormat();

    private List<Action> actions = new ArrayList<Action>();

    /**
     * Add an action
     *
     * @option -a =ACTION=PARAM[...]
     */
    protected void action(Action action) {
        logger.debug(tr._("action: "), action);
        actions.add(action);
    }

    public PackageFragmentsProcess() {
        plugins.addCategory(tr._("action"), Action.class);
        plugins.register("list", Lister.class, this);
        plugins.register("cat", Cat.class, this);
        // actionPoint.register("mani", ManiCat.class, this);
        plugins.register("pointref", PointRef.class, this);
        plugins.register("grep", Grep.class, this);
        plugins.register("px", PartialExtract.class, this);
    }

    @Override
    protected void processImpl(FileHandler handler)
            throws Exception {
        IFile root = handler.getFile();

        if (root.isBlob()) {
            // TODO if file is jar, file=new JarFile(file); else skip
        }
        if (root.isDirectory()) { // dir
            for (Action action : actions) {
                action.doRootDir(root);
            }
            return EditResult.pass("tree");
        } else
            return EditResult.pass("non-tree");
    }

    public static void main(String[] args)
            throws Exception {
        new PackageFragmentsProcess().execute(args);
    }

    static interface Action
            extends ICLIPlugin {

        void doRootDir(IFile dir)
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
        public void doRootDir(IFile rootDir)
                throws Exception {
            int maxDepth = CLIAccessor.getRecursive(PackageFragmentsProcess.this);
            FileFinder finder = new FileFinder(maxDepth, rootDir);
            for (IFile f : finder.listFiles()) {
                String path = f.getPath().toString();
                if (f.isDirectory())
                    path += "/";
                list(path);
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
        public void doRootDir(IFile dir)
                throws Exception {
            String path = getPath();
            IFile file = dir.getChild(path); // resolve
            doDirectoryEntry(file);
        }

        public void doDirectoryEntry(IFile file)
                throws Exception {
            URL url = file.getPath().toURL();
            if (file.isDirectory())
                handle(url, file.children());
            else {
                String content = null;
                if (file.isReadable()) {
                    // file.setPreferredCharset(parameters().getInputEncoding());
                    content = file.tooling()._for(StreamReading.class).readString();
                } else if (!handleNull())
                    return;
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
        private IFilenameFilter pathFilter;
        private Pattern pattern;

        public Grep() {
        }

        public Grep(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(tr._("Grep(REGEXP, [FILE=plugin.xml])"));

            int flags = 0;
            if (CLIAccessor.isIgnoreCase(PackageFragmentsProcess.this))
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
        public void doRootDir(IFile dir)
                throws Exception {
            if (pathFilter == null)
                super.doRootDir(dir);
            else {
                for (IFile f : dir.children(pathFilter)) {
                    if (f.isDirectory())
                        doRootDir(f);
                    else
                        super.doDirectoryEntry(f);
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
                        logger.mesg(url, ": ");
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
                        logger.mesg(url, ": ");
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
                    logger.debug(tr._("skip node: "), node);
                    continue;
                }
                Element ext = (Element) node;
                logger.info(tr._("extension: "), url);
                String shortText = getShortText(ext);
                _stdout.println(critarg + ": " + shortText);
                if (logger.isInfoEnabled(1)) {
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
            return tr._("//extension[@point=\"") + criteria + "\"]";
        }

        @Override
        protected String getCriteriaVnam() {
            return "pointId";
        }

        @Override
        protected String getShortText(Node node) {
            if (!(node instanceof Element))
                return tr._("Unexpected");
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
        public void doRootDir(IFile dir)
                throws Exception {
            logger.info(tr._("skipped directory "), dir);
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
        @TypeParameter(id = PatternTraits.textformMode, value = PatternTraits.globTextformMode)
        Pattern includeFile;
        /**
         * exclude these filenames
         *
         * @option =WILDCARDS
         */
        @TypeParameter(id = PatternTraits.textformMode, value = PatternTraits.globTextformMode)
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
