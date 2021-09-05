package net.bodz.uni.catme;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerRepository;

import net.bodz.bas.log.log4j.ILog4jConfigurer;

public class CatMeModuleInit
        implements
            ILog4jConfigurer {

    @Override
    public void initLog4j(LoggerRepository hierarchy) {
        hierarchy.getLogger("net.bodz").setLevel(Level.INFO);
        hierarchy.getLogger("net.bodz.uni").setLevel(Level.INFO);
        hierarchy.getLogger("user").setLevel(Level.DEBUG);
    }

}
