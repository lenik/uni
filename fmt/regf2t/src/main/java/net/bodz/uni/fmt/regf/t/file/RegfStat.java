package net.bodz.uni.fmt.regf.t.file;

import java.util.ArrayList;
import java.util.List;

import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.cell.BigDataCell;
import net.bodz.uni.fmt.regf.t.cell.KeyCell;
import net.bodz.uni.fmt.regf.t.cell.SubkeyElement;
import net.bodz.uni.fmt.regf.t.cell.SubkeyListCell;
import net.bodz.uni.fmt.regf.t.cell.ValueCell;

public class RegfStat
        implements IRegfConsts {

    public transient List<KeyCell> keys = new ArrayList<>();
    public transient List<ValueCell> values = new ArrayList<>();
    public transient List<SubkeyListCell> subkeyLists = new ArrayList<>();
    public transient List<BigDataCell> bigDatas = new ArrayList<>();

    public void test() {
        int eq = 0;
        int neq = 0;
        int z = 0;
        for (SubkeyListCell subkeyList : subkeyLists) {
            for (SubkeyElement element : subkeyList.elements) {
                switch (subkeyList.magic) {
                case MAGIC_LF:
                    KeyCell target1 = (KeyCell) element.target;
                    if (element.hash == 0)
                        z++;
                    if (element.hash != target1.hashLF) {
                        System.out.println("XXX " + target1.keyName //
                                + " Expect: " + element.hash + ", Computed: " + target1.hashLF);
                        neq++;
                    } else {
                        eq++;
                    }
                    break;

                case MAGIC_LH:
                    KeyCell target2 = (KeyCell) element.target;
                    if (element.hash == 0)
                        z++;
                    if (element.hash != target2.hashLH) {
                        System.out.println("XXX " + target2.keyName //
                                + " Expect: " + element.hash + ", Computed: " + target2.hashLH);
                        neq++;
                    } else {
                        eq++;
                    }
                }
            }
        }
        return;
    }

    private static RegfStat instance = new RegfStat();

    public static RegfStat getInstance() {
        return instance;
    }

}
