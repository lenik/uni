package net.bodz.timetab.web;

import java.util.Locale;

import net.bodz.bas.httpd.BasHttpd;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.servlet.config.ServletContextConfig;
import net.bodz.bas.xml.dom.XmlFormatter;
import net.bodz.web.PortAlloc;

@ProgramName("timetabd")
public class Timetabd
        extends BasHttpd {

    public static final int PORT = PortAlloc.TIMETAB;

    @Override
    protected void initSystemGlobals()
            throws Exception {
        XmlFormatter.NULL_VERBATIM = "<i class='fa fa-times zu-null'></i>";
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
    }

    @Override
    protected ServletContextConfig createConfig() {
        return new TimetabConfig();
    }

    @Override
    protected void configure(ServletContextConfig config) {
        config.setPortNumber(PORT);
    }

    public static void main(String[] args)
            throws Exception {
        new Timetabd().execute(args);
    }

}
