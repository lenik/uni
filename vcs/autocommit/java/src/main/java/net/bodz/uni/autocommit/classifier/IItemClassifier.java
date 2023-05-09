package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.meta.codegen.IndexedType;

@IndexedType
public interface IItemClassifier {

    boolean classify(Changeset set, StatusEntry entry);

}
