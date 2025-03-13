package net.bodz.lily.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.type.ClassNameComparator;
import net.bodz.bas.c.type.GenericActualClassInfo;
import net.bodz.bas.c.type.GenericTypes;
import net.bodz.bas.c.type.IndexedTypes;
import net.bodz.bas.db.ibatis.IEntityMapper;
import net.bodz.bas.db.ibatis.IMapper;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.potato.PotatoTypes;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.order.AbstractNonNullComparator;
import net.bodz.lily.concrete.CoCategory;
import net.bodz.lily.concrete.CoCode;
import net.bodz.lily.concrete.CoEvent;
import net.bodz.lily.concrete.CoMessage;
import net.bodz.lily.concrete.CoNode;
import net.bodz.lily.concrete.CoParameter;
import net.bodz.lily.concrete.CoPhase;
import net.bodz.lily.concrete.CoTag;
import net.bodz.lily.concrete.CoTalk;
import net.bodz.lily.concrete.VoteRecord;

/**
 * List mapped entity types.
 */
@ProgramName("ls-entity")
public class ListEntity
        extends BasicCLI {

    /**
     * Select entity classes within these packages.
     *
     * @option -p =NAME
     */
    List<String> packageNames = new ArrayList<>();

    /**
     * Order by entity class name.
     *
     * @option -c
     */
    boolean sortByClassName;

    /**
     * Order by table name.
     *
     * @option -t
     */
    boolean sortByTableName;

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        List<String> prefixes = new ArrayList<>();
        for (String p : packageNames)
            prefixes.add(p + ".");

        List<Item> list = new ArrayList<>();
        M: for (Class<?> mapperClass : IndexedTypes.list(IMapper.class, true)) {
            if (IEntityMapper.class.isAssignableFrom(mapperClass)) {
                GenericActualClassInfo typeInfo = GenericTypes.getActualInfo(mapperClass);
                if (typeInfo == null)
                    throw new NullPointerException("typeInfo");

                GenericActualClassInfo info = typeInfo.getUpward(IEntityMapper.class);
                if (info == null)
                    throw new NullPointerException("upward info for " + mapperClass);

                Class<?>[] bounds = info.getBounds();
                Class<?> entityType = bounds[0];

                for (String p : prefixes) {
                    if (! entityType.getName().startsWith(p))
                        continue M;
                }
                Item item = new Item(entityType);
                list.add(item);
            }
        }

        if (sortByClassName) {
            Collections.sort(list, new ClassNameOrder());
        }
        if (sortByTableName) {
            Collections.sort(list, new TableNameOrder());
        }

        ITreeOut out = Stdio.cout.indented();

        printClassMap(out, list);
        printTableNames(out, list);

    }

    Set<Class<?>> roots = new LinkedHashSet<>();
    {
        roots.add(CoCategory.class);
        roots.add(CoParameter.class);
        roots.add(CoPhase.class);
        roots.add(CoTag.class);
        roots.add(CoCode.class);
        roots.add(CoNode.class);

        roots.add(CoTalk.class);
        roots.add(CoMessage.class);
        roots.add(CoEvent.class);

        roots.add(VoteRecord.class);
        // roots.add(IdEntity.class);
    }

    void printClassMap(ITreeOut out, List<Item> list) {
        Map<Class<?>, List<Item>> map = new LinkedHashMap<>();
        for (Item item : list) {
            Class<?> javaClass = item.type.getJavaClass();
            if (javaClass == null)
                throw new NullPointerException("javaClass: " + item.type);
            for (Class<?> root : roots) {
                if (root.isAssignableFrom(javaClass)) {
                    List<Item> children = map.get(root);
                    if (children == null)
                        map.put(root, children = new ArrayList<>());
                    children.add(item);
                }
            }
        }

        out.println("class-map {");
        out.enter();

        for (Class<?> root : map.keySet()) {
            List<Item> children = map.get(root);
            out.println(root.getName() + ": \\");
            out.enter();
            int n = children.size();
            for (int i = 0; i < n; i++) {
                Item item = children.get(i);
                out.print(item.tableName);
                if (i != n - 1)
                    out.print(" \\");
                out.println();
            }
            out.leave();
        }
        out.leave();
        out.println("}");
    }

    void printTableNames(ITreeOut out, List<Item> list) {
        out.println("table-name {");
        out.enter();
        for (Item item : list) {
            String name = item.tableName;
            if (name == null) {
                name = item.type.getSimpleName();
                name = name.toLowerCase();
            }
            System.out.printf("%-20s%s\n", //
                    name + ":", //
                    item.type.getJavaClass().getCanonicalName());
        }
        out.leave();
        out.println("}");
    }

    public static void main(String[] args)
            throws Exception {
        new ListEntity().execute(args);
    }

    static class Item {
        String tableName;
        IType type;

        public Item(Class<?> clazz) {
            type = PotatoTypes.getInstance().getType(clazz);
            Table aTable = type.getAnnotation(Table.class);
            if (aTable != null)
                tableName = aTable.name();
        }

    }

    static ClassNameComparator classNameOrder = new ClassNameComparator();

    static class TableNameOrder
            extends AbstractNonNullComparator<Item> {

        @Override
        public int compareNonNull(Item o1, Item o2) {
            int cmp = Nullables.compare(o1.tableName, o2.tableName);
            if (cmp != 0)
                return cmp;
            Class<?> c1 = o1.type.getJavaClass();
            Class<?> c2 = o2.type.getJavaClass();
            cmp = classNameOrder.compare(c1.getClass(), c2.getClass());
            return cmp;
        }

    }

    static class ClassNameOrder
            extends AbstractNonNullComparator<Item> {

        @Override
        public int compareNonNull(Item o1, Item o2) {
            Class<?> c1 = o1.type.getJavaClass();
            Class<?> c2 = o2.type.getJavaClass();
            int cmp = classNameOrder.compare(c1.getClass(), c2.getClass());
            return cmp;
        }

    }

}
