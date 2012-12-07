package net.bodz.uni.echo.config;

import net.bodz.bas.c.object.IdentityHashSet;
import net.bodz.bas.c.object.ObjectInfo;
import net.bodz.mda.xjdoc.model1.ArtifactObject;

public abstract class AbstractPluginDescriptor
        extends ArtifactObject
        implements IPluginDescriptor {

    String id;
    int priority;
    int index;
    IdentityHashSet<Object> dependencies;

    public AbstractPluginDescriptor() {
        this(null);
    }

    public AbstractPluginDescriptor(String id) {
        this.id = id != null ? id : ObjectInfo.getSimpleId(this);
        this.dependencies = new IdentityHashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public IdentityHashSet<Object> getDependencies() {
        return dependencies;
    }

}
