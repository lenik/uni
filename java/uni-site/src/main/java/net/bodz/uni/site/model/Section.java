package net.bodz.uni.site.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import net.bodz.bas.i18n.dom1.MutableElement;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;

public class Section
        extends MutableElement
        implements IPathDispatchable {

    Map<String, Project> projectMap;

    public Section(String name) {
        setName(name);
        projectMap = new TreeMap<>();
    }

    public Collection<Project> getProjects() {
        return projectMap.values();
    }

    public void addProject(Project project) {
        if (project == null)
            throw new NullPointerException("project");
        String name = project.getName();
        projectMap.put(name, project);
    }

    @Override
    public IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens)
            throws PathDispatchException {
        String token = tokens.peek();
        Project project = projectMap.get(token);
        if (project == null)
            return null;
        else
            return PathArrival.shift(previous, project, tokens);
    }

}
