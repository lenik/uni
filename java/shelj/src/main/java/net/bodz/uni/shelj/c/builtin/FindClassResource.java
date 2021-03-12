package net.bodz.uni.shelj.c.builtin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.io.FileURL;
import net.bodz.bas.c.java.net.URLClassLoaders;
import net.bodz.bas.c.loader.ClassLoaders;
import net.bodz.bas.err.control.Control;
import net.bodz.bas.fn.IFilter;
import net.bodz.bas.io.res.builtin.InputStreamSource;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.jvm.stack.Caller;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.util.find.FileFinder;

/**
 * Find the class file defined the specified class
 */
@ProgramName("jwhich")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FindClassResource
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(FindClassResource.class);

    /**
     * max depth of directories recurse into
     *
     * @option -r =[DEPTH] default-value=65536
     */
    protected int recursive = 1;

    /**
     * using custom file filter, default find jar/?ar/zip files
     *
     * @option -Ic =CLASS(FileFilter)
     */
    protected IFilter<IFile> filter;

    protected List<URL> classpaths = new ArrayList<URL>();

    /**
     * @option -b =FILE|DIR
     */
    protected void bootClasspath(IFile file)
            throws IOException {

        ClassLoader classLoader = ClassLoaders.getRuntimeClassLoader();

        FileFinder finder = new FileFinder(filter, recursive, file);
        for (IFile f : finder.listFiles()) {
            if (f.getAttributes().isDirectory())
                return;
            URL url = f.getPath().toURL();
            logger.debug("add boot-classpath: ", url);
            URLClassLoaders.addURLs1(classLoader, url);
        }
    }

    /**
     * @option -c =FILE|DIR
     */
    protected void classpath(IFile file)
            throws IOException {
        FileFinder finder = new FileFinder(filter, recursive, file);
        for (IFile f : finder.listFiles()) {
            if (f.getAttributes().isDirectory())
                return;
            URL url = f.getPath().toURL();
            logger.debug("queue classpath: ", url);
            classpaths.add(url);
        }
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
        file = FilePath.canoniOf(url).getPath();
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
     * find all libraries required by the specified class
     *
     * @option -t =MAINCLASS
     */
    protected String testClass;

    /**
     * add one argument to the test arguments
     *
     * @option -a =ARG
     */
    protected String[] testArguments;

    /**
     * suppress the output of test program
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
     * do test
     *
     * @option -T
     */
    protected void test()
            throws Exception {
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
                    Control.invoke(mainf, null, (Object) testArguments);
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
                    URL liburl = FileURL.toURL(lib);
                    if (tryAdds.contains(liburl)) {
                        logger.error("loop fail");
                        break;
                    }
                    tryAdds.add(liburl);

                    // Classpath.addURL(liburl);
                    URLClassLoader urlClassLoader = URLClassLoaders.findFirstURLClassLoader(craftLoader);
                    if (urlClassLoader == null)
                        throw new RuntimeException("No URLClassLoader found for " + craftLoader);

                    URLClassLoaders.addURLs(urlClassLoader, liburl);
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
    protected void mainImpl(String... args)
            throws Exception {
        ClassLoader runtimeLoader = ClassLoaders.getRuntimeClassLoader();

        if (testClass == null) {
            for (URL url : classpaths) {
                logger.debug("add classpath: ", url);
                URLClassLoaders.addURLs1(runtimeLoader, url);
            }

            ClassLoader callerLoader = Caller.getCallerClassLoader(0);
            Iterable<String> strings;
            if (args.length > 0)
                strings = Arrays.asList(args);
            else {
                logger.stdout("Enter class names or resource paths: ");
                strings = new InputStreamSource(System.in).to(StreamReading.class).lines();
            }
            for (String name : strings) {
                name = name.trim();
                URL url = findResource(callerLoader, name);
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
        JAR_EXTENSIONS = Pattern.compile("([a-z]ar|zip)", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected void reconfigure()
            throws Exception {
        if (filter == null)
            filter = new IFilter<IFile>() {
                @Override
                public boolean accept(IFile file) {
                    String ext = FilePath.getExtension(file.getName()).toLowerCase();
                    if (ext == null)
                        return false;
                    else
                        return JAR_EXTENSIONS.matcher(ext).matches();
                }
            };
    }

    public static void main(String[] args)
            throws Exception {
        new FindClassResource().execute(args);
    }

}
