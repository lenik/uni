package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__vue;
import net.bodz.lily.tool.daogen.util.Attrs;

public class FooEditor__vue
        extends JavaGen__vue {

    public FooEditor__vue(JavaGenProject project) {
        super(project, project.Esm_FooEditor);
    }

    @Override
    protected void buildScript1(TypeScriptWriter out, ITableMetadata model) {
        out.name(EsmModules.coreVue.FieldRow.FieldRow);

        out.println("export interface Props {");
        out.println("}");
    }

    @Override
    protected void buildSetupScript(TypeScriptWriter out, ITableMetadata model) {
        out.println("defineOptions({");
        out.enter();
        {
            out.println("inheritAttrs: false");
            out.leave();
        }
        out.println("});");
        out.println();
        out.println("const model = defineModel<%s>();", //
                "Person");
        out.println();
        out.println("const props = withDefaults(defineProps<Props>(), {");
        out.println("});");
        out.println();
        out.println("const emit = defineEmits<{");
        out.enter();
        {
            out.println("error: [message: string]");
            out.println("change: [e: Event]");
            out.leave();
        }
        out.println("}>();");
        out.println();
        out.println("// property shortcuts");
        out.println();

        out.printf("const meta = %s.TYPE.property;", //
                out.localName(project.Esm_Foo.qName));

        int labelWidth = 7;
        out.printf("const fieldRowProps = %s({ labelWidth: '%drem' });\n", //
                out.name(EsmModules.dba.defaults.getDefaultFieldRowProps), //
                labelWidth);
        out.println();
        out.println("const rootElement = ref<HTMLElement>();");

        String dialogVar = Strings.lcfirst(project.Esm_FooChooseDialog.name);
        out.printf("const %s = ref<InstanceType<typeof %s>>();\n", //
                dialogVar, //
                out.localVue(project.Esm_FooChooseDialog.qName));

        out.println("const valids = ref<any>({});");
        out.println();
        out.println("// methods");
        out.println();
        out.println("defineExpose({ update });");
        out.println();
        out.println("function update() {");
        out.println("}");
        out.println();
        out.printf("%s(() => {\n", //
                out.name(EsmModules.vue.onMounted));
        out.println("});");
        out.println();
        out.println("</script>");
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
            Attrs rootAttrs = new Attrs();
            rootAttrs.put("class", "entity-editor " + "person-editor");
            rootAttrs.put("ref", "rootElement");
            rootAttrs.put("v-if", "model != null");
            rootAttrs.put("v-bind", "$attrs");
            out.println(rootAttrs.toXml("div"));

            out.enter();
            {
                out.println("<FieldRow v-bind=\"fieldRowProps\" :property=\"meta.label\" v-model=\"model.label\">");
                out.enter();
                {
                    Attrs inputAttrs = new Attrs();
                    inputAttrs.put("type", "text");
                    inputAttrs.put("v-model", "model.label");
                    inputAttrs.put("placeholder", "enter text...");
                    String xml = inputAttrs.toXml("input", true);
                    out.println(xml);
                    out.leave();
                }
                out.println("</FieldRow>");
                out.leave();
            }
            out.println("</div>");

            String dialogVar = "";
            out.printf("<%s ref=\"%s\" />\n", //
                    out.localVue(project.Esm_FooChooseDialog.qName), //
                    dialogVar);

            out.leave();
        }
        out.println("</template>");
        out.println();
        out.println("<style scoped lang=\"scss\">");
        out.println(".entity-editor {");
        out.enter();
        {
            out.println("padding: 0;");
            out.leave();
        }
        out.println("}");
        out.println("</style>");

    }

}
