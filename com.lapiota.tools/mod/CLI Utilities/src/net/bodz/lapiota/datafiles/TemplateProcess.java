package net.bodz.lapiota.datafiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.BatchEditCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.EvalException;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.text.util.Interps;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.util.GroovyExpand;

import org.codehaus.groovy.control.CompilationFailedException;

@BootInfo(syslibs = "groovy")
@Doc("General template processing")
@ProgramName("jsub")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class TemplateProcess extends BatchEditCLI {

    Charset       inputEncoding;
    Charset       outputEncoding;

    @Option(alias = "S", vnam = "MODEL=PARAM...", doc = "source parsing model")
    SourceModel   sourceModel;

    @Option(alias = "M", vnam = "MODEL=PARAM...", doc = "template processing model")
    TemplateModel templateModel;

    @Option(alias = "x", vnam = "EXT", doc = "add extension to file names")
    String        extension;

    public TemplateProcess() {
        plugins
                .registerCategory(
                        CLINLS.getString("TemplateProcess.sourceModel"), SourceModel.class); //$NON-NLS-1$
        plugins.register("ini", VariableDefSource.class, this); //$NON-NLS-1$
        plugins.register("csv", CSVDefSource.class, this); //$NON-NLS-1$
        plugins.registerCategory(
                CLINLS.getString("TemplateProcess.templateModel"), TemplateModel.class); //$NON-NLS-1$
        plugins.register("ve", VariableExpandTemplate.class, this); //$NON-NLS-1$
        plugins.register("gsp", GroovyTemplate.class, this); //$NON-NLS-1$
    }

    @Override
    protected void _boot() throws Exception {
        inputEncoding = parameters().getInputEncoding();
        outputEncoding = parameters().getOutputEncoding();
    }

    @Override
    protected EditResult doEdit(File file, File editTmp) throws Exception {
        sourceModel.reset(file);
        while (sourceModel.next()) {
            Object context = sourceModel.getContext();
            String destFile = sourceModel.getDestFile();
            if (extension != null)
                destFile += extension;

            L.mesg("file ", destFile); //$NON-NLS-1$
            String contents = templateModel.expand(context);

            File defaultStart = file.getParentFile();

            File dst = getOutputFile(destFile, defaultStart);
            Files.write(editTmp, contents, outputEncoding);

            EditResult result = EditResult.compareAndSave();
            addResult(dst, dst, editTmp, result);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        new TemplateProcess().run(args);
    }

    // Plugin Interfaces

    static interface SourceModel extends CLIPlugin {
        void reset(File sourceFile) throws Exception;

        boolean next() throws Exception;

        String getDestFile();

        Object getContext();
    }

    abstract static class _SourceModel extends _CLIPlugin implements SourceModel {
    }

    static interface TemplateModel extends CLIPlugin {
        String expand(Object context) throws Exception;
    }

    abstract static class _TemplateModel extends _CLIPlugin implements TemplateModel {
    }

    // Plugin Implementations

    @Doc("INI style [file] sections")
    class VariableDefSource extends _SourceModel {

        @Option(doc = "comment char")
        String              commentChar = ";"; //$NON-NLS-1$

        @Option(doc = "enable using `file=key' line as separator")
        String              fileKey     = null;

        BufferedReader      lineIn;
        String              lastFile;
        String              file;
        Map<String, String> variables;

        public VariableDefSource() {
        }

        @Override
        public void reset(File sourceFile) throws IOException {
            lineIn = Files.getBufferedReader(sourceFile);
            // file = Files.getName(sourceFile); // out=file + tmplExt
            if (variables == null)
                variables = new HashMap<String, String>();
            else
                variables.clear();
        }

        @Override
        public boolean next() throws Exception {
            String line;
            while ((line = lineIn.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(commentChar))
                    continue;
                if (line.startsWith("[")) { //$NON-NLS-1$
                    line = line.substring(1);
                    if (line.endsWith("]")) //$NON-NLS-1$
                        line = line.substring(0, line.length() - 1);
                    if (setNextFile(line))
                        return true;
                    continue;
                }
                int eq = line.indexOf('=');
                String name = line;
                String value = "1"; //$NON-NLS-1$
                if (eq != -1) {
                    name = line.substring(0, eq).trim();
                    value = line.substring(eq + 1).trim();
                }
                if (fileKey != null && name.equals(fileKey)) {
                    if (setNextFile(name))
                        return true;
                    continue;
                }
                variables.put(name, value);
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
            return variables;
        }

        @Override
        public String getDestFile() {
            return lastFile;
        }
    }

    @Doc("CSV source (the first row defines field names)")
    class CSVDefSource extends _SourceModel {

        @Option(doc = "comment char")
        String              commentChar = "#";   //$NON-NLS-1$

        @Option(doc = "field delimiter characters, default ','")
        String              delim       = ",";   //$NON-NLS-1$

        @Option(doc = "max number of fields, see String#split(String, int)")
        int                 limit       = 0;

        @Option(doc = "the field names in CSV file when specified")
        String[]            names;

        @Option(doc = "using this field as file name, default `file'")
        String              fileField   = "file"; //$NON-NLS-1$

        @Option(doc = "trim field values, default true")
        boolean             trim        = true;

        BufferedReader      lineIn;
        Map<String, String> variables;

        public CSVDefSource() {
        }

        @Override
        public void reset(File sourceFile) throws Exception {
            lineIn = Files.getBufferedReader(sourceFile);
            if (variables == null)
                variables = new HashMap<String, String>();
            else
                variables.clear();
        }

        @Override
        public void setParameters(Map<String, Object> parameters) throws CLIException,
                ParseException {
            super.setParameters(parameters);
        }

        @Override
        public boolean next() throws Exception {
            String _delim = Pattern.quote(delim);
            String line;
            while ((line = lineIn.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(commentChar))
                    continue;
                int limit = this.limit;
                if (names != null && names.length < limit)
                    limit = names.length;

                String[] parts = Strings.split(line, _delim.toCharArray(), limit);
                if (names == null) {
                    names = parts;
                    for (int i = 0; i < names.length; i++)
                        names[i] = names[i].trim();
                    continue;
                }
                variables.clear();
                assert parts.length <= names.length;
                for (int i = 0; i < parts.length; i++) {
                    String name = names[i].trim();
                    String value = parts[i];
                    if (trim)
                        value = value.trim();
                    variables.put(name, value);
                }
                return true;
            }
            return false;
        }

        @Override
        public Object getContext() {
            return variables;
        }

        @Override
        public String getDestFile() {
            if (variables == null)
                return null;
            return variables.get(fileField);
        }
    }

    @Doc("=template-file Simple $variable expand")
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
                    throw new CLIException(CLINLS
                            .getString("TemplateProcess.templateIsntSpecified")); //$NON-NLS-1$
                template = Files.readAll(templateFile, inputEncoding);
            }
            Map<String, Object> vars = (Map<String, Object>) context;
            if (env)
                vars.putAll(System.getenv());
            return Interps.dereference(template, vars);
        }
    }

    @Doc("=template.gsp Simple GSP (groovy server pages) expand")
    class GroovyTemplate extends _TemplateModel {

        @Option(doc = "expand environment variable")
        boolean          env;

        @Option(doc = "variables must be defined")
        boolean          strict;

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
                    throw new CLIException(CLINLS
                            .getString("TemplateProcess.templateIsntSpecified")); //$NON-NLS-1$
                template = Files.readAll(templateFile, inputEncoding);
            }
            Map<String, Object> _vars = (Map<String, Object>) context;
            if (env)
                _vars.putAll(System.getenv());
            GroovyExpand ve = new GroovyExpand(_vars) {
                @Override
                protected Object get(String name) {
                    if (strict)
                        return super.get(name);
                    // if variable doesn't exist, then implied a null variable.
                    return vars.get(name);
                }
            };
            try {
                return ve.compileAndEvaluate(template);
            } catch (CompilationFailedException e) {
                String script = ve.getCompiledScript();
                System.err.println(CLINLS.getString("TemplateProcess.compile")); //$NON-NLS-1$
                System.err.println(script);
                throw e;
            } catch (Throwable e) {
                String script = ve.getCompiledScript();
                System.err.println(CLINLS.getString("TemplateProcess.evaluate")); //$NON-NLS-1$
                System.err.println(script);
                System.err.println(CLINLS.getString("TemplateProcess.vars")); //$NON-NLS-1$
                // TODO - object dumper
                for (Entry<String, Object> ent : _vars.entrySet())
                    System.err.println(ent.getKey() + " = " + ent.getValue()); //$NON-NLS-1$
                throw new EvalException(e);
            }
        }
    }

}
