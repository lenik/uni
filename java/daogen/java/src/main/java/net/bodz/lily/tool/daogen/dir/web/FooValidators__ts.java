package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;

public class FooValidators__ts
        extends JavaGen__ts {

    public FooValidators__ts(JavaGenProject project) {
        super(project, project.Esm_FooValidators);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        out.name(EsmModules.core.uiTypes.ValidateResult);

        // for ...
        out.printf("export function validate_birthday(val: %s) {\n", //
                out.name(EsmModules.moment.Moment));
        out.println("}");
        out.println();
    }

}
