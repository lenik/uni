package net.bodz.uni.catme.trie;

public class Msym {

    public int id;
    public String name;

    public boolean comment;
    public boolean end;

    public Msym(int id, String name) {
        this.id = id;
        this.name = name;
        this.comment = name.contains("comment");
        this.end = name.endsWith("end");
    }

    @Override
    public String toString() {
        return id + "(" + name + ")";
    }

}
