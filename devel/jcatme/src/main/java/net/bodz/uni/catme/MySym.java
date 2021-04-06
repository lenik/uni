package net.bodz.uni.catme;

public class MySym {

    public static final int OPENER = 1;
    public static final int CLOSER = 2;
    public static final int SIMPLE_OPENER = 3;
    public static final int ESCAPE = 4;
    public static final int NEWLINE = 5;

    public int id;
    public String name;

    public MySym(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + "(" + name + ")";
    }

}
