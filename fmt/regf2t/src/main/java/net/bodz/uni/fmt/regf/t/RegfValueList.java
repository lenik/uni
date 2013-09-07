package net.bodz.uni.fmt.regf.t;

public class RegfValueList {

    /**
     * Actual number of values referenced by this list.
     *
     * May differ from parent key's num_values if there were parsing errors.
     */
    int numValues;

    int /* RegfValueListElem */[] elements;

}
