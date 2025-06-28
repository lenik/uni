package net.bodz.timetab.web;

import net.bodz.bas.site.DefaultSiteDirs;

public class TimetabDirs
        extends DefaultSiteDirs {

    public TimetabDirs() {
    }

    static TimetabDirs instance = new TimetabDirs();

    public static TimetabDirs getInstance() {
        return instance;
    }

}
