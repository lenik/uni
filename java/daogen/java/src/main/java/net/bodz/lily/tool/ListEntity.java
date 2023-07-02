package net.bodz.lily.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Table;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.type.ClassNameComparator;
import net.bodz.bas.c.type.GenericActualClassInfo;
import net.bodz.bas.c.type.GenericTypes;
import net.bodz.bas.c.type.IndexedTypes;
import net.bodz.bas.db.ibatis.IEntityMapper;
import net.bodz.bas.db.ibatis.IMapper;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.potato.PotatoTypes;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.order.AbstractNonNullComparator;

/**
 * List mapped entity types.
 */
@ProgramName("ls-entity")
public class ListEntity
        extends BasicCLI {

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
        List<Item> list = new ArrayList<>();
        for (Class<?> mapperClass : IndexedTypes.list(IMapper.class, true)) {
            if (IEntityMapper.class.isAssignableFrom(mapperClass)) {
                GenericActualClassInfo typeInfo = GenericTypes.getActualInfo(mapperClass);
                if (typeInfo == null)
                    throw new NullPointerException("typeInfo");

                GenericActualClassInfo info = typeInfo.getUpward(IEntityMapper.class);
                if (info == null)
                    throw new NullPointerException("upward info for " + mapperClass);

                Class<?>[] bounds = info.getBounds();
                Class<?> entityType = bounds[0];
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

        for (Item item : list) {
            String name = item.name;
            if (name == null) {
                name = item.type.getSimpleName();
                name = name.toLowerCase();
            }
            System.out.printf("%-20s%s\n", //
                    name + ":", //
                    item.type.getJavaClass().getCanonicalName());
        }
    }

    public static void main(String[] args)
            throws Exception {
        new ListEntity().execute(args);
    }

    static class Item {
        String name;
        IType type;

        public Item(Class<?> clazz) {
            type = PotatoTypes.getInstance().getType(clazz);
            Table aTable = type.getAnnotation(Table.class);
            if (aTable != null)
                name = aTable.name();
        }

    }

    static ClassNameComparator classNameOrder = new ClassNameComparator();

    static class TableNameOrder
            extends AbstractNonNullComparator<Item> {

        @Override
        public int compareNonNull(Item o1, Item o2) {
            int cmp = Nullables.compare(o1.name, o2.name);
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
