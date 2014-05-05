package net.bodz.uni.site.model;

import net.bodz.bas.i18n.dom1.MutableElement;

public class Project
        extends MutableElement {

    ProjectStat projectStatus;

    public Project(String name) {
        setName(name);

        projectStatus = new ProjectStat();
    }

    public ProjectStat getStat() {
        return projectStatus;
    }

}
