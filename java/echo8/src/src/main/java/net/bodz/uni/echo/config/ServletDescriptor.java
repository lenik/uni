package net.bodz.uni.echo.config;

import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

public class ServletDescriptor
        extends AbstractPluginDescriptor {

    Class<? extends Servlet> servletClass;
    List<String> mappings;
    Map<String, String> initParameterMap;

    public ServletDescriptor(Class<? extends Servlet> servletClass) {
        this(null, servletClass);
    }

    public ServletDescriptor(String id, Class<? extends Servlet> servletClass) {
        super(id);
        if (servletClass == null)
            throw new NullPointerException("servletClass");
        this.servletClass = servletClass;
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

    public void setInitParam(String key, String value) {
        initParameterMap.put(key, value);
    }

    public void removeInitParam(String key) {
        initParameterMap.remove(key);
    }

}
