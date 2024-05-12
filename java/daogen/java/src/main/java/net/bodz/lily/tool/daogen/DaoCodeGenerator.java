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
import net.bodz.bas.codegen.ClassPathInfo.Builder;
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
import net.bodz.bas.t.catalog.CatalogSubset;
import net.bodz.bas.t.catalog.ContainingType;
import net.bodz.bas.t.catalog.DefaultCatalogMetadata;
import net.bodz.bas.t.catalog.ICatalogVisitor;
import net.bodz.bas.t.catalog.IJDBCLoadSelector;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.IViewMetadata;
import net.bodz.bas.t.catalog.SchemaOid;
import net.bodz.bas.t.catalog.SelectMode;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.catalog.TableType;
import net.bodz.lily.tool.daogen.config.CatalogConfig;
import net.bodz.lily.tool.daogen.config.CatalogConfigApplier;
import net.bodz.lily.tool.daogen.config.FinishProcessor;
import net.bodz.lily.tool.daogen.config.LangFixupApplier;
import net.bodz.lily.tool.daogen.dir.Foo_Id__java;
import net.bodz.lily.tool.daogen.dir.Foo__java;
import net.bodz.lily.tool.daogen.dir.Foo__java_tv;
import net.bodz.lily.tool.daogen.dir.Foo_stuff__java;
import net.bodz.lily.tool.daogen.dir.dao.FooCriteriaBuilder__java;
import net.bodz.lily.tool.daogen.dir.dao.FooCriteriaBuilder_stuff__java;
import net.bodz.lily.tool.daogen.dir.dao.FooExporter__java;
import net.bodz.lily.tool.daogen.dir.dao.FooManager__java;
import net.bodz.lily.tool.daogen.dir.dao.FooMapper__java;
import net.bodz.lily.tool.daogen.dir.dao.FooMapper__java_tv;
import net.bodz.lily.tool.daogen.dir.dao.FooMapper__xml;
import net.bodz.lily.tool.daogen.dir.dao.VFooMapper__xml;
import net.bodz.lily.tool.daogen.dir.dao.test.FooManagerTest__java;
import net.bodz.lily.tool.daogen.dir.dao.test.FooMapperTest__java;
import net.bodz.lily.tool.daogen.dir.dao.test.FooMapperTest__java_v;
import net.bodz.lily.tool.daogen.dir.dao.test.FooSamples__java;
import net.bodz.lily.tool.daogen.dir.web.Foo0__ts;
import net.bodz.lily.tool.daogen.dir.web.Foo1__ts;
import net.bodz.lily.tool.daogen.dir.web.FooAdmin__vue;
import net.bodz.lily.tool.daogen.dir.web.FooChooseDialog__vue;
import net.bodz.lily.tool.daogen.dir.web.FooEditor__vue;
import net.bodz.lily.tool.daogen.dir.web.FooType0__ts;
import net.bodz.lily.tool.daogen.dir.web.FooType1__ts;
import net.bodz.lily.tool.daogen.dir.web.FooValidators0__ts;
import net.bodz.lily.tool.daogen.dir.web.FooValidators1__ts;
import net.bodz.lily.tool.daogen.dir.ws.FooIndex__java;
import net.bodz.lily.tool.daogen.util.MavenDirs;

/**
 * Generate DAO implementations in Java.
 */
@ProgramName("daogen")
public class DaoCodeGenerator
        extends BasicCLI
        implements
            IJDBCLoadSelector {

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

    Class<?> appClass = getClass();

    /**
     * Output directory. Use the maven base.dir by default.
     *
     * @option -O =PATH
     */
    File outDir;

    /**
     * Where to save header files include entity, mask, samples types.
     *
     * By default, search sibling -api or -model projects and put header files in src/main/java.
     * fallback to out-dir.
     *
     * @option -H =PATH
     */
    File headerDir;

    /**
     * Where to save -mapper files, exporters.
     *
     * By default, search sibling -impl or -dao projects and put class files in src/main/java.
     * fallback to out-dir.
     *
     * @option =PATH
     */
    File daoDir;

    /**
     * Where to save web-service related files include -Index.
     *
     * By default, search sibling -webapp, -web, -ws, -server projects and put class files in
     * src/main/java. fallback to out-dir.
     *
     * @option -W =PATH
     */
    File wsDir;

    /**
     * Where to save ES modules for types and components.
     *
     * @option -M =PATH
     */
    File webDir;

//    boolean checkHeaderDir;

    /**
     * Max parent level to search for the header dir.
     *
     * @option --max-api-depth =NUM
     */
    int maxParents = 1;

    /**
     * Show output paths only, no-op.
     *
     * @option
     */
    boolean showPath;

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
     * Use diff-patch.
     *
     * @option -d
     */
    boolean diffPatch;

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
        case MATERIALIZED_VIEW:
            if (table.getTableType() == TableType.MATERIALIZED_VIEW)
                logger.info("make materialized view " + table.getId());
            else
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
        long seed;
        if (seedRandom)
            seed = System.currentTimeMillis();
        else
            seed = seedMagic.hashCode();

        Builder builder = new ClassPathInfo.Builder()//
                .qName(table.getJavaType())//
                .maven(outDir);
        // ClassPathInfo outPath = builder.build();
        ClassPathInfo headerPath = builder.maven(headerDir).build();
        ClassPathInfo daoPath = builder.maven(daoDir).build();
        ClassPathInfo wsPath = builder.maven(wsDir).build();
        ClassPathInfo esmPath = builder.npm(webDir).build();

        DirConfig dirConfig = new DirConfig(headerPath, daoPath, wsPath, esmPath);

        JavaGenProject project = new JavaGenProject(outDir, dirConfig, seed);
        project.catalog = table.getCatalog();
        project.config = config;

        project.extraDDLs = extraDDLs;

        UpdateMethod defaultUpdateMethod;
        if (forceMode)
            defaultUpdateMethod = diffPatch ? UpdateMethod.DIFF_PATCH : UpdateMethod.OVERWRITE;
        else
            defaultUpdateMethod = diffPatch ? UpdateMethod.DIFF_PATCH : UpdateMethod.NO_UPDATE;
        project.setPreferredUpdateMethod(defaultUpdateMethod);

        return project;
    }

    public void makeTable(ITableMetadata table)
            throws IOException {
        JavaGenProject project = createProject(table);

        new Foo_stuff__java(project).buildFile(table, UpdateMethod.OVERWRITE);
        if (table.getPrimaryKeyColumns().length > 1)
            new Foo_Id__java(project).buildFile(table, UpdateMethod.OVERWRITE);

        new Foo__java(project).buildFile(table);
        new FooCriteriaBuilder_stuff__java(project).buildFile(table, UpdateMethod.OVERWRITE);
        new FooCriteriaBuilder__java(project).buildFile(table);
        new FooSamples__java(project).buildFile(table, UpdateMethod.OVERWRITE);

        new FooMapper__xml(project).buildFile(table);
        new FooMapper__java(project).buildFile(table);
        new FooMapperTest__java(project).buildFile(table);
        new FooManager__java(project).buildFile(table);
        new FooManagerTest__java(project).buildFile(table);

        if (extraDDLs)
            new FooExporter__java(project).buildFile(table, UpdateMethod.OVERWRITE);

        new FooIndex__java(project).buildFile(table);
        // new FooIndexTest__java(project).buildFile(table);

        new FooType0__ts(project).buildFile(table, UpdateMethod.OVERWRITE);
        new FooType1__ts(project).buildFile(table);
        new Foo0__ts(project).buildFile(table, UpdateMethod.OVERWRITE);
        new Foo1__ts(project).buildFile(table);
        new FooValidators0__ts(project).buildFile(table, UpdateMethod.OVERWRITE);
        new FooValidators1__ts(project).buildFile(table);
        new FooAdmin__vue(project).buildFile(table);
        new FooChooseDialog__vue(project).buildFile(table);
        new FooEditor__vue(project).buildFile(table);
    }

    public void makeView(IViewMetadata view)
            throws IOException {
        JavaGenProject project = createProject(view);

        new Foo_stuff__java(project).buildFile(view, UpdateMethod.OVERWRITE);
        if (view.getPrimaryKeyColumns().length > 1)
            new Foo_Id__java(project).buildFile(view, UpdateMethod.OVERWRITE);

        new Foo__java_tv(project).buildFile(view);
        new FooCriteriaBuilder_stuff__java(project).buildFile(view, UpdateMethod.OVERWRITE);
        new FooCriteriaBuilder__java(project).buildFile(view);
        new FooIndex__java(project).buildFile(view);
        new VFooMapper__xml(project).buildFile(view);
        new FooMapper__java_tv(project).buildFile(view);
        new FooMapperTest__java_v(project).buildFile(view);

        new FooType0__ts(project).buildFile(view, UpdateMethod.OVERWRITE);
        new FooType1__ts(project).buildFile(view);
        new Foo0__ts(project).buildFile(view, UpdateMethod.OVERWRITE);
        new Foo1__ts(project).buildFile(view);
        new FooValidators0__ts(project).buildFile(view, UpdateMethod.OVERWRITE);
        new FooValidators1__ts(project).buildFile(view);
        new FooChooseDialog__vue(project).buildFile(view);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {

        configDirs();
        if (this.showPath) {
            logger.info("header: " + this.headerDir);
            logger.info("dao: " + this.daoDir);
            logger.info("ws: " + this.wsDir);
            logger.info("web: " + this.webDir);
            return;
        }

        if (parentPackage == null)
            throw new IllegalArgumentException("parent-package isn't specified.");

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

        catalog.setJDBCLoadSelector(this);

        try {
            if (loadCatalogFile != null) {
                String extension = FilePath.getExtension(loadCatalogFile);
                if ("xml".equals(extension))
                    XmlFn.load(catalog, loadCatalogFile);
                else
                    JsonFn.load(catalog, loadCatalogFile, JsonFormOptions.DEFAULT);
            }

            connection = dataContext.getConnection();
            catalog.loadFromJDBC(connection, "TABLE", "VIEW", "MATERIALIZED VIEW");

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

    public void configDirs()
            throws IOException {
        this.startDir = SysProps.userWorkDir;
        if (this.chdir != null)
            this.startDir = new File(this.startDir, this.chdir).getCanonicalFile();

        if (this.outDir == null) {
            MavenPomDir pomDir = MavenDirs.findPomDir(this.appClass, this.startDir);
            this.outDir = pomDir.getBaseDir();
        }

        DirSearcher searcher = new DirSearcher(this.appClass, this.startDir, this.maxParents);

        if (this.headerDir == null)
            this.headerDir = searcher.findSiblingDir("header-dir", DirSearcher.MAVEN, 0, //
                    "-types", "-model", "model", "-api");
        if (this.daoDir == null)
            this.daoDir = searcher.findSiblingDir("dao-dir", DirSearcher.MAVEN, 0, //
                    "-dao", "-impl");
        if (this.wsDir == null)
            this.wsDir = searcher.findSiblingDir("ws-dir", DirSearcher.MAVEN, 0, //
                    "-ws", "-webapp", "-server", "server");
        if (this.webDir == null)
            this.webDir = searcher.findSiblingDir("web-dir", DirSearcher.NPM, 0, //
                    "-web", "html", "client");

        if (this.headerDir == null)
            this.headerDir = this.outDir;
        if (this.daoDir == null)
            this.daoDir = this.outDir;
        if (this.wsDir == null)
            this.wsDir = this.outDir;
        if (this.webDir == null)
            this.webDir = this.outDir;
    }

    /** ⇱ Implementation Of {@link IJDBCLoadSelector}. */
    /* _____________________________ */static section.iface __jdbc_selector__;

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

    public static void main(String[] args)
            throws Exception {

        DaoCodeGenerator app = new DaoCodeGenerator(//
                DataHub.getPreferredHub().getMain());
        app.execute(args);
    }

}
