package net.bodz.lapiota.jaep;

public class Project {

    public static final String RUNNING = "running";
    public static final String PAUSED = "paused";
    public static final String DONE = "done";
    public static final String CANCELED = "canceled";

    private QName name;
    private String state;
    private NoteDB notes;

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public void setName(String qName) {
        this.name = new QName(qName);
    }

}
