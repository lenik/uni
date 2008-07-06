package net.bodz.lapiota.eclipse.jdt;

import java.util.List;

public class Iter {

    <E> void f(List<E> list, E... args) {
        for (E element : list) {
            System.out.println(element);
        }
        for (E arg : args)
            System.out.println((E) arg);
    }

}
