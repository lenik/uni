/** JNIGEN GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_HASH_SET_CLASS_H
#define __JAVA_UTIL_HASH_SET_CLASS_H

#include <jni.h>
#include <sweetjni.hxx>

#include <java/lang/Object_class.hxx>

namespace java::util {

class HashSet_class : public java::lang::Object_class {
public:
    HashSet_class();
    void dump();

public:
    jclass _class;

    jmethod INIT_int;
    jmethod INIT_int_float;
    jmethod INIT_Collection;
    jmethod INIT;

    jmethod METHOD_add;
    jmethod METHOD_addAll;
    jmethod METHOD_clear;
    jmethod METHOD_clone;
    jmethod METHOD_contains;
    jmethod METHOD_containsAll;
    jmethod METHOD_equals;
    jmethod METHOD_forEach;
    jmethod METHOD_hashCode;
    jmethod METHOD_isEmpty;
    jmethod METHOD_iterator;
    jmethod METHOD_newHashSet;
    jmethod METHOD_parallelStream;
    jmethod METHOD_remove;
    jmethod METHOD_removeAll;
    jmethod METHOD_removeIf;
    jmethod METHOD_retainAll;
    jmethod METHOD_size;
    jmethod METHOD_spliterator;
    jmethod METHOD_stream;
    jmethod METHOD_toArray_ObjectArray;
    jmethod METHOD_toArray;
    jmethod METHOD_toArray_IntFunction;
    jmethod METHOD_toString;
}; // class HashSet_class

} // namespace

#endif
