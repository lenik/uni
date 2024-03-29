package net.bodz.uni.shelj.c.devel;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.fn.IFilter;
import net.bodz.bas.io.res.tools.StreamLoading;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.IFilenameFilter;
import net.bodz.bas.vfs.VFS;
import net.bodz.bas.vfs.facade.DefaultVfsFacade;
import net.bodz.bas.vfs.facade.IVfsFacade;
import net.bodz.bas.vfs.util.find.FileFinder;

/**
 * Add missed entries for NLS property files
 */
@MainVersion({ 0, 1 })
@ProgramName("nlsadd")
@RcsKeywords(id = "$Id$")
public class NLSAddMissings
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(NLSAddMissings.class);

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

    IVfsFacade vfs = DefaultVfsFacade.getInstance();

    static class MasterPropertiesFilter
            implements IFilter<IFile> {

        @Override
        public boolean accept(IFile file) {
            if (!file.getAttributes().isRegularFile())
                return false;
            if (file.getName().contains("_"))
                return false;
            String ext = FilePath.getExtension(file.getName());
            if (!"properties".equals(ext))
                return false;
            return true;
        }

    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (String arg : expandWildcards(args)) {
            IFile file = VFS.resolve(arg);
            processFile(file);
        }
    }

    protected void processFile(IFile file)
            throws Exception {
        if (file.getAttributes().isDirectory()) {
            FileFinder finder = new FileFinder(new MasterPropertiesFilter(), file);
            for (IFile f : finder) {
                processFile(f);
            }
            return;
        }

        final String base = FilePath.stripExtension(file.getName());
        final String ext = FilePath.getExtension(file.getName());
        if (!"properties".equals(ext)) {
            logger.warn("Skipped non-properties file: ", file);
            return;
        }

        Properties masterProps = file.to(StreamLoading.class).loadProperties();
        Set<String> masterNames = new HashSet<>();
        Enumeration<?> _enum = masterProps.propertyNames();
        while (_enum.hasMoreElements())
            masterNames.add((String) _enum.nextElement());
        logger.infof("Master file: %s (%d entries)\n", file, masterNames.size());

        IFile dir = file.getParentFile();
        if (dir == null)
            throw new NullPointerException("dir");
        Iterable<? extends IFile> localeFiles = dir.children(new IFilenameFilter() {
            @Override
            public boolean accept(IFile dir, String filename) {
                String fbase = FilePath.stripExtension(filename);
                String fext = FilePath.getExtension(filename);
                if (!Nullables.equals(fext, ext)) // extension must be same
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
        for (IFile localeFile : localeFiles) {
            logger.info("  File ", localeFile);

            localeFile.setPreferredCharset(encoding);
            Properties props = localeFile.to(StreamLoading.class).loadProperties();

            boolean dirty = false;
            int add = 0;
            int remove = 0;
            for (String name : masterNames)
                if (!props.containsKey(name)) {
                    logger.info("    Add missing property ", name);
                    props.setProperty(name, masterProps.getProperty(name));
                    dirty = true;
                    add++;
                }
            if (removeExtras) {
                Enumeration<?> names = props.propertyNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    if (!masterNames.contains(name)) {
                        logger.info("    Remove redundant property ", name);
                        props.remove(name);
                        dirty = true;
                        remove++;
                    }
                }
            }

            if (dirty) {
                logger.infof("    +%d -%d /%d entries. \n", add, remove, props.size());

                if (backupExtension != null) {
                    IFile bakfile = localeFile.resolve(localeFile.getName() + backupExtension);
                    if (bakfile.isExisted() && !force) {
                        logger.warn("  Bak file existed: ", bakfile);
                        if (!dialogs.confirm("Overwrite " + bakfile + "? "))
                            continue;
                    }
                    logger.info("    Backup to ", bakfile);
                    vfs.copy(localeFile, bakfile);
                }

                logger.info("    Save ", localeFile);
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

                localeFile.to(StreamWriting.class).writeString(contents);
            }

            addSum += add;
            removeSum += remove;
        }
        logger.infof("  Total %d entries added, %d entries removed. \n\n", addSum, removeSum);
    }

    public static void main(String[] args)
            throws Throwable {
        new NLSAddMissings().execute(args);
    }

}
