package net.bodz.uni.echo.config;

import net.bodz.bas.c.object.UseNet;
import net.bodz.bas.util.order.AbstractNonNullComparator;

public class PluginDescriptorComparator
        extends AbstractNonNullComparator<IPluginDescriptor> {

    UseNet<IPluginDescriptor> depNet;

    public PluginDescriptorComparator(UseNet<IPluginDescriptor> depNet) {
        if (depNet == null)
            throw new NullPointerException("depNet");
        this.depNet = depNet;
    }

    @Override
    public int compareNonNull(IPluginDescriptor o1, IPluginDescriptor o2) {
        int priority1 = o1.getPriority();
        int priority2 = o2.getPriority();
        int cmp = priority1 - priority2;
        if (cmp != 0)
            return cmp;

        int index1 = o1.getIndex();
        int index2 = o2.getIndex();
        cmp = index1 - index2;
        if (cmp != 0)
            return cmp;

        // Set<IPluginConfig> prev2c = depNet.getPrevClosure(o2);
        // if (prev2c.contains(o1))
        // return -1;

        String id1 = o1.getId();
        String id2 = o2.getId();
        cmp = id1.compareTo(id2);
        return cmp;
    }

}
