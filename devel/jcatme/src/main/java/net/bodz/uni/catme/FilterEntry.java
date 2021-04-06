package net.bodz.uni.catme;

public class FilterEntry {

    public String name;
    public ITextFilter filter;

    public FilterEntry(String name, ITextFilter filter) {
        this.name = name;
        this.filter = filter;
    }

}
