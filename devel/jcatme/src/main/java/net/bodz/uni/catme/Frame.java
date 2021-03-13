package net.bodz.uni.catme;

import java.util.List;
import java.util.Map;

public class Frame {

    int echo;
    int skip;

    Map<String, Object> vars;
    List<Object> filters;

    public String filter(String s) {
        return s;
    }

}
