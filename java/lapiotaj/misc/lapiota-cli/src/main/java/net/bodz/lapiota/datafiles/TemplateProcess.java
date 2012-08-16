package net.bodz.lapiota.datafiles;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.codehaus.groovy.control.CompilationFailedException;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.cli.plugin.AbstractCLIPlugin;
import net.bodz.bas.cli.plugin.CLIPlugin;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIException;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.lang.fn.EvalException;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;
import net.bodz.lapiota.util.GroovyExpand;

/**
 * General template processing
 */
@BootInfo(syslibs = "groovy")
@ProgramName("jsub")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class TemplateProcess
        extends BatchEditCLI {

    Charset inputEncoding;
    Charset outputEncoding;

    /**
     * Source parsing model
     *
     * @option -S =MODEL=PARAM...
     */
    SourceModel sourceModel;

    /**
     * Template processing model
     *
     * @option -M =MODEL=PARAM...
     */
    TemplateModel templateModel;

    /**
     * Add extension to file names
     *
     * @option -x =EXT
     */
    String extension;

    public TemplateProcess() {
        plugins.registerCategory(CLINLS.getString("TemplateProcess.sourceModel"), SourceModel.class);
        plugins.register("ini", VariableDefSource.class, this);
        plugins.register("csv", CSVDefSource.class, this);
        plugins.registerCategory(CLINLS.getString("TemplateProcess.templateModel"), TemplateModel.class);
        plugins.register("ve", VariableExpandTemplate.class, this);
        plugins.register("gsp", GroovyTemplate.class, this);
    }

    @Override
    protected void _boot()
            throws Exception {
        inputEncoding = parameters().getInputEncoding();
        outputEncoding = parameters().getOutputEncoding();
    }

    @Override
    protected EditResult doEdit(IFile file, IFile editTmp)
            throws Exception {
        sourceModel.reset(file);
        while (sourceModel.next()) {
            Object context = sourceModel.getContext();
            String destFile = sourceModel.getDestFile();
            if (extension != null)
                destFile += extension;

            L.mesg("file ", destFile);
            String contents = templateModel.expand(context);

            IFile defaultStart = file.getParentFile();

            File dst = getOutputFile(destFile, defaultStart);
            Files.write(editTmp, contents, outputEncoding);

            EditResult result = EditResult.compareAndSave();
            addResult(dst, dst, editTmp, result);
        }
        return null;
    }

    public static void main(String[] args)
            throws Exception {
        new TemplateProcess().execute(args);
    }

    // Plugin Interfaces

    static interface SourceModel
            extends CLIPlugin {
        void reset(File sourceFile)
                throws Exception;

        boolean next()
                throws Exception;

        String getDestFile();

        Object getContext();
    }

    abstract static class _SourceModel
            extends AbstractCLIPlugin
            implements SourceModel {
    }

    static interface TemplateModel
            extends CLIPlugin {
        String expand(Object context)
                throws Exception;
    }

    abstract static class _TemplateModel
            extends AbstractCLIPlugin
            implements TemplateModel {
    }

    // Plugin Implementations

    /**
     * INI style [file] sections
     */
    class VariableDefSource
            extends _SourceModel {

        /**
         * Comment char
         *
         * @option
         */
        String commentChar = ";";

        /**
         * Enable using `file=key' line as separator
         *
         * @option
         */
        String fileKey = null;

        BufferedReader lineIn;
        String lastFile;
        String file;
        Map<String, String> variables;

        public VariableDefSource() {
        }

        @Override
        public void reset(File sourceFile)
                throws IOException {
            lineIn = Files.getBufferedReader(sourceFile);
            // file = Files.getName(sourceFile); // out=file + tmplExt
            if (variables == null)
                variables = new HashMap<String, String>();
            else
                variables.clear();
        }

        @Override
        public boolean next()
                throws Exception {
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

    /**
     * CSV source (the first row defines field names)
     */
    class CSVDefSource
            extends _SourceModel {

        /**
         * comment char
         *
         * @option
         */
        String commentChar = "#";

        /**
         * field delimiter characters, default ','
         *
         * @option
         */
        String delim = ",";

        /**
         * max number of fields, see String#split(String, int)
         *
         * @option
         */
        int limit = 0;

        /**
         * the field names in CSV file when specified
         *
         * @option
         */
        String[] names;

        /**
         * using this field as file name, default `file'
         *
         * @option
         */
        String fileField = "file";

        /**
         * trim field values, default true
         *
         * @option
         */
        boolean trim = true;

        BufferedReader lineIn;
        Map<String, String> variables;

        public CSVDefSource() {
        }

        @Override
        public void reset(File sourceFile)
                throws Exception {
            lineIn = Files.getBufferedReader(sourceFile);
            if (variables == null)
                variables = new HashMap<String, String>();
            else
                variables.clear();
        }

        @Override
        public void setParameters(Map<String, Object> parameters)
                throws CLIException, ParseException {
            super.setParameters(parameters);
        }

        @Override
        public boolean next()
                throws Exception {
            String _delim = Pattern.quote(delim);
            String line;
            while ((line = lineIn.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(commentChar))
                    continue;
                int limit = this.limit;
                if (names != null && names.length < limit)
                    limit = names.length;

                String[] parts = StringArray.split(line, _delim.toCharArray(), limit);
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

    /**
     * =template-file Simple $variable expand
     */
    class VariableExpandTemplate
            extends _TemplateModel {

        /**
         * expand environment variable
         */
        boolean env;

        // @Option(alias = "t", vnam = "FILE", doc =
        // "template filename, used by specific template models")
        protected File templateFile;
        protected String template;

        public VariableExpandTemplate() {
        }

        public VariableExpandTemplate(File templateFile) {
            this.templateFile = templateFile;
        }

        @SuppressWarnings("unchecked")
        @Override
        public String expand(Object context)
                throws Exception {
            if (template == null) {
                if (templateFile == null)
                    throw new CLIException(CLINLS.getString("TemplateProcess.templateIsntSpecified"));
                template = Files.readAll(templateFile, inputEncoding);
            }
            Map<String, Object> vars = (Map<String, Object>) context;
            if (env)
                vars.putAll(System.getenv());
            return Interps.dereference(template, vars);
        }
    }

    /**
     * =template.gsp Simple GSP (groovy server pages) expand
     */
    class GroovyTemplate
            extends _TemplateModel {

        /**
         * expand environment variable
         *
         * @option
         */
        boolean env;

        /**
         * variables must be defined
         *
         * @option
         */
        boolean strict;

        // @Option(alias = "t", vnam = "FILE", doc =
        // "template filename, used by specific template models")
        protected File templateFile;
        protected String template;

        public GroovyTemplate() {
        }

        public GroovyTemplate(File templateFile) {
            this.templateFile = templateFile;
        }

        @SuppressWarnings("unchecked")
        @Override
        public String expand(Object context)
                throws Exception {
            if (template == null) {
                if (templateFile == null)
                    throw new CLIException(CLINLS.getString("TemplateProcess.templateIsntSpecified"));
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
                System.err.println(CLINLS.getString("TemplateProcess.compile"));
                System.err.println(script);
                throw e;
            } catch (Throwable e) {
                String script = ve.getCompiledScript();
                System.err.println(CLINLS.getString("TemplateProcess.evaluate"));
                System.err.println(script);
                System.err.println(CLINLS.getString("TemplateProcess.vars"));
                // TODO - object dumper
                for (Entry<String, Object> ent : _vars.entrySet())
                    System.err.println(ent.getKey() + " = " + ent.getValue());
                throw new EvalException(e);
            }
        }
    }

}
