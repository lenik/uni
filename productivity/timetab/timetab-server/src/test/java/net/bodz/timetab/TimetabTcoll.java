package net.bodz.timetab;

import java.util.List;

import net.bodz.bas.codegen.java.TypeCollectorApp;

public class TimetabTcoll
        extends TypeCollectorApp {

    @Override
    protected void buildPackageList(List<String> packages) {
        packages.add("net.bodz.timetab");
    }

    public static void main(String[] args)
            throws Exception {
        new TimetabTcoll().execute(args);
    }

}
