package net.bodz.lily.tool.daogen;

import java.io.File;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.UpdateMethod;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.ICatalogMetadata;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.lily.tool.daogen.config.CatalogConfig;

public class JavaGenProject {

    static final Logger logger = LoggerFactory.getLogger(JavaGenProject.class);

    File baseDir;
    String daoPackage = "dao";
    String wsPackage = "ws";
    UpdateMethod updateMethod;

    ICatalogMetadata catalog;
    CatalogConfig config;
    long randomSeed;

    boolean parentColumnInParallelMode = true;
    boolean extraDDLs;

    public final ClassPathInfo Foo;
    public final ClassPathInfo _Foo_stuff;
    public final ClassPathInfo IFoo_Id;
    public final ClassPathInfo Foo_Id;
    public final ClassPathInfo Foo_IdAccessor;

    public final ClassPathInfo FooTest; // test
    public final ClassPathInfo _Foo_stuffTest; // test
    public final ClassPathInfo Foo_IdTest; // test
    public final ClassPathInfo Foo_IdAccessorTest; // test

    public final ClassPathInfo _FooMask_stuff;
    public final ClassPathInfo FooMask;
    public final ClassPathInfo FooMaskTest; // test

    public final ClassPathInfo FooSamples; // test

    public final ClassPathInfo FooMapper;
    public final ClassPathInfo FooMapperTest; // test
    public final ClassPathInfo FooManager;
    public final ClassPathInfo FooManagerTest; // test
    public final ClassPathInfo FooExporter;

    public final ClassPathInfo FooIndex;
    public final ClassPathInfo FooIndexTest; // test

    public JavaGenProject(File baseDir, DirConfig dirs, long randomSeed) {
        this.baseDir = baseDir;
        this.randomSeed = randomSeed;

        String dao_ = daoPackage + ".";
        String ws_ = wsPackage + ".";
        String generated = "src/main/generated";

        Foo = dirs.modelPath;
        _Foo_stuff = Foo.join("_" + Foo.name + "_stuff", generated);
        IFoo_Id = Foo.join("I" + Foo.name + "_Id");
        Foo_Id = Foo.join(Foo.name + "_Id");
        Foo_IdAccessor = Foo.join(Foo.name + ".Id");

        FooTest = dirs.modelTestPath.join(Foo.name + "Test");
        _Foo_stuffTest = FooTest.join("_" + Foo.name + "_stuffTest");
        Foo_IdTest = FooTest.join(Foo.name + "_IdTest");
        Foo_IdAccessorTest = FooTest.join(Foo.name + "_IdAccessorTest");

        _FooMask_stuff = Foo.join(dao_ + "_" + Foo.name + "Mask_stuff", generated);
        FooMask = Foo.join(dao_ + Foo.name + "Mask");
        FooMaskTest = FooTest.join(dao_ + Foo.name + "MaskTest");

        FooSamples = dirs.daoTestPath.join(Foo.name + "Samples", generated);

        FooMapper = dirs.daoPath.join(dao_ + Foo.name + "Mapper");
        FooMapperTest = dirs.daoTestPath.join(dao_ + Foo.name + "MapperTest");
        FooManager = dirs.daoPath.join(dao_ + Foo.name + "Manager");
        FooManagerTest = dirs.daoTestPath.join(dao_ + Foo.name + "ManagerTest");
        FooExporter = dirs.daoPath.join(Foo.name + "Exporter", generated);

        FooIndex = dirs.wsPath.join(ws_ + Foo.name + "Index");
        FooIndexTest = dirs.wsTestPath.join(ws_ + Foo.name + "IndexTest");
    }

    public UpdateMethod getPreferredUpdateMethod() {
        return updateMethod;
    }

    public void setPreferredUpdateMethod(UpdateMethod updateMethod) {
        if (updateMethod == null)
            throw new NullPointerException("updateMethod");
        this.updateMethod = updateMethod;
    }

    public ColumnName columnName(IColumnMetadata column) {
        return config.columnName(column);
    }

}
