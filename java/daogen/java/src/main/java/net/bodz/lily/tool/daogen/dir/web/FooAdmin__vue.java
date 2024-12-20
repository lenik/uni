package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.esm.extern.ExternModules;
import net.bodz.bas.esm.skel01.SkeljsModules;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;

public class FooAdmin__vue
        extends DTDriven__vue {

    public FooAdmin__vue(JavaGenProject project) {
        super(project, project.Esm_FooAdmin);
    }

    @Override
    protected String getTitle(ITableMetadata model) {
        String name = model.getJavaType().name;
        String words = StringId.SPACE.breakCamel(name);
        words = Strings.ucfirst(words);
        return "Admin view of: " + words;
    }

    @Override
    protected void buildScript1(TypeScriptWriter out, ITableMetadata table) {
        out.println("export interface Props {");
        out.println("}");
    }

    @Override
    protected void buildSetupScript(TypeScriptWriter out, ITableMetadata table) {
        out.println("const props = withDefaults(defineProps<Props>(), {");
        out.println("});");
        out.println();
        out.printf("const admin = %s<InstanceType<typeof %s>>();\n", //
                out.name(ExternModules.vue.ref), //
                out.name(SkeljsModules.dba.LilyAdmin));
        out.printf("const type = %s.TYPE;\n", //
                out.importDefault(project.Esm_Foo.qName));
        out.println("const selection = ref<any>({});");

        out.println();
        dumpTypeMap(out);

        out.println();
        out.printf("%s(() => {\n", //
                out.name(ExternModules.vue.onMounted));
        out.println("});");
        out.println();
    }

    @Override
    protected void buildScript2(TypeScriptWriter out, ITableMetadata table) {
    }

    @Override
    protected void buildTemplate(TypeScriptWriter out, ITableMetadata table) {
        out.println("<template>");
        out.enter();
        {
            out.printf("<%s ref=\"admin\" :type=\"type\" :typeMap=\"typeMap\" v-model=\"selection\">\n", //
                    out.name(SkeljsModules.dba.LilyAdmin));
            out.enter();
            {
                out.println("<template #columns>");
                out.enter();
                {
                    buildColumns(out, table);
                    out.leave();
                }
                out.println("</template>");
                out.println("<template #preview>");
                out.enter();
                {
                    out.printf("<%s class=\"editor\" v-model=\"selection\" />\n", //
                            out.importVue(project.Esm_FooEditor.qName));
                    out.leave();
                }
                out.println("</template>");
                out.println("<template #side-tools> Side Tools</template>");
                out.println("<template #editor>");
                out.enter();
                {
                    out.printf("<%s class=\"editor\" v-model=\"selection\" />\n",
                            out.importVue(project.Esm_FooEditor.qName));
                    out.leave();
                }
                out.println("</template>");
                out.leave();
            }
            out.println("</LilyAdmin>");
            out.leave();
        }
        out.println("</template>");
        out.println();
        out.println("<style lang=\"scss\"></style>");
        out.println();
        out.println("<style scoped lang=\"scss\">");
        out.println(".lily-admin {");
        out.println("    padding: 0;");
        out.println("}");
        out.println("</style>");
    }

}
