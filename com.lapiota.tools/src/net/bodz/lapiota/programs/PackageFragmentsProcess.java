package net.bodz.lapiota.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.cli.BatchProcessCLI;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;
import net.bodz.bas.io.Files;
import net.bodz.lapiota.ant.tasks.ProgramName;
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

    @Option(alias = "a", vnam = "ACTION:PARAM[,...]", doc = "add an action")
    protected void action(String actexp) {
        int paramoff = actexp.indexOf('=');
        String act = actexp;
        String[] params = {};
        if (paramoff != -1) {
            act = actexp.substring(0, paramoff);
            actexp = actexp.substring(paramoff + 1);
            params = actexp.split(",");
        }
        Handler handler = newHandler(act, params);
        L.x.P("handler of ", act, ": ", handler);
        actions.add(handler);
    }

    private List<Handler> actions;

    public PackageFragmentsProcess() {
        actions = new ArrayList<Handler>();
    }

    @Override
    protected ProcessResult process(File in) throws Throwable {
        if (in.isDirectory()) { // dir
            for (Handler act : actions) {
                act.handle(in);
            }
        } else { // .jar
            for (Handler act : actions) {
                JarFile jar;
                try {
                    jar = new JarFile(in);
                } catch (IOException e) {
                    return null;
                }
                act.handle(jar);
            }
        }
        return ProcessResult.unchanged();
    }

    @Override
    protected int _cliflags() {
        return super._cliflags() & ~CLI_AUTOSTDIN;
    }

    public static void main(String[] args) throws Throwable {
        new PackageFragmentsProcess().climain(args);
    }

    static Map<String, Class<? extends Handler>> handlerTypes;
    static {
        handlerTypes = new HashMap<String, Class<? extends Handler>>();
    }

    static void register(String name, Class<? extends Handler> clazz) {
        handlerTypes.put(name, clazz);
    }

    Handler newHandler(String name, String[] parameters) {
        Class<? extends Handler> clazz = handlerTypes.get(name);
        Handler handler;
        if (clazz == null)
            throw new IllegalArgumentException("invalid handler: " + name);
        try {
            if (clazz.isMemberClass()) {
                L.x.P("create member class: " + clazz);
                Constructor<? extends Handler> ctor = clazz
                        .getDeclaredConstructor(getClass());
                handler = ctor.newInstance(this);
            } else {
                L.x.P("create class: " + clazz);
                handler = clazz.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        handler.setParameters(parameters);
        return handler;
    }

    interface Handler {

        void setParameters(String[] parameters);

        void handle(File dir) throws Throwable;

        void handle(JarFile jar) throws Throwable;

    }

    abstract class _Handler implements Handler {

        protected String[] parameters;

        @Override
        public void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

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

    class TestHandler extends _Handler {

        @Override
        public String getPath() {
            if (parameters.length == 0)
                return ".";
            return parameters[0];
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

    class Cat extends _Handler {
        @Override
        public String getPath() {
            if (parameters.length > 0)
                return parameters[0];
            return MANIFEST;
        }

        @Override
        public void handle(URL url, String content) throws Throwable {
            System.out.println(url + ":");
            System.out.println(content);
            System.out.println();
        }

    }

    class Grep extends _Handler {

        Pattern pattern;

        @Override
        public void setParameters(String[] parameters) {
            if (parameters.length < 1)
                throw new IllegalArgumentException(
                        "Grep(REGEXP, [FILE=plugin.xml])");
            super.setParameters(parameters);
            int flags = 0;
            if (ignoreCase)
                flags |= Pattern.CASE_INSENSITIVE;
            pattern = Pattern.compile(parameters[0], flags);
        }

        @Override
        protected String getPath() {
            if (parameters.length >= 2)
                return parameters[1];
            return "plugin.xml";
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
                    _stdout.printf("%4d %s\n", lineNo, line);
                }
            }
            in.close();
        }

    }

    abstract class XpathSearch<Ct> extends _Handler {

        protected String critarg;
        protected Ct     criteria;

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
        public void setParameters(String[] parameters) {
            if (parameters.length < 1)
                throw new IllegalArgumentException(getClass().getSimpleName()
                        + "(" + getCriteriaVnam() + ", [XML="
                        + getDefaultName() + "])");
            super.setParameters(parameters);
            criteria = parseCriteria(critarg = parameters[0]);
        }

        @Override
        public String getPath() {
            if (parameters.length >= 2)
                return parameters[1];
            return getDefaultName();
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

    class PointRef extends XpathSearch<String> {

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

    static {
        register("test", TestHandler.class);
        register("cat", Cat.class);
        // register("mani", ManiCat.class);
        register("pointref", PointRef.class);
        register("grep", Grep.class);
    }

}
