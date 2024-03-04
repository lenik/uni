package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;

public class FooChooseDialog__vue
        extends DTDriven__vue {

    public FooChooseDialog__vue(JavaGenProject project) {
        super(project, project.Esm_FooChooseDialog);
    }

    @Override
    protected String getTitle(ITableMetadata model) {
        String name = model.getJavaType().name;
        String words = StringId.SPACE.breakCamel(name);
        words = Strings.ucfirst(words);
        return "Choose dialog for: " + words;
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
        dumpTypeMap(out);

        out.println();
        out.printf("const entityChooseDialog = %s<undefined | InstanceType<typeof %s>>();", //
                out.name(EsmModules.vue.ref), //
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
        out.printf("%s(() => {\n", //
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
            out.printf(
                    "<EntityChooseDialog ref=\"entityChooseDialog\" :type=\"%s.TYPE\" :typeMap=\"typeMap\" :modal=\"modal\">\n", //
                    out.importName(project.Foo.qName));
            out.enter();
            {
                buildColumns(out, table);
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
