package net.bodz.uni.typedet.type;

import org.junit.Test;

import net.bodz.bas.stat.distrib.LRUHistoMap;

public class LRUHistoMapTest {

    @Test
    public void test() {
    }

    public static void main(String[] args) {
        LRUHistoMap<String> histo = new LRUHistoMap<String>(10);
        for (int k = 0; k < 10; k++) {
            char ch = (char) ('n' + k);
            histo.add("" + ch);
            histo.dump();
        }
        for (int k = 0; k < 3; k++) {
            histo.add("W");
            histo.dump();
        }
        for (int k = 5; k < 10; k++) {
            char ch = (char) ('n' + k);
            histo.add("" + ch);
            histo.dump();
        }
        for (int k = 0; k < 10; k++) {
            char ch = (char) ('a' + k);
            histo.add("" + ch);
            histo.dump();
        }
        for (int k = 8; k < 12; k++) {
            char ch = (char) ('a' + k);
            histo.add("" + ch);
            histo.dump();
        }
        for (int k = 8; k < 13; k++) {
            char ch = (char) ('a' + k);
            histo.add("" + ch);
            histo.dump();
        }
        for (int k = 8; k < 15; k++) {
            char ch = (char) ('a' + k);
            histo.add("" + ch);
            histo.dump();
        }
    }

}
