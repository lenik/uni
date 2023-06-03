package net.bodz.lily.tool.daogen.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.FormatException;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.api.ElementHandlerException;
import net.bodz.bas.fmt.json.IJsonForm;
import net.bodz.bas.fmt.json.IJsonOut;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.fmt.rst.IRstForm;
import net.bodz.bas.fmt.rst.IRstHandler;
import net.bodz.bas.fmt.rst.IRstOutput;
import net.bodz.bas.json.JsonObject;
import net.bodz.bas.t.catalog.ColumnOid;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.map.ListMap;
import net.bodz.lily.tool.daogen.ColumnName;
import net.bodz.lily.tool.daogen.DialectFn;
import net.bodz.lily.tool.daogen.TableName;

public class CatalogConfig
        implements
            IRstForm,
            IJsonForm {

    static final String K_COLUMN_PROPERTY = "column-property";
    static final String K_COLUMN_REF = "column-ref";
    static final String K_KEY_COLUMNS = "key-columns";
    static final String K_TABLE_NAME = "table-name";
    static final String K_CLASS_MAP = "class-map";
    static final String K_COLUMN_LEVEL = "column-level";
    static final String K_JOIN_LEVEL = "join-level";
    static final String K_TABLES = "tables";
    static final String K_TABLE = "table";
    static final String K_MIXINS = "mixins";
    static final String K_MIXIN = "mixin";

    public String defaultPackageName;

    public final Map<String, String> columnPropertyMap = new HashMap<>();
    public final ColumnRefMap columnRefMap = new ColumnRefMap();
    public final KeyColumnSettings keyColumnSettings = new KeyColumnSettings();
    public final Map<String, String> tableNameMap = new HashMap<>();
    public final ListMap<String, String> class2TableList = new ListMap<>();
    public final Map<String, Integer> columnLevelMap = new HashMap<>();
    public final Map<String, Integer> joinLevelMap = new HashMap<>();

    public final Map<String, TableSettings> tableMap = new LinkedHashMap<>();

    // cache
    Map<String, String> tableClassMap;
    Map<String, MixinSettings> mixinMap;

    NameDecoratorList foreignKeyDecorators = new NameDecoratorList();

    public CatalogConfig() {
        foreignKeyDecorators.addSuffix("Id");
    }

    List<String> resolveTableList(String className) {
        List<String> list = class2TableList.get(className);
        if (list == null) {
            list = new ArrayList<>();
            class2TableList.put(className, list);
        }
        return list;
    }

    TableSettings resolveTable(String tableName) {
        TableSettings table = tableMap.get(tableName);
        if (table == null)
            tableMap.put(tableName, table = new TableSettings());
        return table;
    }

    MixinSettings resolveMixin(String mixinName) {
        MixinSettings mixin = mixinMap.get(mixinName);
        if (mixin == null) {
            mixin = new MixinSettings();
            mixinMap.put(mixinName, mixin);
        }
        return mixin;
    }

    public TableName tableName(ITableMetadata table) {
        TableName n = new TableName();
        n.tableName = table.getName();
        n.tableNameQuoted = DialectFn.quoteName(n.tableName);
        n.compactName = table.getCompactName();
        n.compactNameQuoted = DialectFn.quoteQName(n.compactName);
        n.fullName = table.getId().getFullName();
        n.fullNameQuoted = DialectFn.quoteQName(n.fullName);

        String simple = table.getJavaName();
        if (simple == null) {
            simple = StringId.UL.toCamel(n.tableName);
            simple = Strings.ucfirst(simple);
        }
        n.simpleClassName = simple;

        n.packageName = table.getJavaPackage();
        if (n.packageName == null)
            n.packageName = table.getId().getPreferredPackageName(defaultPackageName);

        n.className = n.packageName + "." + n.simpleClassName;
        return n;
    }

    public ColumnName columnName(IColumnMetadata column) {
        ColumnName n = new ColumnName();
        n.column = column.getName();
        n.columnQuoted = DialectFn.quoteName(n.column);

        // boolean javaNameSpecified = column.getJavaName() != null;
        String field = column.getJavaName();
        if (field == null)
            field = StringId.UL.toCamel(n.column);
        n.setField(field);

//        if (column.isForeignKey()) {
//            name.keyProperty = name.property;
//            name.refProperty = name.property;
//
//            INameDecorator decorator = foreignKeyDecorators.findDecorator(name.property);
//            if (decorator != null)
//                name.refProperty = decorator.undecorate(name.property);
//            else
//                name.keyProperty = foreignKeyDecorators.getPreferredDecoratedName(name.property);
//        }
        return n;
    }

    public ColumnName[] columnNames(IColumnMetadata[] columns) {
        int n = columns.length;
        ColumnName[] names = new ColumnName[n];
        for (int i = 0; i < n; i++)
            names[i] = columnName(columns[i]);
        return names;
    }

    @Override
    public void writeObject(IRstOutput out)
            throws IOException, FormatException {
        if (!columnPropertyMap.isEmpty()) {
            out.beginElement(K_COLUMN_PROPERTY);
            for (String column : columnPropertyMap.keySet()) {
                String property = columnPropertyMap.get(column);
                out.attribute(column, property);
            }
            out.endElement();
        }

        if (!columnRefMap.isEmpty()) {
            out.beginElement(K_COLUMN_REF);
            for (String alias : columnRefMap.alias2QColumn.keySet()) {
                ColumnOid qColumn = columnRefMap.alias2QColumn.get(alias);
                out.attribute(alias, qColumn.getFullName());
            }
            out.endElement();
        }

        if (!keyColumnSettings.isEmpty()) {
            out.beginElement(K_KEY_COLUMNS);
            keyColumnSettings.writeObject(out);
            out.endElement();
        }

        if (!tableNameMap.isEmpty()) {
            out.beginElement(K_TABLE_NAME);
            for (String table : tableNameMap.keySet()) {
                String javaName = tableNameMap.get(table);
                out.attribute(table, javaName);
            }
            out.endElement();
        }

        if (!class2TableList.isEmpty()) {
            out.beginElement(K_CLASS_MAP);
            for (String type : class2TableList.keySet()) {
                List<String> tables = class2TableList.get(type);
                String tableList = StringArray.join(", ", tables);
                out.attribute(type, tableList);
            }
            out.endElement();
        }

        if (!columnLevelMap.isEmpty()) {
            out.beginElement(K_COLUMN_LEVEL);
            for (String column : columnLevelMap.keySet()) {
                Integer level = columnLevelMap.get(column);
                out.attribute(column, level);
            }
            out.endElement();
        }

        if (!joinLevelMap.isEmpty()) {
            out.beginElement(K_JOIN_LEVEL);
            for (String column : joinLevelMap.keySet()) {
                Integer depth = joinLevelMap.get(column);
                out.attribute(column, depth);
            }
            out.endElement();
        }

        if (!tableMap.isEmpty()) {
            for (String tableName : tableMap.keySet()) {
                out.beginElement(K_TABLE, tableName);
                TableSettings table = tableMap.get(tableName);
                table.writeObject(out);
                out.endElement();
            }
        }

        if (!mixinMap.isEmpty()) {
            for (String mixinName : mixinMap.keySet()) {
                out.beginElement(K_MIXIN, mixinName);
                MixinSettings mixin = mixinMap.get(mixinName);
                mixin.writeObject(out);
                out.endElement();
            }
        }
    }

    public synchronized Map<String, String> getTableClassMap() {
        if (tableClassMap == null) {
            tableClassMap = new HashMap<>();
            for (String type : class2TableList.keySet())
                for (String table : class2TableList.get(type))
                    tableClassMap.put(table, type);
        }
        return tableClassMap;
    }

    @Override
    public IRstHandler getElementHandler() {
        return new IRstHandler() {
            String parent;

            @Override
            public IRstHandler beginChild(String name, String[] args)
                    throws ParseException, ElementHandlerException {
                this.parent = name;
                switch (name) {
                case K_KEY_COLUMNS:
                    return keyColumnSettings.getElementHandler();

                case K_TABLE:
                    if (args.length != 1)
                        throw new ParseException("expect table name");
                    String tableName = args[0].trim();
                    TableSettings table = resolveTable(tableName);
                    return table.getElementHandler();

                case K_MIXIN:
                    if (args.length != 1)
                        throw new ParseException("expect mixin name");
                    String mixinName = args[0].trim();
                    MixinSettings mixin = resolveMixin(mixinName);
                    return mixin.getElementHandler();
                }
                return this;
            }

            @Override
            public boolean attribute(String name, String data)
                    throws ParseException, ElementHandlerException {
                switch (parent) {
                case K_COLUMN_PROPERTY:
                    columnPropertyMap.put(name, data.trim());
                    return true;

                case K_COLUMN_REF:
                    columnRefMap.addColumnRef(name, data.trim());
                    return true;

                case K_TABLE_NAME:
                    tableNameMap.put(name, data.trim());
                    return true;

                case K_CLASS_MAP:
                    for (String token : data.split(",")) {
                        resolveTableList(name).add(token.trim());
                    }
                    tableClassMap = null;
                    return true;

                case K_COLUMN_LEVEL:
                    int level = Integer.parseInt(data.trim());
                    columnLevelMap.put(name, level);
                    return true;

                case K_JOIN_LEVEL:
                    int depth = Integer.parseInt(data.trim());
                    joinLevelMap.put(name, depth);
                    return true;

                default:
                    return false;
                }
            }
        };
    }

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        throw new NotImplementedException();
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.key(K_COLUMN_PROPERTY);
        out.map(columnPropertyMap);

        out.key(K_COLUMN_REF);
        out.map(columnRefMap.alias2QColumn);

        if (!keyColumnSettings.isEmpty()) {
            out.key(K_KEY_COLUMNS);
            out.object();
            keyColumnSettings.jsonOut(out, opts);
            out.endObject();
        }

        out.key(K_TABLE_NAME);
        out.map(tableNameMap);

        out.key(K_CLASS_MAP);
        out.map(class2TableList);

        out.key(K_COLUMN_LEVEL);
        out.map(columnLevelMap);

        out.key(K_JOIN_LEVEL);
        out.map(joinLevelMap);

        out.key(K_TABLES);
        out.map(tableMap);

        out.key(K_MIXINS);
        out.map(mixinMap);
    }

}
