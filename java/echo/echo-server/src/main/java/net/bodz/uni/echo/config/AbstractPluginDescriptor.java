package net.bodz.uni.echo.config;

import net.bodz.bas.c.object.IdentityHashSet;
import net.bodz.bas.c.object.ObjectInfo;

public abstract class AbstractPluginDescriptor
        implements IPluginDescriptor {

    String id;
    int index;
    int priority;
    String displayName;
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
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public IdentityHashSet<Object> getDependencies() {
        return dependencies;
    }

}
