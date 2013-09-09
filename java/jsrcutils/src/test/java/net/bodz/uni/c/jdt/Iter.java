package net.bodz.uni.c.jdt;

import java.util.List;

public class Iter {

    <E> void f(List<E> list, @SuppressWarnings("unchecked") E... args) {
        for (E element : list) {
            System.out.println(element);
        }
        for (E arg : args)
            System.out.println(arg);
    }

}
