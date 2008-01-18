package net.bodz.lapiota.util;

import java.util.Map;

import org.junit.Test;

public class RcsTest {

    @Test
    public void test1() {
        Map<String, Object> id = Rcs.parseId(Rcs.class);
        for (String key : id.keySet()) {
            Object val = id.get(key);
            System.out.println(key + " = " + val);
        }
    }

}
