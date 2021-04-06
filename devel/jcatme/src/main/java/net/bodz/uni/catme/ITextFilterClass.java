package net.bodz.uni.catme;

import java.util.List;

public interface ITextFilterClass {

    boolean isScript();

    ITextFilter createFilter(IFrame frame, List<String> args);

}
