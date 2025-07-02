/** JNIGEN GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_ARRAY_LIST_CLASS_H
#define __JAVA_UTIL_ARRAY_LIST_CLASS_H

#include <jni.h>
#include <sweetjni.hxx>

#include <java/lang/Object_class.hxx>

namespace java::util {

class ArrayList_class : public java::lang::Object_class {
public:
    ArrayList_class();
    void dump();

public:
    jclass _class;

    jmethod INIT_Collection;
    jmethod INIT;
    jmethod INIT_int;

    jmethod METHOD_add_arg0_arg1;
    jmethod METHOD_add_arg0;
    jmethod METHOD_addAll_arg0_arg1;
    jmethod METHOD_addAll_arg0;
    jmethod METHOD_addFirst;
    jmethod METHOD_addLast;
    jmethod METHOD_clear;
    jmethod METHOD_clone;
    jmethod METHOD_contains;
    jmethod METHOD_containsAll;
    jmethod METHOD_ensureCapacity;
    jmethod METHOD_equals;
    jmethod METHOD_forEach;
    jmethod METHOD_get;
    jmethod METHOD_getFirst;
    jmethod METHOD_getLast;
    jmethod METHOD_hashCode;
    jmethod METHOD_indexOf;
    jmethod METHOD_isEmpty;
    jmethod METHOD_iterator;
    jmethod METHOD_lastIndexOf;
    jmethod METHOD_listIterator_arg0;
    jmethod METHOD_listIterator;
    jmethod METHOD_parallelStream;
    jmethod METHOD_remove_Object;
    jmethod METHOD_remove_int;
    jmethod METHOD_removeAll;
    jmethod METHOD_removeFirst;
    jmethod METHOD_removeIf;
    jmethod METHOD_removeLast;
    jmethod METHOD_replaceAll;
    jmethod METHOD_retainAll;
    jmethod METHOD_set;
    jmethod METHOD_size;
    jmethod METHOD_sort;
    jmethod METHOD_spliterator;
    jmethod METHOD_stream;
    jmethod METHOD_subList;
    jmethod METHOD_toArray;
    jmethod METHOD_toArray_ObjectArray;
    jmethod METHOD_toArray_IntFunction;
    jmethod METHOD_toString;
    jmethod METHOD_trimToSize;
}; // class ArrayList_class

} // namespace

#endif
