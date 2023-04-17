package net.bodz.lily.tool.javagen.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Table;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.potato.PotatoTypes;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.TableOid;

public class EntityClassModel
        extends TableOid {

    private static final long serialVersionUID = 1L;

    public Class<?> declaringClass;

    Map<String, EntityFieldModel> fields = new LinkedHashMap<>();

    public EntityClassModel(Class<?> clazz) {
        fromDeclaredFields(clazz);
    }

    public void fromDeclaredFields(Class<?> clazz) {
        this.declaringClass = clazz;

        IType type = PotatoTypes.getInstance().loadType(clazz);

        String pkg = clazz.getPackage().getName();
        String simple = clazz.getSimpleName();
        String maskQn = pkg + ".impl." + simple + "Mask";
        IType maskType = null;
        try {
            Class<?> maskClass = Class.forName(maskQn);
            maskType = PotatoTypes.getInstance().loadType(maskClass);
        } catch (ClassNotFoundException e) {
        }

        Table aTable = clazz.getAnnotation(Table.class);
        catalogName = Nullables.trimToNull(aTable.catalog());
        schemaName = Nullables.trimToNull(aTable.schema());
        tableName = aTable.name();

        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers))
                continue;
            if (!Serializable.class.isAssignableFrom(field.getType()))
                continue;

            EntityFieldModel item = new EntityFieldModel();
            String name = field.getName();
            item.fieldName = name;
            item.columnName = StringId.UL.breakCamel(name);
            item.fieldType = field.getType();

            boolean hasSqlCast = type.getProperty(name + "_sql") != null;
            if (hasSqlCast)
                item.fieldName_sqlOverride = name + "_sql";

            if (maskType != null) {
                MaskFieldModel mask = new MaskFieldModel(name);
                mask.hasMain = maskType.getProperty(name) != null;
                mask.hasRange = maskType.getProperty(name + "Range") != null;
                mask.hasPattern = maskType.getProperty(name + "Pattern") != null;
                item.mask = mask;
            }
            fields.put(name, item);
        }
    }

    public Map<String, EntityFieldModel> getFieldMap() {
        return fields;
    }

    public Collection<EntityFieldModel> getFields() {
        return fields.values();
    }

}
