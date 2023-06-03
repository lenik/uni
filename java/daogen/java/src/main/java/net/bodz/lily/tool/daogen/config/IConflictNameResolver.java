package net.bodz.lily.tool.daogen.config;

@FunctionalInterface
public interface IConflictNameResolver {

    String resolveConflictName(String name);

    IConflictNameResolver NONE = (String name) -> name;

}
