package net.bodz.lapiota.javatools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import net.bodz.bas.c.java.io.FileFinder;
import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.io.resource.tools.StreamLoading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;

/**
 * Add missed entries for NLS property files
 */
@ProgramName("nlsadd")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class NLSAddMissings
        extends BasicCLI {

    /**
     * backup file extension, include the dot(.) if necessary
     *
     * @option -k =BAKEXT
     */
    String backupExtension;

    /**
     * force to overwrite existing files
     *
     * @option -f =value
     */
    boolean force;

    String encoding = "utf-8";
    boolean sort = true;

    boolean removeExtras = true;

    static class MasterPropertiesFilter
            implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (!file.isFile())
                return false;
            if (file.getName().contains("_"))
                return false;
            String ext = FilePath.getExtension(file);
            if (!"properties".equals(ext))
                return false;
            return true;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doFileArgument(IFile file)
            throws Exception {
        if (file.isTree()) {
            FileFinder finder = new FileFinder(new MasterPropertiesFilter(), file);
            for (File f : finder) {
                doFileArgument(f);
            }
            return;
        }

        final String base = FilePath.stripExtension(file.getName());
        final String ext = FilePath.getExtension(file.getName());
        if (!"properties".equals(ext)) {
            L.warn("Skipped non-properties file: ", file);
            return;
        }
        Properties master = file.tooling()._for(StreamLoading.class).loadProperties();
        Enumeration<String> _enum = (Enumeration<String>) master.propertyNames();
        Set<String> masterNames = Collections2.toSet(_enum);
        L.infof("Master file: %s (%d entries)\n", file, masterNames.size());

        IFile dir = file.getParentFile();
        if (dir == null)
            throw new NullPointerException("dir");
        File[] localeFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                String fbase = FilePath.stripExtension(filename);
                String fext = FilePath.getExtension(filename);
                if (!fext.equals(ext)) // extension must be same
                    return false;
                if (fbase.equals(base)) // self, ignore
                    return false;
                if (fbase.startsWith(base + "_"))
                    return true;
                return false;
            }
        });
        int addSum = 0;
        int removeSum = 0;
        for (File localeFile : localeFiles) {
            L.info("  File ", localeFile);
            Properties props = Files.loadProperties(localeFile, encoding);
            boolean dirty = false;
            int add = 0;
            int remove = 0;
            for (String name : masterNames)
                if (!props.containsKey(name)) {
                    L.info("    Add missing property ", name);
                    props.setProperty(name, master.getProperty(name));
                    dirty = true;
                    add++;
                }
            if (removeExtras) {
                _enum = (Enumeration<String>) props.propertyNames();
                Set<String> pnames = Collections2.toSet(_enum);
                for (String pname : pnames) {
                    if (!masterNames.contains(pname)) {
                        L.info("    Remove redundant property ", pname);
                        props.remove(pname);
                        dirty = true;
                        remove++;
                    }
                }
            }

            if (dirty) {
                L.infof("    +%d -%d /%d entries. \n", add, remove, props.size());
                if (backupExtension != null) {
                    File bakfile = new File(localeFile.getPath() + backupExtension);
                    if (bakfile.exists() && !force) {
                        L.warn("  Bak file existed: ", bakfile);
                        if (!UI.confirm("Overwrite " + bakfile + "? "))
                            continue;
                    }
                    L.info("    Backup to ", bakfile);
                    Files.copy(localeFile, bakfile);
                }
                L.info("    Save ", localeFile);
                // StringWriter buf = new StringWriter(props.size() * 100);
                ByteArrayOutputStream buf = new ByteArrayOutputStream(props.size() * 100);
                props.store(buf, null);
                // this buffer always in ISO-8859-1, in Sun's implementation.
                String contents = buf.toString();
                if (sort) {
                    String[] lines = contents.split("\n");
                    Arrays.sort(lines);
                    contents = StringArray.join("\n", lines);
                }
                FileOutputStream out = new FileOutputStream(localeFile);
                OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
                try {
                    writer.write(contents);
                } finally {
                    writer.close();
                }
            }
            addSum += add;
            removeSum += remove;
        }
        L.infof("  Total %d entries added, %d entries removed. \n\n", addSum, removeSum);
    }

    public static void main(String[] args)
            throws Throwable {
        new NLSAddMissings().execute(args);
    }

}
