package net.bodz.lily.tool.javagen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.bodz.bas.t.catalog.IColumnMetadata;

public interface IColumnOrder
        extends
            Comparator<IColumnMetadata> {

    SeqByPass getOrder(IColumnMetadata column);

    @Override
    default int compare(IColumnMetadata o1, IColumnMetadata o2) {
        if (o1 == o2)
            return 0;
        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;
        SeqByPass s1 = getOrder(o1);
        SeqByPass s2 = getOrder(o2);
        int cmp = SeqByPassComparator.INSTANCE.compare(s1, s2);
        if (cmp != 0)
            return cmp;
        return -1;
    }

    default List<IColumnMetadata> reorder(List<IColumnMetadata> columns, int maxPass) {
        List<IColumnMetadata> selection = new ArrayList<>();
        for (IColumnMetadata column : columns) {
            SeqByPass s = getOrder(column);
            if (s.pass > maxPass)
                continue;
            selection.add(column);
        }
        Collections.sort(selection, this);
        return selection;
    }

}
