package net.bodz.lily.tool.daogen.reflect;

public class MaskFieldModel {

    MaskClassModel parent;
    public String name;

    public boolean hasMain;
    public boolean hasRange;
    public boolean hasPattern;

    public MaskFieldModel(String name) {
        this.name = name;
    }

}
