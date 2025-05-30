package net.bodz.lily.tool.daogen.dir.web;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.GeneratedValue;

import net.bodz.bas.c.string.StringEscape;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.c.type.TypeChain;
import net.bodz.bas.code.util.Attrs;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.esm.extern.ExternModules;
import net.bodz.bas.esm.skel01.Skel01Modules;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.bean.DetailLevel;
import net.bodz.bas.meta.bean.Internal;
import net.bodz.bas.meta.cache.Derived;
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
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.meta.FieldGroupVue;
import net.bodz.lily.meta.ReadOnly;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__vue;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooEditor__vue
        extends DaoGen__vue {

    static final Logger logger = LoggerFactory.getLogger(FooEditor__vue.class);

    Map<String, EsmName> dialogs = new LinkedHashMap<>();

    public FooEditor__vue(DaoGenProject project) {
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
    protected boolean templateFirst() {
        return true;
    }

    @Override
    protected void buildScript1(TypeScriptWriter out, ITableMetadata model) {
        out.name(Skel01Modules.core.FieldRow.FieldRow);

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
                out.importDefault(project.Esm_Foo.qName));
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
                out.importDefault(project.Esm_Foo.qName));

        int labelWidth = 7;
        out.printf("const fieldRowProps = %s({ labelWidth: '%drem' });\n", //
                out.name(Skel01Modules.dba.defaults.getDefaultFieldRowProps), //
                labelWidth);
        out.printf("%s(%s, fieldRowProps);\n", //
                out.name(ExternModules.vue.provide), //
                out.name(Skel01Modules.core.FieldRow.FIELD_ROW_PROPS));

        out.println();
        out.printf("const rootElement = %s<HTMLElement>();\n", //
                out.name(ExternModules.vue.ref));

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
                out.name(ExternModules.vue.onMounted));
        out.println("});");
    }

    @Override
    protected void buildScript2(TypeScriptWriter out, ITableMetadata model) {
    }

    boolean bringForwards = false;
    Set<String> bringForwardProps = new HashSet<>(Arrays.asList(//
            "CoObject.label", //
            "CoObject.description", //
            "CoObject.icon" //
    ));

    @Override
    protected void buildTemplate(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
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
            out.println(rootAttrs.toHtml("div"));

            if (extend.javaClass != null && StructRow.class.isAssignableFrom(extend.javaClass)) {
                out.enter();
                fieldGroups(out, table, extend);
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

    static final List<Class<? extends Annotation>> excludeAnnotations;
    static final List<Class<? extends Annotation>> readOnlyAnnotations;

    static {
        excludeAnnotations = new ArrayList<>();
        excludeAnnotations.add(Derived.class);
        excludeAnnotations.add(Internal.class);
        readOnlyAnnotations = new ArrayList<>();
        readOnlyAnnotations.add(GeneratedValue.class);
        readOnlyAnnotations.add(ReadOnly.class);
    }

    void fieldGroups(TypeScriptWriter out, ITableMetadata table, TypeExtendInfo extend) {
//        List<IColumnMetadata> columns = table.getColumns();
        Map<String, IColumnMetadata> property2Column = new HashMap<>();
        for (IColumnMetadata column : table.getColumns()) {
            ColumnNaming cname = project.config.naming(column);
            property2Column.put(cname.propertyName, column);
        }

//        Map<String, CrossReference> fkeys = table.getForeignKeys();
        Map<String, CrossReference> property2FKey = new HashMap<>();
        for (CrossReference xref : table.getForeignKeys().values()) {
            String propertyName = xref.getPropertyName();
            property2FKey.put(propertyName, xref);
        }

        Set<String> handled = new HashSet<>();

//        Class<?> bringToTopClass = extend.javaBaseClass;

        Map<Class<?>, Map<IProperty, IColumnMetadata>> groups = new LinkedHashMap<>();

        for (Class<?> decl : TypeChain.supersToRoot(extend.javaClass, StructRow.class)) {
            IType type = BeanTypeProvider.getInstance().getType(decl);

            Map<IProperty, IColumnMetadata> selection = new LinkedHashMap<>();
L:
            for (IProperty property : type.getProperties()) {
                String propName = property.getName();
                if (handled.contains(propName))
                    continue;

                for (Class<? extends Annotation> a : excludeAnnotations)
                    if (property.isAnnotationPresent(a))
                        continue L;

                DetailLevel aDetailLevel = property.getAnnotation(DetailLevel.class);
                if (aDetailLevel != null) {
                    int detailLevel = aDetailLevel.value();
                    if (detailLevel == DetailLevel.HIDDEN)
                        continue;
                }

                Class<?> propDecl = property.getDeclaringClass();
                if (propDecl != decl) {
                    if (!bringForwards)
                        continue;
                    String classProp = propDecl.getSimpleName() + "." + propName;
                    if (!bringForwardProps.contains(classProp))
                        continue;
                }

                IColumnMetadata column = property2Column.get(propName);
                if (column == null) {
                    CrossReference fkey = property2FKey.get(propName);
                    if (fkey != null) {
                        IColumnMetadata[] fCols = fkey.getForeignColumns();
                        column = fCols[0];
                    }
                }

                if (column != null) {
                    selection.put(property, column);
                    handled.add(propName);
                    continue;
                }

                // System.err.println();
            }

            if (selection.isEmpty())
                continue;

            groups.put(decl, selection);
        }

        List<Class<?>> supersFromRoot = new ArrayList<>(groups.keySet());
        Collections.reverse(supersFromRoot);

        for (Class<?> decl : supersFromRoot) {
            if (decl.isAnnotationPresent(FieldGroupVue.class)) {
                QualifiedName fieldGroupVue = QualifiedName.of(decl).append("FieldGroup");
                out.printf("<%s :meta=\"meta\" v-model=\"model\" />\n", //
                        out.importVue(fieldGroupVue));
            } else {
                fieldGroup(out, table, decl, groups.get(decl));
            }
        }
    }

    void fieldGroup(TypeScriptWriter out, ITableMetadata table, Class<?> decl, Map<IProperty, IColumnMetadata> selection) {

        String tsTypeInfo = typeInfoResolver().resolveClass(decl);

        out.printf("<%s :type=\"%s\">\n", //
                out.name(Skel01Modules.dba.FieldGroup), //
                tsTypeInfo);
        out.enter();

        Map<IColumnMetadata, IProperty> map1 = new TreeMap<>(OrdinalComparator.INSTANCE);
        Map<CrossReference, IProperty> map2 = new TreeMap<>(OrdinalComparator.INSTANCE);

        for (IProperty property : selection.keySet()) {
            IColumnMetadata column = selection.get(property);
            if (column.isForeignKey()) {
                CrossReference xref = table.getForeignKeyFromColumn(column.getName());
                map2.put(xref, property); // can be repeat put.
            } else {
                map1.put(column, property);
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

    void fieldRow(TypeScriptWriter out, IColumnMetadata column, IProperty property) {
        ColumnNaming cname = project.naming(column);
        String propertyName = cname.propertyName;
        String propertyModel = "model." + propertyName;

        out.printf("<FieldRow :property=\"meta.%s\" v-model=\"%s\">\n", //
                propertyName, //
                propertyModel);
        {
            out.enter();

            editControl(out, column, property);

            out.leave();
        }
        out.println("</FieldRow>");
    }

    static boolean isReadOnly(IProperty property) {
        if (!property.isWritable())
            return true;
        for (Class<? extends Annotation> a : readOnlyAnnotations)
            if (property.isAnnotationPresent(a))
                return true;
        return false;
    }

    void fkRow(TypeScriptWriter out, CrossReference xref, IProperty property) {
        String propertyName = xref.getPropertyName();
        out.printf("<FieldRow :property=\"meta.%s\" v-model=\"model.%s\">\n", //
                propertyName, //
                propertyName);
        {
            out.enter();
            refEditor(out, xref, property);
            out.leave();
        }
        out.println("</FieldRow>");
    }

    void refEditor(TypeScriptWriter out, CrossReference xref, IProperty property) {
        String propertyName = xref.getPropertyName();
        String className = xref.getParentTable().getJavaTypeName();

        QualifiedName qType = QualifiedName.parse(className);
        // EsmSource typeSrc = out.domainMap.findSource(qType, "ts", project.Esm_FooEditor.qName);
        QualifiedName qTypeInfo = qType.nameAdd("TypeInfo");
        String typeInfoClass = out.importDefault(qTypeInfo);

        QualifiedName qDialogType = qType.nameAdd("ChooseDialog");
        EsmSource dialogSource = out.domainMap.findSource(qDialogType, "vue", project.Esm_FooEditor.qName);
        if (dialogSource == null)
            throw new NullPointerException("can't find source for dialog type " + qDialogType + ", qType=" + qType);

        String dialogVar = Strings.lcfirst(qDialogType.name);
        dialogs.put(dialogVar, dialogSource.defaultExport(qDialogType.name));

        Attrs attrs = new Attrs();
        attrs.put(":type", typeInfoClass + ".INSTANCE");
        attrs.put(":dialog", dialogVar);
        attrs.put("v-model", "model." + propertyName);
        attrs.put("v-model:id", "model." + propertyName + "Id");

        boolean readOnly = isReadOnly(property);
        if (readOnly)
            attrs.put("disabled", Attrs.NO_VALUE);

        String html = attrs.toHtml(out.im.name(//
                Skel01Modules.dba.RefEditor), true);
        out.println(html);
    }

//    void readView(TypeScriptWriter out, IColumnMetadata column, IProperty property) {
//        ColumnNaming cname = project.naming(column);
//        String propertyName = cname.propertyName;
//        String propertyModel = "model." + propertyName;
//        out.printf("<div class='readonly' v-text=\"%s\"></div>\n", propertyModel);
//    }

    void editControl(TypeScriptWriter out, IColumnMetadata column, IProperty property) {
        ColumnNaming cname = project.naming(column);
        String propertyName = cname.propertyName;
        String propertyModel = "model." + propertyName;

        Class<?> type = column.getJavaClass();
        String tsType = typeResolver().importAsType()//
                .property(property.getName())//
                .resolveClass(type);

        final String defaultTagName = "input";
        String tagName = defaultTagName;
        Attrs tagAttrs = new Attrs();
        switch (tsType) {
            case "number":
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
                tagAttrs.put("type", "number");
                break;

            case "BigDecimal":
            case "BigInteger":
                tagAttrs.put("type", "number");
                break;

            case "boolean":
                tagAttrs.put("type", "checkbox");
                break;
            case "char":
            case "string":
            case "InetAddress":
                tagAttrs.put("type", "text");
                if (tsType.equals("char"))
                    tagAttrs.put("maxlength", 1);
                break;

            case "JavaDate":
            case "SQLDate":
            case "Timestamp":

            case "LocalDate":
            case "LocalTime":
            case "OffsetTime":
            case "Instant":
            case "LocalDateTime":
            case "ZonedDateTime":
            case "OffsetDateTime":
                tagName = out.importName(Skel01Modules.core.DateTime);
                break;

            case "JsonVariant":
                if ("files".equals(propertyName)) {
                    tagName = out.importName(Skel01Modules.dba.FilesEditor);
                } else {
                    tagName = out.importName(Skel01Modules.core.JsonEditor);
                }
                break;
        }

        if (!tagAttrs.isEmpty() || !tagName.equals(defaultTagName)) {
            switch (tsType) {
                default:
                    tagAttrs.put("v-model", propertyModel);
            }
            if (isReadOnly(property))
                tagAttrs.put("disabled", Attrs.NO_VALUE);

            String html = tagAttrs.toHtml(tagName, true);
            out.println(html);
            return;
        }

        if (type.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type;
            selectOptions(out, propertyName, enumMap(enumType));
            return;
        }

        if (Predef.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            Class<? extends Predef<?, ?>> predefType = (Class<? extends Predef<?, ?>>) type;
            selectOptions(out, propertyName, _predefMap(predefType));
            // } else if (CoCategory.class.isAssignableFrom(type)) {
            return;
        }

        logger.errorf("don't know how to make an html input for type %s used by property %s.", //
                type.getName(), propertyName);
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
