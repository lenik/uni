package net.bodz.lily.tool.javagen.config;

import java.util.ArrayList;

import net.bodz.bas.err.IllegalUsageException;

public class NameDecoratorList
        extends ArrayList<INameDecorator> {

    private static final long serialVersionUID = 1L;

    public void addSuffix(String suffix) {
        if (suffix == null)
            throw new NullPointerException("suffix");
        SuffixDecorator decorator = new SuffixDecorator(suffix);
        add(decorator);
    }

    public String getPreferredDecoratedName(String name) {
        return getPreferredDecoratedName(name, IConflictNameResolver.NONE);
    }

    public String getPreferredDecoratedName(String name, IConflictNameResolver resolver) {
        for (INameDecorator d : this)
            if (d.isDecorated(name))
                return resolver.resolveConflictName(name);

        for (INameDecorator d : this)
            return d.decorate(name);

        return E_NoDecoratorDefined();
    }

    public boolean isDecorated(String name) {
        for (INameDecorator d : this)
            if (d.isDecorated(name))
                return true;
        return false;
    }

    public INameDecorator findDecorator(String name) {
        return findDecorator(name, null);
    }

    public INameDecorator findDecorator(String name, INameDecorator fallback) {
        for (INameDecorator d : this)
            if (d.isDecorated(name))
                return d;
        return fallback;
    }

    public INameDecorator findDecoratorOrDefault(String name) {
        INameDecorator _default = isEmpty() ? null : get(0);
        return findDecorator(name, _default);
    }

    private static <T> T E_NoDecoratorDefined() {
        throw new IllegalUsageException("no decorator defined.");
    }

    public static void main(String[] args) {
        NameDecoratorList ndl = new NameDecoratorList();
        ndl.getPreferredDecoratedName("hello");
    }

}
