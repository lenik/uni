package com.lapiota.jaep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

public class NoteDB implements Serializable {

    private static final long   serialVersionUID = 2051478815405384547L;

    /** DATE MESSAGE */
    private TreeMap<Date, Note> tab;

    public NoteDB() {
        this.tab = new TreeMap<Date, Note>();
    }

    protected boolean textMatch(String s, String mode) {
        if (s.contains(mode))
            return true;
        return false;
    }

    public List<Date> search(String keyword) {
        List<Date> dates = new ArrayList<Date>();
        for (Entry<Date, Note> e : tab.entrySet()) {
            Date date = e.getKey();
            Note note = e.getValue();
            assert note != null : "null note, date=" + date;
            if (textMatch(note.getText(), keyword))
                dates.add(date);
        }
        return dates;
    }

    public TreeMap<Date, Note> getTab() {
        return tab;
    }

    public void addNote(String state, String text) {
        Date date = new Date();
        Note note;
        if (tab.containsKey(date)) {
            note = tab.get(date);
            note.setState(state);
            text = note.getText() + "\n" + text;
            note.setText(text);
        } else {
            note = new Note(state, text);
            tab.put(date, note);
        }
    }

}
