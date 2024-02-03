package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class DirConfig {

    static final Logger logger = LoggerFactory.getLogger(DirConfig.class);

    public ClassPathInfo modelPath;
    public ClassPathInfo daoPath;
    public ClassPathInfo wsPath;
    public ClassPathInfo webPath;

    public DirConfig(ClassPathInfo modelPath, ClassPathInfo daoPath, ClassPathInfo wsPath, ClassPathInfo webPath) {
        this.modelPath = modelPath;
        this.daoPath = daoPath;
        this.wsPath = wsPath;
        this.webPath = webPath;
    }

}
