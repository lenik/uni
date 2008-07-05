package net.bodz.lapiota.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings( { "unchecked", "unused" })
public class TPTest<T extends Object & Serializable> {

    T test() {
        T var = null, tmp;
        tmp = var;
        Serializable out = var;
        var = (T) out;
        {
            T tmp2;
        }
        return var;
    }

    class C<G extends Collection> {
        G<?> gelm;

        class D<H extends G> {

        }
    }

    class Inner {
        void test(String arg) {
            TPTest.this.test();
        }
    }

    void f() {
        List<String> list = new ArrayList<String>();
        String s = list.get(0);
    }

}
