package net.bodz.uni.site.web;

import org.junit.Assert;

import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.impl.TreeOutImpl;
import net.bodz.bas.site.org.SiteGraph;
import net.bodz.bas.site.org.SiteGraphDotBuilder;
import net.bodz.bas.site.org.Sitemap;
import net.bodz.bas.site.org.SitemapEntry;
import net.bodz.uni.site.DefaultUniSite;
import net.bodz.uni.site.UniSite;

public class UniSiteTest
        extends Assert {

    UniSite site = new DefaultUniSite().getTarget();

    public void dumpSitemap() {
        Sitemap sitemap = site.getSitemap();
        for (SitemapEntry entry : sitemap) {
            System.out.printf("%s\n", entry.getUrl());
        }
    }

    public void dumpSiteGraph() {
        ITreeOut out = TreeOutImpl.from(Stdio.cout);
        SiteGraphDotBuilder dotBuilder = new SiteGraphDotBuilder(out);

        SiteGraph graph = site.getSiteGraph();
        graph.setName("Start");
        dotBuilder.buildGraph(graph);
    }

    public static void main(String[] args) {
        new UniSiteTest().dumpSiteGraph();
    }

}
