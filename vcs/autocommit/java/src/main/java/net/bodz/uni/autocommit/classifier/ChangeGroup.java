package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.t.order.IPriority;

public class ChangeGroup
        implements
            IPriority {

    int priority;
    String name;
    String displayName;

    @Override
    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
