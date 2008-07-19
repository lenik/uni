package net.bodz.lapiota.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.Files;
import net.bodz.lapiota.util.BatchProcessCLI;
import net.bodz.lapiota.util.ProgramName;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

@Doc("Batch package-fragment (in *.jar or directory format) process")
@Version( { 0, 0 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@RunInfo(lib = { "dom4j", "jaxen" })
@ProgramName("jars")
public class PackageFragmentsProcess extends BatchProcessCLI {

    @Option(alias = "F", vnam = "pretty|compact", parser = OutputFormatParser.class)
    protected OutputFormat outputFormat = new OutputFormat();

    private List<Action>   actions      = new ArrayList<Action>();

    @Option(alias = "a", vnam = "ACTION=PARAM[,...]", doc = "add an action")
    protected void action(Action action) throws CLIException {
        L.x.P("action: ", action);
        actions.add(action);
    }

    public PackageFragmentsProcess() {
        plugins.registerPluginType("action", Action.class);
        plugins.register("test", TestHandler.class, this);
        plugins.register("cat", Cat.class, this);
        // actionPoint.register("mani", ManiCat.class, this);
        plugins.register("pointref", PointRef.class, this);
        plugins.register("grep", Grep.class, this);
    }

    @Override
    protected ProcessResult process(File in) throws Throwable {
        if (in.isDirectory()) { // dir
            for (Action act : actions) {
                act.handle(in);
            }
            return ProcessResult.pass("dir");
        } else { // .jar
            for (Action act : actions) {
                JarFile jar;
                try {
                    jar = new JarFile(in);
                } catch (IOException e) {
                    return null;
                }
                act.handle(jar);
            }
            return ProcessResult.pass("jar");
        }
    }

    @Override
    protected InputStream _getDefaultIn() {
        return null;
    }

    public static void main(String[] args) throws Throwable {
        new PackageFragmentsProcess().climain(args);
    }

    static interface Action extends CLIPlugin {

        void handle(File dir) throws Throwable;

        void handle(JarFile jar) throws Throwable;

    }

    abstract class _Action extends _CLIPlugin implements Action {

        protected abstract String getPath();

        protected boolean handleNull() {
            return false;
        }

        public abstract void handle(URL url, String content) throws Throwable;

        public void handle(URL url, String[] list) throws Throwable {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handle(File dir) throws Throwable {
            String path = getPath();
            File file = new File(dir, path).getCanonicalFile();
            URL url = file.toURI().toURL();
            if (file.isDirectory()) {
                handle(url, file.list());
                return;
            }
            String content = null;
            if (file.canRead())
                content = Files.readAll(file, inputEncoding);
            else if (!handleNull())
                return;
            handle(url, content);
        }

        @Override
        public void handle(JarFile jar) throws Throwable {
            String path = getPath();
            JarEntry entry = jar.getJarEntry(path);
            File _file = new File(jar.getName()).getCanonicalFile();
            String uri = "jar:" + _file.toURI() + "!/" + path;
            URL url = new URL(uri);

            if (entry == null) {
                if (handleNull())
                    handle(url, (String) null);
                return;
            }
            if (entry.isDirectory()) {
                Enumeration<JarEntry> entries = jar.entries();
                List<String> buf = new ArrayList<String>();
                while (entries.hasMoreElements()) {
                    JarEntry e = entries.nextElement();
                    String ename = e.getName();
                    if (ename.startsWith(path))
                        buf.add(ename);
                }
                String[] list = buf.toArray(new String[0]);
                handle(url, list);
                return;
            }
            InputStream in = jar.getInputStream(entry);
            String content = Files.readAll(in, inputEncoding);
            in.close();
            handle(url, content);
        }

    }

    class TestHandler extends _Action {

        private String path;

        public TestHandler() {
        }

        public TestHandler(String[] args) {
            if (args.length == 0)
                path = ".";
            else
                path = args[0];
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void handle(URL url, String content) throws Throwable {
            _stdout.println("Content: " + url);
            _stdout.println(content);
            _stdout.println();
        }

        @Override
        public void handle(URL url, String[] list) throws Throwable {
            _stdout.println("Directory: " + url);
            for (String entry : list) {
                _stdout.print("    ");
                _stdout.println(entry);
            }
            _stdout.println();
        }

    }

    static String MANIFEST = "META-INF/MANIFEST.MF";

    @Doc("=[FILE] dump specified file")
    class Cat extends _Action {

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
            System.out.println(url + ":");
            System.out.println(content);
            System.out.println();
        }
    }

    @Doc("=PATTERN[,FILE], search by regexp")
    class Grep extends _Action {

        private String  path;
        private Pattern pattern;

        @Option(doc = "trim the output lines")
        private boolean trim;

        public Grep() {
        }

        public Grep(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(
                        "Grep(REGEXP, [FILE=plugin.xml])");

            int flags = 0;
            if (ignoreCase)
                flags |= Pattern.CASE_INSENSITIVE;
            pattern = Pattern.compile(args[0], flags);

            if (args.length == 1)
                path = "plugin.xml";
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
                        L.m.P(url, ": ");
                    }
                    if (trim)
                        line = line.trim();
                    _stdout.printf("%4d %s\n", lineNo, line);
                }
            }
            in.close();
        }
    }

    abstract class XpathSearch<Ct> extends _Action {

        private String path;
        private String critarg;
        private Ct     criteria;

        public XpathSearch() {
        }

        public XpathSearch(String[] args) {
            if (args.length < 1)
                throw new IllegalArgumentException(getClass().getSimpleName()
                        + "(" + getCriteriaVnam() + ", [XML="
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
        public void handle(URL url, String content) throws Throwable {
            // DocumentFactory factory = DocumentFactory.getInstance();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new StringReader(content));

            String _xpath = getXpath();
            XPath extensionOfPoint = doc.createXPath(_xpath);
            for (Object node : extensionOfPoint.selectNodes(doc)) {
                if (!(node instanceof Node)) {
                    L.x.P("skip node: ", node);
                    continue;
                }
                Element ext = (Element) node;
                L.i.P("extension: ", url);
                String shortText = getShortText(ext);
                _stdout.println(critarg + ": " + shortText);
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
            return "//extension[@point=\"" + criteria + "\"]";
        }

        @Override
        protected String getCriteriaVnam() {
            return "pointId";
        }

        @Override
        protected String getShortText(Node node) {
            if (!(node instanceof Element))
                return "Unexpected";
            Element elm = (Element) node;
            return elm.attribute("id").getText();
        }
    }

}
