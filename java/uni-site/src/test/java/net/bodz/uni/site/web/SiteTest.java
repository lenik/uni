package net.bodz.uni.site.web;

import org.junit.Assert;

import net.bodz.bas.potato.ref.PropertyGUIRefEntry;
import net.bodz.bas.potato.ref.PropertyGUIRefMap;
import net.bodz.uni.site.model.Site;

public class SiteTest
        extends Assert {

    public static void main(String[] args) {
        Site site = new Site();
        PropertyGUIRefMap map = PropertyGUIRefEntry.map(site);
        map.importProperties();
        System.out.println(map.keySet());
    }

}
