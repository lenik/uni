package net.bodz.lily.tool.daogen.dir.web;

import java.util.HashSet;
import java.util.Set;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__vue;

public class FooChooseDialog__vue
        extends JavaGen__vue {

    TsTemplates templates;

    public FooChooseDialog__vue(JavaGenProject project) {
        super(project, project.Esm_FooChooseDialog);
        templates = new TsTemplates(project);
    }

    @Override
    protected void buildScript1(TypeScriptWriter out, ITableMetadata table) {
        out.println("export interface Props {");
        out.enter();
        {
            out.println("modal?: boolean | string");
            out.leave();
        }
        out.println("}");
    }

    @Override
    protected void buildSetupScript(TypeScriptWriter out, ITableMetadata table) {
        out.println("const model = defineModel();");
        out.println();
        out.println("const props = withDefaults(defineProps<Props>(), {");
        out.enter();
        {
            out.println("modal: true");
            out.leave();
        }
        out.println("});");
        out.println();
        out.println("const emit = defineEmits<{");
        out.enter();
        {
            out.println("error: [message: string]");
            out.leave();
        }
        out.println("}>();");
        out.println();
        out.println("// property shortcuts");
        out.println();
        out.printf("const entityChooseDialog = ref<undefined | InstanceType<typeof %s>>();", //
                out.name(EsmModules.dba.EntityChooseDialog));
        out.println();
        out.println("defineExpose({ open });");
        out.println();
        out.printf("function open(callback?: %s) {\n", //
                out.name(EsmModules.core.uiTypes.DialogSelectCallback));
        out.enter();
        {
            out.println("entityChooseDialog.value?.open(callback);");
            out.leave();
        }
        out.println("}");
        out.println();
        out.println("%s(() => {\n", //
                out.name(EsmModules.vue.onMounted));
        out.println("});");

    }

    @Override
    protected void buildScript2(TypeScriptWriter out, ITableMetadata table) {
    }

    @Override
    protected void buildTemplate(TypeScriptWriter out, ITableMetadata table) {
        out.println("<template>");
        out.enter();
        {
            out.println("<EntityChooseDialog ref=\"entityChooseDialog\" :type=\"Person.TYPE\" :modal=\"modal\">");
            out.enter();
            {
                Set<String> handledXrefs = new HashSet<>();

                for (IColumnMetadata column : table.getColumns()) {
                    if (column.isCompositeProperty()) {
                        // checkCompositeProperty(table, column);
                        continue;
                    }

                    if (column.isForeignKey()) {
                        CrossReference xref = table.getForeignKeyFromColumn(column.getName());
                        if (handledXrefs.add(xref.getConstraintName()))
                            templates.declFKColumn(out, xref);
                    } else {
                        templates.declColumn(out, column);
                    }
                }

                out.leave();
            }
            out.println("</EntityChooseDialog>");
            out.leave();
        }
        out.println("</template>");
        out.println();
        out.println("<style scoped lang=\"scss\">");
        out.println(".component-root {");
        out.enter();
        {
            out.println("padding: 0;");
            out.leave();
        }
        out.println("}");
        out.println("</style>");
    }

}
