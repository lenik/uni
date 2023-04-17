package net.bodz.lily.tool.javagen.config;

public interface INameDecorator {

    boolean isDecorated(String s);

    String decorate(String s);

    String undecorate(String s);

}
