package net.bodz.uni.site.model;

import org.junit.Assert;

import net.bodz.bas.potato.ref.UiPropertyRef;
import net.bodz.bas.potato.ref.UiPropertyRefMap;
import net.bodz.uni.site.model.Site;

public class SiteTest
        extends Assert {

    public static void main(String[] args) {
        Site site = new Site();
        UiPropertyRefMap map = UiPropertyRef.map(site);
        map.importProperties();
        System.out.println(map.keySet());
    }

}
