package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.t.order.IPriority;

public class EditElement
        implements
            IPriority {

    int priority;
    String path;
    String name;
    String displayName;

    public static EditElement fromStatusEntry(StatusEntry entry) {
        EditElement ee = new EditElement();
        return ee;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
