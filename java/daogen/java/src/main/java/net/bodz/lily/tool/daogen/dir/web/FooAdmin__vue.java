package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__vue;

public class FooAdmin__vue
        extends JavaGen__vue {

    public FooAdmin__vue(JavaGenProject project) {
        super(project, project.Esm_FooAdmin);
    }

    @Override
    protected void buildScript1(TypeScriptWriter out, ITableMetadata model) {
        out.println("export interface Props {");
        out.println("}");
    }

    @Override
    protected void buildSetupScript(TypeScriptWriter out, ITableMetadata model) {
        out.println();
        out.println("const props = withDefaults(defineProps<Props>(), {");
        out.println("});");
        out.println();
        out.printf("const admin = %s<InstanceType<typeof %s>>();\n", //
                out.name(EsmModules.vue.ref), //
                out.name(EsmModules.dbaVue.LilyAdmin.LilyAdmin));
        out.printf("const type = %s.TYPE;\n", //
                out.localName(project.Esm_Foo.qName));
        out.println("const selection = ref<any>({});");

        String defaultDialogVar = "defaultPersonChooseDialog";
        out.printf("const %s = ref<InstanceType<typeof %s>>();\n", //
                defaultDialogVar, //
                out.localVue(project.Esm_FooChooseDialog.qName));
        out.println();
        out.printf("%s(() => {\n", //
                out.name(EsmModules.vue.onMounted));
        out.println("});");
        out.println();
    }

    @Override
    protected void buildScript2(TypeScriptWriter out, ITableMetadata model) {
    }

    @Override
    protected void buildTemplate(TypeScriptWriter out, ITableMetadata model) {

        out.println("<template>");
        out.enter();
        {
            out.printf("<%s ref=\"admin\" :type=\"type\" v-model=\"selection\">\n", //
                    out.name(EsmModules.dbaVue.LilyAdmin.LilyAdmin));
            out.enter();
            {
                out.println("<template #columns>");
                out.enter();
                {
                    out.println("<th data-field=\"id\" class=\"id\">ID</th>");
                    out.println("<th data-field=\"properties\" class=\"hidden\">properties</th>");
                    out.println("<th data-field=\"label\">Name</th>");
                    out.leave();
                }
                out.println("</template>");
                out.println("<template #preview>");
                out.enter();
                {
                    out.printf("<%s class=\"editor\" v-model=\"selection\" />\n", //
                            out.localVue(project.Esm_FooEditor.qName));
                    out.leave();
                }
                out.println("</template>");
                out.println("<template #side-tools> Side Tools</template>");
                out.println("<template #editor>");
                out.enter();
                {
                    out.printf("<%s class=\"editor\" v-model=\"selection\" />\n",
                            out.localVue(project.Esm_FooEditor.qName));
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
        out.println(".lily-admin {}");
        out.println("}");
        out.println("</style>");
    }

}