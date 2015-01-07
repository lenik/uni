package net.bodz.uni.echo.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import net.bodz.mda.xjdoc.Xjdocs;
import net.bodz.mda.xjdoc.model.ClassDoc;

public class ServletDescriptor
        extends AbstractPluginDescriptor {

    Class<? extends Servlet> servletClass;
    List<String> mappings = new ArrayList<>();
    Map<String, String> initParameterMap = new HashMap<>();

    public ServletDescriptor(Class<? extends Servlet> servletClass) {
        this(null, servletClass);
    }

    public ServletDescriptor(String id, Class<? extends Servlet> servletClass) {
        super(id);
        if (servletClass == null)
            throw new NullPointerException("servletClass");
        this.servletClass = servletClass;

        ClassDoc classDoc = Xjdocs.getDefaultProvider().getClassDoc(servletClass);
        if (classDoc != null)
            // TODO Elements.copy(this, classDoc);
            setDisplayName(classDoc.getText().getHeadPar());
    }

    /**
     * Get the initialize order. Holders with order<0, are initialized on use. Those with order>=0
     * are initialized in increasing order when the handler is started.
     */
    @Override
    public int getPriority() {
        return super.getPriority();
    }

    /**
     * Set the initialize order. Holders with order<0, are initialized on use. Those with order>=0
     * are initialized in increasing order when the handler is started.
     */
    @Override
    public void setPriority(int priority) {
        super.setPriority(priority);
    }

    public Class<? extends Servlet> getServletClass() {
        return servletClass;
    }

    public List<String> getMappings() {
        return mappings;
    }

    public void addMapping(String mapping) {
        if (mapping == null)
            throw new NullPointerException("mapping");
        mappings.add(mapping);
    }

    public Map<String, String> getInitParamMap() {
        return initParameterMap;
    }

    public void setInitParam(String key, Object value) {
        if (key == null)
            throw new NullPointerException("key");
        if (value == null)
            throw new NullPointerException("value");
        initParameterMap.put(key, value.toString());
    }

    public void removeInitParam(String key) {
        initParameterMap.remove(key);
    }

    @Override
    public String toString() {
        return servletClass.getName();
    }

}
