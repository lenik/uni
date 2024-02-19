package net.bodz.lily.tool.daogen.dir.web;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.bodz.bas.c.string.StringEscape;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.c.type.TypeChain;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.order.OrdinalComparator;
import net.bodz.bas.t.predef.Predef;
import net.bodz.bas.t.predef.PredefMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.concrete.CoEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__vue;
import net.bodz.lily.tool.daogen.util.Attrs;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooEditor__vue
        extends JavaGen__vue {

    static final Logger logger = LoggerFactory.getLogger(FooEditor__vue.class);

    Map<String, EsmName> dialogs = new LinkedHashMap<>();

    public FooEditor__vue(JavaGenProject project) {
        super(project, project.Esm_FooEditor);
    }

    @Override
    protected String getTitle(ITableMetadata model) {
        String name = model.getJavaType().name;
        String words = StringId.SPACE.breakCamel(name);
        words = Strings.ucfirst(words);
        return "Editor view of: " + words;
    }

    @Override
    protected void buildScript1(TypeScriptWriter out, ITableMetadata model) {
        out.name(EsmModules.core.FieldGroup);
        out.name(EsmModules.core.FieldRow);

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
        out.printf("const model = defineModel<%s>();\n", //
                out.importName(project.Esm_Foo.qName));
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

        out.printf("const meta = %s.TYPE.property;\n", //
                out.importName(project.Esm_Foo.qName));

        int labelWidth = 7;
        out.printf("const fieldRowProps = %s({ labelWidth: '%drem' });\n", //
                out.name(EsmModules.dba.defaults.getDefaultFieldRowProps), //
                labelWidth);
        out.println();
        out.printf("const rootElement = %s<HTMLElement>();\n", //
                out.name(EsmModules.vue.ref));

        for (String dialogVar : dialogs.keySet()) {
            EsmName dialogTag = dialogs.get(dialogVar);
            out.printf("const %s = ref<InstanceType<typeof %s>>();\n", //
                    dialogVar, out.name(dialogTag));
        }

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
    }

    @Override
    protected void buildScript2(TypeScriptWriter out, ITableMetadata model) {
    }

    @Override
    protected void buildTemplate(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo info = new TypeAnalyzer(project, out, true)//
                .getExtendInfo(table, //
                        project.Foo.qName, //
                        project._Foo_stuff.qName);

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
                if (info.clazz != null && CoEntity.class.isAssignableFrom(info.clazz)) {
                    List<IColumnMetadata> columns = table.getColumns();
                    Map<String, IColumnMetadata> property2Column = new HashMap<>();
                    for (IColumnMetadata column : columns) {
                        ColumnNaming cname = project.config.naming(column);
                        property2Column.put(cname.propertyName, column);
                    }

                    Set<String> handled = new HashSet<>();
                    Set<String> bringForward = new HashSet<>(Arrays.asList(//
                            "CoObject.label", //
                            "CoObject.description" //
                    ));

                    Class<?> bringToTopClass = info.baseClass;

                    for (Class<?> decl : TypeChain.supersFromRoot(info.clazz)) {
                        if (! StructRow.class.isAssignableFrom(decl))
                            continue;

                        out.printf("<FieldGroup decl=\"%s\">\n", decl.getName());
                        out.enter();

                        IType type = BeanTypeProvider.getInstance().getType(decl);

                        Map<IColumnMetadata, IProperty> map1 = new TreeMap<>(OrdinalComparator.INSTANCE);
                        Map<CrossReference, IProperty> map2 = new TreeMap<>(OrdinalComparator.INSTANCE);

                        for (IProperty property : type.getProperties()) {

                            if (property.getDeclaringClass() == decl || decl == bringToTopClass) {
                                if (handled.contains(property.getName()))
                                    continue;

                                String classProp = property.getDeclaringClass().getSimpleName() + "."
                                        + property.getName();
                                if (bringForward.contains(classProp) && decl != bringToTopClass)
                                    continue;

                                IColumnMetadata column = property2Column.get(property.getName());
                                if (column != null) {
                                    if (column.isForeignKey()) {
                                        CrossReference xref = table.getForeignKeyFromColumn(column.getName());
                                        map2.put(xref, property); // can be repeat put.
                                    } else {
                                        map1.put(column, property);
                                    }
                                    handled.add(property.getName());
                                }
                            }
                        }

                        for (IColumnMetadata column : map1.keySet()) {
                            IProperty property = map1.get(column);
                            fieldRow(out, column, property);
                        }

                        for (CrossReference xref : map2.keySet()) {
                            IProperty property = map2.get(xref);
                            fkRow(out, xref, property);
                        }

                        out.leave();
                        out.println("</FieldGroup>");
                    }
                }

                out.leave();
            }
            out.println("</div>");

            for (String dialogVar : dialogs.keySet()) {
                EsmName dialogTag = dialogs.get(dialogVar);
                out.printf("<%s ref=\"%s\" />\n", //
                        out.name(dialogTag), //
                        dialogVar);
            }
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

    void fieldRow(TypeScriptWriter out, IColumnMetadata column, IProperty property) {
        ColumnNaming cname = project.naming(column);
        String propertyName = cname.propertyName;

        Class<?> type = column.getJavaClass();
        String tsType = tsTypes.resolve(type);

        out.printf("<FieldRow v-bind=\"fieldRowProps\" :property=\"meta.%s\" v-model=\"model.%s\">\n", //
                propertyName, //
                propertyName);

        out.enter();
        {
            boolean useInput = true;
            Attrs inputAttrs = new Attrs();
            switch (tsType) {
            case "number":
            case "integer":
            case "long":
                inputAttrs.put("type", "number");
                break;
            case "string":
                inputAttrs.put("type", "text");
                break;
            case "boolean":
                inputAttrs.put("type", "checkbox");
                break;
            case "Date":
                inputAttrs.put("type", "date");
                break;
            default:
                useInput = false;
            }

            if (useInput) {
                inputAttrs.put("v-model", "model." + propertyName);
                String xml = inputAttrs.toXml("input", true);
                out.println(xml);

            } else if (type.isEnum()) {
                @SuppressWarnings("unchecked")
                Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type;
                selectOptions(out, propertyName, enumMap(enumType));

            } else if (Predef.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                Class<? extends Predef<?, ?>> predefType = (Class<? extends Predef<?, ?>>) type;
                selectOptions(out, propertyName, _predefMap(predefType));

                // } else if (CoTag.class.isAssignableFrom(type)) {

                // } else if (CoCategory.class.isAssignableFrom(type)) {
            } else {
                logger.error("unsupported property: " + propertyName + " of " + type);
            }

            out.leave();
        }
        out.println("</FieldRow>");
    }

    void fkRow(TypeScriptWriter out, CrossReference xref, IProperty property) {
        String propertyName = xref.getJavaName(); // property.getName();
        String className = xref.getParentTable().getJavaType().getFullName();

        out.printf("<FieldRow v-bind=\"fieldRowProps\" :property=\"meta.%s\" v-model=\"model.%s\">\n", //
                propertyName, //
                propertyName);

        out.enter();
        {

            QualifiedName qType = QualifiedName.parse(className);
            QualifiedName qDialogType = qType.nameAdd("ChooseDialog");
            EsmSource dialogSource = out.packageMap.findSource(qDialogType, "vue", project.Esm_FooEditor.qName);
            if (dialogSource == null)
                throw new NullPointerException("can't find source for dialog type " + qDialogType + ", qType=" + qType);

            String dialogVar = Strings.lcfirst(qDialogType.name);

            dialogs.put(dialogVar, dialogSource.defaultExport(qDialogType.name));

            Attrs attrs = new Attrs();
            attrs.put(":dialog", dialogVar);
            attrs.put("v-model", "model." + xref.getJavaName());
            attrs.put("v-model:id", "model." + propertyName + "Id");

            String xml = attrs.toXml(out.im.name(//
                    EsmModules.dba.RefEditor), true);
            out.println(xml);

            out.leave();
        }
        out.println("</FieldRow>");
    }

    void selectOptions(TypeScriptWriter out, String property, Map<?, ?> options) {
        out.printf("<select v-model=\"model.%s\">\n", property);
        out.enter();
        for (Object k : options.keySet()) {
            String label = k.toString();
            String value = options.get(k).toString();
            value = StringEscape.escapeXmlText(value);
            out.printf("<option value=\"%s\">%s</option>\n", value, label);
        }
        out.leave();
        out.println("</select>");
    }

    static Map<String, Enum<?>> enumMap(Class<? extends Enum<?>> enumClass) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        EnumSet<?> set = EnumSet.allOf((Class) enumClass);
        Map<String, Enum<?>> map = new LinkedHashMap<>();
        for (Enum<?> e : set)
            map.put(e.name(), e);
        return map;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Map<String, ?> _predefMap(Class<? extends Predef<?, ?>> predefClass) {
        return predefMap((Class) predefClass);
    }

    static <E extends Predef<E, K>, K extends Comparable<K>> Map<String, K> predefMap(Class<E> predefClass) {
        PredefMetadata<E, K> metadata = PredefMetadata.forClass(predefClass);
        Map<K, E> keyMap = metadata.getKeyMap();
        Map<String, K> map = new LinkedHashMap<>();
        for (K key : keyMap.keySet()) {
            E e = keyMap.get(key);
            String label = e.getLabel().toString();
            if (label == null)
                label = e.getName();
            map.put(label, key);
        }
        return map;
    }

}
