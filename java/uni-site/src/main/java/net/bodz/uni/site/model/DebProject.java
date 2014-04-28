package net.bodz.uni.site.model;

import java.util.Map;

import net.bodz.bas.i18n.dom.iString;
import net.bodz.uni.site.util.DebControl;

public class DebProject
        extends Project {

    private DebControl debControl;

    public DebProject(String name) {
        super(name);
    }

    public DebControl getDebControl() {
        return debControl;
    }

    public void setDebControl(DebControl debControl) {
        this.debControl = debControl;
    }

    protected Map<String, String> getInfo() {
        Map<String, String> pkg1 = getDebControl().getFirstPackage();
        return pkg1;
    }

    @Override
    public iString getDescription() {
        String description = getInfo().get("Description");
        return iString.fn.val(description);
    }

    public String getArchitecture() {
        String arch = getInfo().get("Architecture");
        return arch;
    }

}
