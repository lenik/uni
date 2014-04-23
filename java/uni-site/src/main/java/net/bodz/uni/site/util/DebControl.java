package net.bodz.uni.site.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.bodz.bas.io.ITreeOut;

public class DebControl {

    Map<String, String> sourceMap;
    Map<String, Map<String, String>> packageMapMap;

    public DebControl() {
        packageMapMap = new HashMap<>();
    }

    public Map<String, String> getSource() {
        return sourceMap;
    }

    public void setSource(Map<String, String> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public Set<String> getPackageNames() {
        return packageMapMap.keySet();
    }

    public Map<String, String> getPackage(String packageName) {
        return packageMapMap.get(packageName);
    }

    public void addPackage(String packageName, Map<String, String> properties) {
        packageMapMap.put(packageName, properties);
    }

    public void removePackage(String packageName) {
        packageMapMap.remove(packageName);
    }

    public Map<String, String> getFirstPackage() {
        Collection<Map<String, String>> packages = packageMapMap.values();
        if (packages.isEmpty())
            return null;
        else
            return packages.iterator().next();
    }

    public void dump(ITreeOut out) {
        out.println("== Source ==");
        out.enter();
        dump(out, sourceMap);
        out.leave();
        out.println();

        for (Map<String, String> packageMap : packageMapMap.values()) {
            String packageName = packageMap.get("Package");
            out.println("== Package: " + packageName + " ==");
            out.enter();
            dump(out, packageMap);
            out.leave();
            out.println();
        }
    }

    void dump(ITreeOut out, Map<String, String> properties) {
        for (Entry<?, ?> entry : properties.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            out.println(key + ": " + value);
        }
    }

}
