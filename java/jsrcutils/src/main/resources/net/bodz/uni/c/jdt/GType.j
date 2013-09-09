package net.bodz.uni.eclipse.jdt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings( { "unchecked", "unused" })
public class GType<T extends Object & Serializable> {

    T test() {
        T var = null, tmp;
        tmp = var;
        assert (T) var != (T) tmp : "unexpected...<" + (T) var + ">";
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
            H helm;

            G<?> dfun() {
                return gelm = helm;
            }
        }
    }

    class Inner {
        void test(String arg) {
            GType.this.test();
        }
    }

    void f() {
        List<String> list = new ArrayList<String>();
        String s = list.get(0);
    }

    <E> void f(List<E> list, E... args) {
        for (E e : (List<E>) list) {
            E f = (E) e;
        }
        E g;
        for (final E e : (List<E>) list)
            g = (E) e;
    }

}
