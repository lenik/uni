package net.bodz.uni.catme;

import org.graalvm.polyglot.HostAccess.Export;

public class Command {

    String name;
    boolean greedy;

    @Export
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Export
    public boolean isGreedy() {
        return greedy;
    }

    public void setGreedy(boolean greedy) {
        this.greedy = greedy;
    }

}
