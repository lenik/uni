package net.bodz.uni.catme;

public class MySym {

    /** \* */
    public static final int ID_OPENER = 1;
    /** *\ */
    public static final int ID_CLOSER = 2;
    /** -- */
    public static final int ID_SL_OPENER = 3;
    /** \ */
    public static final int ID_ESCAPE = 4;
    /** \n */
    public static final int ID_NEWLINE = 5;

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

    public static final MySym OPENER = new MySym(MySym.ID_OPENER, "opener");
    public static final MySym SL_OPENER = new MySym(MySym.ID_SL_OPENER, "simpleOpener");
    public static final MySym NEWLINE = new MySym(MySym.ID_NEWLINE, "newLine");
    public static final MySym CLOSER = new MySym(MySym.ID_CLOSER, "closer");
    public static final MySym ESCAPE = new MySym(MySym.ID_ESCAPE, "escape");

}
