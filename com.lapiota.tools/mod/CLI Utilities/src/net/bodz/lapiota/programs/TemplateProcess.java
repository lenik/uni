package net.bodz.lapiota.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.text.interp.Quotable;
import net.bodz.bas.text.interp.VariableExpand;
import net.bodz.lapiota.annotations.ProgramName;
import net.bodz.lapiota.util.GroovyExpand;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("General template processing")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@RunInfo(lib = "groovy")
@ProgramName("jtem")
public class TemplateProcess extends BatchProcessCLI {

    @Option(alias = "S", vnam = "MODEL=PARAM...", doc = "source parsing model")
    protected SourceModel   sourceModel;

    @Option(alias = "M", vnam = "MODEL=PARAM...", doc = "template processing model")
    protected TemplateModel templateModel;

    public static void main(String[] args) throws Throwable {
        new TemplateProcess().climain(args);
    }

    public TemplateProcess() {
        plugins.registerPluginType("source model", SourceModel.class);
        plugins.register("ini", VariableDefSource.class, this);
        plugins.register("csv", CSVDefSource.class, this);
        plugins.registerPluginType("template model", TemplateModel.class);
        plugins.register("ve", VariableExpandTemplate.class, this);
        plugins.register("gsp", GroovyTemplate.class, this);
    }

    // Plugin Interfaces

    static interface SourceModel extends CLIPlugin {
        void reset(File sourceFile) throws Exception;

        boolean next() throws Exception;

        String getDestFile();

        Object getContext();
    }

    abstract static class _SourceModel extends _CLIPlugin implements
            SourceModel {
    }

    static interface TemplateModel extends CLIPlugin {
        String expand(Object context) throws Exception;
    }

    abstract static class _TemplateModel extends _CLIPlugin implements
            TemplateModel {
    }

    // Plugin Implementations

    @Doc("INI style [file] sections")
    class VariableDefSource extends _SourceModel {

        @Option(doc = "comment char")
        String              commentChar = ";";

        @Option(doc = "enable using `file=key' line as separator")
        String              fileKey     = null;

        BufferedReader      lineIn;
        String              lastFile;
        String              file;
        Map<String, String> properties;

        public VariableDefSource() {
            properties = new HashMap<String, String>();
        }

        @Override
        public void reset(File sourceFile) throws IOException {
            lineIn = Files.getBufferedReader(sourceFile);
            // file = Files.getName(sourceFile); // out=file + tmplExt
            properties.clear();
        }

        @Override
        public boolean next() throws Exception {
            String line;
            while ((line = lineIn.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(commentChar))
                    continue;
                if (line.startsWith("[")) {
                    line = line.substring(1);
                    if (line.endsWith("]"))
                        line = line.substring(0, line.length() - 1);
                    if (setNextFile(line))
                        return true;
                    continue;
                }
                int eq = line.indexOf('=');
                String name = line;
                String value = "1";
                if (eq != -1) {
                    name = line.substring(0, eq).trim();
                    value = line.substring(eq + 1).trim();
                }
                if (fileKey != null && name.equals(fileKey)) {
                    if (setNextFile(name))
                        return true;
                    continue;
                }
                properties.put(name, value);
            }
            return setNextFile(null);
        }

        boolean setNextFile(String nextFile) {
            lastFile = file;
            file = nextFile;
            return lastFile != null;
        }

        @Override
        public Object getContext() {
            return properties;
        }

        @Override
        public String getDestFile() {
            return lastFile;
        }
    }

    @Doc("CSV source (the first row defines field names)")
    class CSVDefSource extends _SourceModel {

        @Option(doc = "comment char")
        String              commentChar = "#";

        @Option(doc = "field delimiter")
        String              delimiter   = ",";
        private Quotable    quot;

        @Option(doc = "max number of fields, see String#split(String, int)")
        int                 limit       = 0;

        @Option(doc = "the field names in CSV file when specified")
        String[]            names;

        @Option(doc = "trim field values, default true")
        boolean             trim        = true;

        BufferedReader      lineIn;
        String              file;
        Map<String, String> properties;

        @Override
        public void reset(File sourceFile) throws Exception {
            lineIn = Files.getBufferedReader(sourceFile);
            file = Files.getName(sourceFile); // out=file + tmplExt
            properties.clear();
        }

        @Override
        public void setParameters(Map<String, Object> parameters)
                throws CLIException, ParseException {
            super.setParameters(parameters);
            quot = new Quotable('"');
        }

        @Override
        public boolean next() throws Exception {
            String _delim = Pattern.quote(delimiter);
            String line;
            while ((line = lineIn.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(commentChar))
                    continue;
                int limit = this.limit;
                if (names != null && names.length < limit)
                    limit = names.length;

                String[] parts = quot.split(_delim, line, limit);
                if (names == null) {
                    names = parts;
                    continue;
                }
                properties.clear();
                assert parts.length <= names.length;
                for (int i = 0; i < parts.length; i++) {
                    String name = names[i];
                    String value = parts[i];
                    if (trim)
                        value = value.trim();
                    properties.put(name, value);
                }
                break;
            }
            return false;
        }

        @Override
        public Object getContext() {
            return properties;
        }

        @Override
        public String getDestFile() {
            return file;
        }
    }

    @Doc("Simple $variable expand")
    class VariableExpandTemplate extends _TemplateModel {

        @Option(doc = "expand environment variable")
        boolean          env;

        // @Option(alias = "t", vnam = "FILE", doc =
        // "template filename, used by specific template models")
        protected File   templateFile;
        protected String template;

        public VariableExpandTemplate() {
        }

        public VariableExpandTemplate(File templateFile) {
            this.templateFile = templateFile;
        }

        @SuppressWarnings("unchecked")
        @Override
        public String expand(Object context) throws Exception {
            if (template == null) {
                if (templateFile == null)
                    throw new CLIException("template file isn't specified");
                template = Files.readAll(templateFile, inputEncoding);
            }
            Map<String, Object> vars = (Map<String, Object>) context;
            if (env)
                vars.putAll(System.getenv());
            VariableExpand ve = new VariableExpand(vars);
            return ve.process(template);
        }
    }

    @Doc("Simple GSP (groovy server pages) expand")
    class GroovyTemplate extends _TemplateModel {

        @Option(doc = "expand environment variable")
        boolean          env;

        // @Option(alias = "t", vnam = "FILE", doc =
        // "template filename, used by specific template models")
        protected File   templateFile;
        protected String template;

        public GroovyTemplate() {
        }

        public GroovyTemplate(File templateFile) {
            this.templateFile = templateFile;
        }

        @SuppressWarnings("unchecked")
        @Override
        public String expand(Object context) throws Exception {
            if (template == null) {
                if (templateFile == null)
                    throw new CLIException("template file isn't specified");
                template = Files.readAll(templateFile, inputEncoding);
            }
            Map<String, Object> vars = (Map<String, Object>) context;
            if (env)
                vars.putAll(System.getenv());
            GroovyExpand ve = new GroovyExpand(vars);
            return ve.process(template);
        }
    }

}
