package net.bodz.uni.site;

import org.junit.Assert;

import net.bodz.bas.potato.ref.PropertyGUIRefEntry;
import net.bodz.bas.potato.ref.PropertyGUIRefMap;

public class UniSiteTest
        extends Assert {

    public static void main(String[] args) {
        UniSite site = new UniSite();
        PropertyGUIRefMap map = PropertyGUIRefEntry.map(site);
        map.importProperties();
        System.out.println(map.keySet());
    }

}
