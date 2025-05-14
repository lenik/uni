package net.bodz.lily.tool.daogen;

import java.nio.file.Path;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.UpdateMethod;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.ICatalogMetadata;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.lily.tool.daogen.config.CatalogConfig;

public class DaoGenProject {

    static final Logger logger = LoggerFactory.getLogger(DaoGenProject.class);

    Path baseDir;
    String daoPackage = "dao";
    String wsPackage = "ws";
    UpdateMethod defaultUpdateMethod = UpdateMethod.DIFF_PATCH_UPGRADE;

    public ICatalogMetadata catalog;
    public CatalogConfig config;
    public long randomSeed;

    boolean parentColumnInParallelMode = true;
    public boolean extraDDLs;

    public final String typeInfoSuffix = "TypeInfo";
    public final String validatorsSuffix = "Validators";
    public final String criteriaBuilderSuffix = "CriteriaBuilder";

    public final ClassPathInfo web;

    public final ClassPathInfo Foo;
    public final ClassPathInfo _Foo_stuff;
    public final ClassPathInfo IFoo_Id;
    public final ClassPathInfo Foo_Id;
    //    public final ClassPathInfo Foo_IdAccessor;
    public final ClassPathInfo Foo_IdTypeInfo;

    public final ClassPathInfo FooTest; // test
    public final ClassPathInfo _Foo_stuffTest; // test
    public final ClassPathInfo Foo_IdTest; // test
    public final ClassPathInfo Foo_IdAccessorTest; // test

    public final ClassPathInfo _FooCriteriaBuilder_stuff;
    public final ClassPathInfo FooCriteriaBuilder;
    public final ClassPathInfo FooCriteriaBuilderTest; // test

    public final ClassPathInfo FooSamples; // test

    public final ClassPathInfo FooMapper;
    public final ClassPathInfo FooMapperTest; // test
    public final ClassPathInfo FooManager;
    public final ClassPathInfo FooManagerTest; // test
    public final ClassPathInfo FooExporter;

    public final ClassPathInfo FooIndex;
    public final ClassPathInfo FooIndexTest; // test

    public final ClassPathInfo Esm_Foo_stuff;
    public final ClassPathInfo Esm_Foo;
    public final ClassPathInfo Esm_Foo_stuff_Type;
    public final ClassPathInfo Esm_FooType;
    public final ClassPathInfo Esm_Foo_stuff_Validators;
    public final ClassPathInfo Esm_FooValidators;

    public final ClassPathInfo Esm_FooAdmin;
    public final ClassPathInfo Esm_FooChooseDialog;
    public final ClassPathInfo Esm_FooEditor;

    public DaoGenProject(Path baseDir, DirConfig dirs, long randomSeed) {
        this.baseDir = baseDir;
        this.randomSeed = randomSeed;

        String dao_ = daoPackage + ".";
        String ws_ = wsPackage + ".";
        String generated = "src/main/generated";

        Foo = dirs.modelPath;
        _Foo_stuff = Foo.child("_" + Foo.name + "_stuff", generated);
        IFoo_Id = Foo.child("I" + Foo.name + "_Id");
        Foo_Id = Foo.child(Foo.name + "_Id");
//        Foo_IdAccessor = Foo.child(Foo.name + ".Id");
        Foo_IdTypeInfo = Foo.child(Foo.name + "_IdTypeInfo");

        FooTest = dirs.modelPath.child(Foo.name + "Test");
        _Foo_stuffTest = FooTest.child("_" + Foo.name + "_stuff_Test");
        Foo_IdTest = FooTest.child(Foo.name + "_IdTest");
        Foo_IdAccessorTest = FooTest.child(Foo.name + "_IdAccessorTest");

        ClassPathInfo criteriaBase = dirs.criteriaInDao ? dirs.daoPath : dirs.modelPath;
        _FooCriteriaBuilder_stuff = criteriaBase.child(dao_ + "_" + Foo.name + criteriaBuilderSuffix + "_stuff", generated);
        FooCriteriaBuilder = criteriaBase.child(dao_ + Foo.name + criteriaBuilderSuffix);
        FooCriteriaBuilderTest = criteriaBase.child(dao_ + Foo.name + criteriaBuilderSuffix + "Test");

        FooSamples = dirs.daoPath.child(Foo.name + "Samples", generated);

        FooMapper = dirs.daoPath.child(dao_ + Foo.name + "Mapper");
        FooMapperTest = dirs.daoPath.child(dao_ + Foo.name + "MapperTest");
        FooManager = dirs.daoPath.child(dao_ + Foo.name + "Manager");
        FooManagerTest = dirs.daoPath.child(dao_ + Foo.name + "ManagerTest");
        FooExporter = dirs.daoPath.child(Foo.name + "Exporter", generated);

        FooIndex = dirs.wsPath.child(ws_ + Foo.name + "Index");
        FooIndexTest = dirs.wsPath.child(ws_ + Foo.name + "IndexTest");

        ClassPathInfo web = dirs.webPath;
        this.web = web;

        Esm_Foo_stuff_Type = web.child(_Foo_stuff.name + "_" + typeInfoSuffix);
        Esm_FooType = web.child(Foo.name + typeInfoSuffix);

        Esm_Foo_stuff = web.child(_Foo_stuff.name);
        Esm_Foo = web.child(Foo.name);

        Esm_Foo_stuff_Validators = web.child(_Foo_stuff.name + "_" + validatorsSuffix);
        Esm_FooValidators = web.child(Foo.name + validatorsSuffix);

        Esm_FooAdmin = web.child(Foo.name + "Admin");
        Esm_FooChooseDialog = web.child(Foo.name + "ChooseDialog");
        Esm_FooEditor = web.child(Foo.name + "Editor");
    }

    public UpdateMethod getPreferredUpdateMethod() {
        return defaultUpdateMethod;
    }

    public void setPreferredUpdateMethod(UpdateMethod updateMethod) {
        if (updateMethod == null)
            throw new NullPointerException("updateMethod");
        this.defaultUpdateMethod = updateMethod;
    }

    public ColumnNaming naming(IColumnMetadata column) {
        return config.naming(column);
    }

}
