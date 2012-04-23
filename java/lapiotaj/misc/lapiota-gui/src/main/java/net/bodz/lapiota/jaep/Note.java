package net.bodz.lapiota.jaep;

import java.io.Serializable;

public class Note
        implements Serializable {

    private static final long serialVersionUID = -68352391889824454L;

    private String state;
    private String text;

    public Note(String state, String text) {
        if (text == null)
            throw new NullPointerException("text");
        this.state = state;
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
