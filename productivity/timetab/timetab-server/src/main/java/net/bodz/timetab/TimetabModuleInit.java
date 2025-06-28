package net.bodz.timetab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;

import net.bodz.bas.log.log4j.ILog4jConfigurer;

public class TimetabModuleInit
        implements
            ILog4jConfigurer {

    @Override
    public void setupBuilder(ConfigurationBuilder<? extends Configuration> builder) {

        // mybatis 使用 mapper class 对应的 logger 进行 jdbc logging。
        builder.add(builder.newLogger("net.bodz.timetab", Level.DEBUG));

        builder.add(builder.newLogger("net.bodz", Level.INFO));
        // builder.add(builder.newLogger("org.postgresql", Level.DEBUG));

        // p6 logging
        builder.add(builder.newLogger("p6spy", Level.INFO));
        // builder.add(builder.newLogger("org.apache", Level.INFO));
    }

}
