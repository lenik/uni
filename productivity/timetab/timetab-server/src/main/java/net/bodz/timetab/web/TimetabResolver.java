package net.bodz.timetab.web;

import net.bodz.lily.app.IDataApplication;
import net.bodz.lily.site.DataAppSite;
import net.bodz.lily.site.StaticDataHostResolver;

public class TimetabResolver
        extends StaticDataHostResolver {

    @Override
    protected DataAppSite buildRoot(IDataApplication dataApp) {
        return new TimetabSite(dataApp);
    }

}
