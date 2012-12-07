package net.bodz.bas.html;

import java.util.List;
import java.util.Set;

/**
 * Multi-selection model.
 */
public interface IMultiSelectionModel<T> {

    Set<T> getSelection();

    List<T> getCandidates();

    Set<Integer> getSelectedIndexes();

    void setSelectedIndexes(Set<Integer> selectedIndexes);

}
