package net.bodz.uni.catme;

public class FilterEntry {

    public String key;

    public ITextFilter filter;

    public FilterEntry(String key, ITextFilter filter) {
        this.key = key;
        this.filter = filter;
    }

}
