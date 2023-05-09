package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.t.order.IPriority;

class EditTypeConsts {

    static final int PR_RENAME = 0;
    static final int PR_ADD = 1;
    static final int PR_REMOVE = 2;
    static final int PR_EDIT = 3;

}

public enum EditType
        implements
            IPriority {

    ADD(EditTypeConsts.PR_ADD, "add"),

    EDIT(EditTypeConsts.PR_EDIT, "edit"),

    REMOVE(EditTypeConsts.PR_REMOVE, "del"),

    RENAME(EditTypeConsts.PR_RENAME, "move"),

    ;

    public final String display;
    private int priority;

    private EditType(int priority, String display) {
        this.priority = priority;
        this.display = display;
    }

    @Override
    public int getPriority() {
        return priority;
    }

}
