package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;

public class DirConfig {

    public ClassPathInfo modelPath;
    public ClassPathInfo daoPath;
    public ClassPathInfo wsPath;
    public ClassPathInfo htmlPath;

    public ClassPathInfo modelTestPath;
    public ClassPathInfo daoTestPath;
    public ClassPathInfo wsTestPath;
    public ClassPathInfo htmlTestPath;

    public DirConfig(ClassPathInfo modelPath, ClassPathInfo daoPath, ClassPathInfo wsPath, ClassPathInfo htmlPath) {
        this.modelPath = modelPath;
        this.daoPath = daoPath;
        this.wsPath = wsPath;
        this.htmlPath = htmlPath;

        this.modelTestPath = modelPath.toSrcTest();
        this.daoTestPath = daoPath.toSrcTest();
        this.wsTestPath = wsPath.toSrcTest();
        this.htmlTestPath = htmlPath.toSrcTest();
    }

}
