package net.bodz.lily.tool.daogen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.c.string.StringPart;
import net.bodz.bas.c.system.SysProps;
import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.UpdateMethod;
import net.bodz.bas.db.ctx.DataContext;
import net.bodz.bas.db.ctx.DataHub;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.api.ElementHandlerException;
import net.bodz.bas.fmt.json.JsonFn;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.fmt.rst.RstFn;
import net.bodz.bas.fmt.xml.XmlFn;
import net.bodz.bas.io.res.AbstractStreamResource;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.catalog.*;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.tool.daogen.config.CatalogConfig;
import net.bodz.lily.tool.daogen.config.CatalogConfigApplier;
import net.bodz.lily.tool.daogen.config.FinishProcessor;
import net.bodz.lily.tool.daogen.config.LangFixupApplier;

/**
 * Generate DAO implementations in Java.
 */
@ProgramName("daogen")
public class DaoCodeGenerator
        extends BasicCLI {

    static Logger logger = LoggerFactory.getLogger(DaoCodeGenerator.class);

    /**
     * Change to this directory.
     *
     * The utility search for the closest maven project start from the workdir.
     *
     * @option -C
     */
    String chdir;
    File startDir;

    /**
     * Parent package name of generated java models.
     *
     * @option -p =QNAME
     */
    String parentPackage;

    /**
     * Output directory. Use the maven base.dir by default.
     *
     * @option -O =PATH
     */
    File outDir;

    /**
     * API/Headers output directory. By default, search sibling -api or -model projects and put
     * header files in src/main/java. If can't find such project, use the same value specified with
     * <code>--out-dir</code>.
     *
     * @option -H =PATH
     */
    File headerDir;

    /**
     * Max parent level to search for the header dir.
     *
     * @option --max-api-depth =NUM
     */
    int maxParents = 2;

    /**
     * Generate models for views.
     *
     * @option -V
     */
    Boolean includeViews;

    /**
     * Generate models for tables.
     *
     * @option -T
     */
    Boolean includeTables;

    /**
     * Generate extra DDLs.
     *
     * @option -D --extra-ddls
     */
    boolean extraDDLs = false;

    /**
     * Compute a digest as the random seed from the given string. "magic" by default.
     *
     * @option -S
     */
    String seedMagic = "magic";

    /**
     * Use a random seed value. Typically a value from timestamp.
     *
     * @option -R
     */
    boolean seedRandom;

    /**
     * Use diff-merge.
     *
     * @option -d
     */
    boolean diffMerge;

    /**
     * Overwrite all existing files.
     *
     * @option -f
     */
    boolean forceMode;

    CatalogConfig config = new CatalogConfig();

    // The catalog data file can avoid to access the RDBMS, and speed up the loading,

    /**
     * Save the catalog metadata to file, and quit.
     *
     * @option --save-catalog =FILE
     */
    File saveCatalogFile;

    /**
     * Load the catalog metadata from file and merge with database.
     *
     * @option --load-catalog =FILE
     */
    File loadCatalogFile;

    /**
     * Save the effective config to file, for diagnostic.
     *
     * @option --save-config =FILE
     */
    File saveConfigFile;

    DataContext dataContext;
    Connection connection;

    CatalogSubset catalogSubset = new CatalogSubset(null);
    DefaultCatalogMetadata catalog = new DefaultCatalogMetadata();
    boolean loadDependedObjects = true;

    public DaoCodeGenerator(DataContext dataContext) {
        if (dataContext == null)
            throw new NullPointerException("dataContext");
        this.dataContext = dataContext;
    }

    /**
     * Use specified codegen config.
     *
     * @option -c =FILE
     */
    public void config(File configFile)
            throws ElementHandlerException, IOException, ParseException {
        RstFn.loadFromRst(config, configFile);
    }

    boolean processTableOrView(ITableMetadata table) {
        switch (table.getTableType()) {
        case TABLE:
        case SYSTEM_TABLE:
        case TEMP:
        case GLOBAL_TEMP:
            logger.info("make table " + table.getId());
            try {
                makeTable(table);
            } catch (Exception e) {
                logger.error("Error make table: " + e.getMessage(), e);
                return false;
            } finally {
            }
            return true;

        case VIEW:
            logger.info("make view " + table.getId());
            try {
                makeView((IViewMetadata) table);
            } catch (Exception e) {
                logger.error("Error make table: " + e.getMessage(), e);
                return false;
            } finally {
            }
            return true;

        default:
            return false;
        }
    }

    JavaGenProject createProject(ITableMetadata table) {
        String simpleName = table.getJavaName();
        String packageName = table.getJavaPackage();
        if (simpleName == null)
            throw new NullPointerException("simpleName");
        if (packageName == null)
            throw new NullPointerException("packageName");

        long seed;
        if (seedRandom)
            seed = System.currentTimeMillis();
        else
            seed = seedMagic.hashCode();

        ClassPathInfo modelPath = new ClassPathInfo(packageName, simpleName, //
                outDir, "src/main/java", "src/main/resources");
        ClassPathInfo modelApiPath = new ClassPathInfo(packageName, simpleName, //
                headerDir, "src/main/java", "src/main/resources");

        JavaGenProject project = new JavaGenProject(outDir, modelPath, modelApiPath, seed);
        project.catalog = table.getCatalog();
        project.config = config;

        project.extraDDLs = extraDDLs;

        UpdateMethod updateMethod;
        if (forceMode)
            updateMethod = diffMerge ? UpdateMethod.DIFF_MERGE : UpdateMethod.OVERWRITE;
        else
            updateMethod = diffMerge ? UpdateMethod.DIFF_MERGE : UpdateMethod.NO_UPDATE;
        project.setPreferredUpdateMethod(updateMethod);

        return project;
    }

    public void makeTable(ITableMetadata table)
            throws IOException {
        JavaGenProject project = createProject(table);

        new Foo_stuff__java(project).buildFile(table, UpdateMethod.OVERWRITE);
        if (table.getPrimaryKeyColumns().length > 1)
            new Foo_Id__java(project).buildFile(table, UpdateMethod.OVERWRITE);

        new Foo__java(project).buildFile(table);
        new FooMask_stuff__java(project).buildFile(table, UpdateMethod.OVERWRITE);
        new FooMask__java(project).buildFile(table);
        new FooIndex__java(project).buildFile(table);
        new FooMapper__xml(project).buildFile(table);
        new FooMapper__java(project).buildFile(table);
        new FooMapperTest__java(project).buildFile(table);
        new FooSamples__java(project).buildFile(table);
        if (extraDDLs)
            new FooExporter__java(project).buildFile(table);
    }

    public void makeView(IViewMetadata view)
            throws IOException {
        JavaGenProject project = createProject(view);

        new Foo_stuff__java(project).buildFile(view, UpdateMethod.OVERWRITE);
        if (view.getPrimaryKeyColumns().length > 1)
            new Foo_Id__java(project).buildFile(view, UpdateMethod.OVERWRITE);

        new Foo__java_tv(project).buildFile(view);
        new FooMask_stuff__java(project).buildFile(view, UpdateMethod.OVERWRITE);
        new FooMask__java(project).buildFile(view);
        new FooIndex__java(project).buildFile(view);
        new VFooMapper__xml(project).buildFile(view);
        new FooMapper__java_tv(project).buildFile(view);
        new FooMapperTest__java_v(project).buildFile(view);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (parentPackage == null)
            throw new IllegalArgumentException("parent-package isn't specified.");

        startDir = SysProps.userWorkDir;
        if (chdir != null)
            startDir = new File(startDir, chdir).getCanonicalFile();

        if (outDir == null) {
            MavenPomDir pomDir = findPomDir(startDir);
            outDir = pomDir.getBaseDir();
        }

        if (headerDir == null) {
            MavenPomDir pomDir = findPomDir(startDir);
            if (pomDir != null) {
                String moduleName = pomDir.getName();
                Split projectPart = Split.pop(moduleName, '-');
                MavenPomDir apiPomDir = findPomDir(pomDir.getBaseDir(), 0, maxParents, //
                        "model", //
                        projectPart.a + "-api", //
                        projectPart.a + "-model", //
                        null);
                if (apiPomDir != null)
                    headerDir = apiPomDir.getBaseDir();
            }
            if (headerDir == null)
                headerDir = outDir;
        }

        if (includeTables == null && includeViews == null)
            includeTables = includeViews = true;

        if (saveConfigFile != null) {
            RstFn.saveToRst(config, saveConfigFile);
        }

        for (String arg : args) {
            if (arg.startsWith("@")) {
                String path = arg.substring(1);
                AbstractStreamResource resource;

                if (new File(path).exists()) {
                    resource = new FileResource(new File(path));
                } else {
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    URL classRes = classLoader.getResource(path);
                    if (classRes == null)
                        throw new IllegalArgumentException("Bad resource path: " + path);
                    resource = new URLResource(classRes);
                }

                for (String line : resource.read().lines()) {
                    String name = line.trim();
                    if (name.isEmpty() || name.startsWith("#"))
                        continue;
                    TableOid oid = TableOid.parse(name);
                    catalogSubset.addTable(oid);
                }

            } else if (arg.endsWith(".*.*")) {
                catalogSubset.addAllSchemas();
            } else if (arg.endsWith(".*")) {
                String schemaName = StringPart.before(arg, ".*");
                // schemaName = schemaName.toLowerCase();
                catalogSubset.addFullSchema(schemaName);
            } else {
                TableOid oid = TableOid.parse(arg);
                catalogSubset.addTable(oid);
            }
        }

        catalog.setJDBCLoadSelector(new IJDBCLoadSelector() {
            @Override
            public SelectMode selectSchema(SchemaOid id) {
                ContainingType type = catalogSubset.contains(id.getSchemaName());
                if (type != ContainingType.NONE)
                    return SelectMode.INCLUDE;
                else
                    return SelectMode.EXCLUDE;
            }

            @Override
            public SelectMode selectTable(TableOid oid, TableType type) {
                if (type.isTable())
                    if (includeTables != Boolean.TRUE)
                        return SelectMode.SKIP;
                if (type.isView())
                    if (includeViews != Boolean.TRUE)
                        return SelectMode.SKIP;

                if (catalogSubset.contains(oid))
                    return SelectMode.INCLUDE;

                return loadDependedObjects ? SelectMode.EXCLUDE : SelectMode.SKIP;
            }

        });

        try {
            if (loadCatalogFile != null) {
                String extension = FilePath.getExtension(loadCatalogFile);
                if ("xml".equals(extension))
                    XmlFn.load(catalog, loadCatalogFile);
                else
                    JsonFn.load(catalog, loadCatalogFile, JsonFormOptions.DEFAULT);
            }

            connection = dataContext.getConnection();
            catalog.loadFromJDBC(connection, "TABLE", "VIEW");

            config.defaultPackageName = parentPackage;
            catalog.accept(new CatalogConfigApplier(config));
            catalog.accept(new LangFixupApplier(config));
            catalog.accept(new FinishProcessor(config));

            if (saveCatalogFile != null) {
                String extension = FilePath.getExtension(saveCatalogFile);
                if ("xml".equals(extension))
                    XmlFn.save(catalog, saveCatalogFile);
                else
                    JsonFn.save(catalog, saveCatalogFile, JsonFormOptions.PRETTY);
                return;
            }

            catalog.accept(new ICatalogVisitor() {
                @Override
                public boolean beginTableOrView(ITableMetadata table) {
                    if (table.isExcluded())
                        return false;
                    return processTableOrView(table);
                }
            });
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    Class<?> appClass = getClass();

    MavenPomDir findPomDir(File startDir, int maxDepth, int maxParents, String... moduleNames) {
        logger.debugf("find pomdirs in %s [depth=%d, parents=%d]", startDir, maxDepth, maxParents);

        for (String moduleName : moduleNames) {
            if (moduleName == null)
                continue;
            File moduleDir = new File(startDir, moduleName);
            if (MavenPomDir.isPomDir(moduleDir)) {
                logger.debug("matched pom dir: " + moduleDir);
                return new MavenPomDir(moduleDir);
            }
        }

        if (maxDepth > 0) {
            for (File subDir : startDir.listFiles((File f) -> f.isDirectory())) {
                MavenPomDir child = findPomDir(subDir, maxDepth - 1, 0, moduleNames);
                if (child != null)
                    return child;
            }
        }

        if (maxParents <= 0)
            return null;
        File parent = startDir.getParentFile();
        if (parent == null)
            return null;

        return findPomDir(parent, maxDepth + 1, maxParents - 1, moduleNames);
    }

    MavenPomDir findPomDir(File startDir) {
        MavenPomDir pomDir = MavenPomDir.closest(startDir);
        if (pomDir == null) {
            if (appClass == DaoCodeGenerator.class)
                throw new RuntimeException("Can't locate the maven project from " + startDir);

            pomDir = MavenPomDir.fromClass(appClass);
            if (pomDir == null)
                throw new RuntimeException("Can't locate the maven project from " + appClass);
        }
        return pomDir;
    }

    public static void main(String[] args)
            throws Exception {

        DaoCodeGenerator app = new DaoCodeGenerator(//
                DataHub.getPreferredHub().getMain());
        app.execute(args);
    }

}
