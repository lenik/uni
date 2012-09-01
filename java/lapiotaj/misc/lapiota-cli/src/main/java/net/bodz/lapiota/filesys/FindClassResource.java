package net.bodz.lapiota.filesys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.io.resource.builtin.InputStreamSource;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.jvm.stack.Caller;
import net.bodz.bas.loader.Classpath;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;

/**
 * Find the class file defined the specified class
 */
@MainVersion({ 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("jwhich")
public class FindClassResource
        extends BasicCLI {

    /**
     * Max depth of directories recurse into
     *
     * @option -r =[DEPTH] default=65536
     */
    protected int recursive = 1;

    /**
     * Using custom file filter, default find jar/?ar/zip files
     *
     * @option -lc =CLASS(FileFilter)
     */
    protected FileFilter filter;

    protected List<URL> classpaths = new ArrayList<URL>();

    /**
     * @option -b =FILE|DIR
     */
    protected void bootClasspath(File file)
            throws IOException {
        FsWalk walker = new FsWalk(file, filter, recursive) {
            @Override
            public void process(File file)
                    throws IOException {
                if (file.isDirectory())
                    return;
                URL url = file.toURI().toURL();
                logger.debug("add boot-classpath: ", url);
                Classpath.addURL(url);
            }
        };
        walker.walk();
    }

    /**
     * @option -c =FILE|DIR %want=FileParser2.class
     */
    protected void classpath(File file)
            throws IOException {
        FsWalk walker = new FsWalk(file, filter, recursive) {
            @Override
            public void process(IFile file)
                    throws IOException {
                if (file.isTree())
                    return;
                URL url = file.getPath().toURL();
                logger.debug("queue classpath: ", url);
                classpaths.add(url);
            }
        };
        walker.walk();
    }

    static String libpath(URL url)
            throws IOException {
        String file = url.toString();
        if ("jar".equals(url.getProtocol())) {
            int pos = file.lastIndexOf("!");
            assert pos > 0;
            file = file.substring(4, pos);
        }
        url = new URL(file);
        try {
            file = new File(url.toURI()).getCanonicalPath();
        } catch (URISyntaxException e) {
            throw new Error(e.getMessage(), e);
        }
        return file;
    }

    static URL findResource(ClassLoader loader, String name) {
        String binpath = name.replace('.', '/') + ".class";
        URL url = loader.getResource(binpath);
        if (url == null)
            url = loader.getResource(name);
        return url;
    }

    /**
     * Find all libraries required by the specified class
     *
     * @option -t=MAINCLASS
     */
    protected String testClass;

    /**
     * Add one argument to the test arguments
     *
     * @option -a=ARG
     */
    protected String[] testArguments;

    /**
     * Suppress the output of test program
     *
     * @option -Q
     */
    protected boolean testQuiet = false;

    protected URL tryFind(String name) {
        URL[] urls = classpaths.toArray(new URL[0]);
        URLClassLoader tryLoader = new URLClassLoader(urls);
        return findResource(tryLoader, name);
    }

    /**
     * Do test
     *
     * @option -T
     */
    protected void test()
            throws IOException, ReflectiveOperationException {
        if (testArguments == null)
            testArguments = new String[0];

        List<URL> tryAdds = new ArrayList<URL>();
        URL tryAdd = null;

        for (int itry = 1; itry <= 1000; itry++) {
            PrintStream _out = System.out;
            PrintStream _err = System.err;
            if (testQuiet) {
                OutputStream sout = new ByteArrayOutputStream();
                OutputStream serr = new ByteArrayOutputStream();
                System.setOut(new PrintStream(sout));
                System.setErr(new PrintStream(serr));
            }

            // URL[] urls = tryAdds.toArray(new URL[0]);
            // ClassLoader craftLoader = new URLClassLoader(urls);
            ClassLoader craftLoader = ClassLoader.getSystemClassLoader();
            // Thread thread = Thread.currentThread();
            // ClassLoader callerLoader = Caller.getCallerClassLoader();
            // thread.setContextClassLoader(craftLoader);
            // Class<?> loadClass = craftLoader.loadClass(className);

            Class<?> clazz = Class.forName(testClass);
            Method mainf = clazz.getMethod("main", String[].class);
            try {
                try {
                    logger.info("execute ", mainf.getDeclaringClass(), "::", mainf.getName(), "/", testArguments.length);
                    mainf.invoke(null, (Object) testArguments);
                } finally {
                    if (testQuiet) {
                        System.setOut(_out);
                        System.setErr(_err);
                    }
                }
            } catch (NoClassDefFoundError e) {
                String respath = e.getMessage();
                String failClass = respath.replace('/', '.');
                logger.warn("try(", itry, ") ", failClass);
                tryAdd = tryFind(failClass);
                if (tryAdd != null) {
                    String lib = libpath(tryAdd);
                    logger.info("add required ", lib);
                    URL liburl = new File(lib).toURI().toURL();
                    if (tryAdds.contains(liburl)) {
                        logger.error("loop fail");
                        break;
                    }
                    tryAdds.add(liburl);
                    // Classpath.addURL(liburl);
                    Classpath.addURL(craftLoader, liburl);
                    continue;
                } else {
                    logger.error("failed to find: ", failClass);
                    throw e;
                }
            }
            break;
        }
        logger.mesg("test succeeded, the required libraries: ");
        for (URL url : tryAdds)
            logger.mesg(libpath(url));

        testClass = null;
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        if (testClass == null) {
            for (URL url : classpaths) {
                logger.debug("add classpath: ", url);
                Classpath.addURL(url);
            }

            ClassLoader loader = Caller.getCallerClassLoader(1);
            Iterable<String> iter;
            if (args.length > 0)
                iter = Arrays.asList(args);
            else {
                logger.stdout("Enter class names or resource paths: ");
                iter = new InputStreamSource(System.in).tooling()._for(StreamReading.class).listLines();
            }
            for (String name : iter) {
                name = name.trim();
                URL url = findResource(loader, name);
                if (url == null)
                    logger.mesg("No-Class: ", name);
                else
                    logger.mesg("Found: ", libpath(url));
            }
        } else {
            if (args.length > 0) {
                if (testArguments == null)
                    testArguments = args;
                else {
                    String[] concat = new String[testArguments.length + args.length];
                    System.arraycopy(testArguments, 0, concat, 0, testArguments.length);
                    System.arraycopy(args, 0, concat, testArguments.length, args.length);
                    testArguments = concat;
                }
            }
            test();
        }
    }

    private static Pattern JAR_EXTENSIONS;
    static {
        JAR_EXTENSIONS = Pattern.compile("(.ar|zip)", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected void _boot() {
        if (filter == null)
            filter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String ext = FilePath.getExtension(file).toLowerCase();
                    return JAR_EXTENSIONS.matcher(ext).matches();
                }
            };
    }

    public static void main(String[] args)
            throws Throwable {
        new FindClassResource().execute(args);
    }

}
