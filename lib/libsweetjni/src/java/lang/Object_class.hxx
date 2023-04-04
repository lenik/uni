/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_OBJECT_CLASS_H
#define __JAVA_LANG_OBJECT_CLASS_H

#include <jni.h>

namespace java::lang {

class Object_class {
public:
    Object_class();
    void dump();

public:
    jclass _class;

    jmethod INIT;

    jmethod METHOD_equals;
    jmethod METHOD_getClass;
    jmethod METHOD_hashCode;
    jmethod METHOD_notify;
    jmethod METHOD_notifyAll;
    jmethod METHOD_toString;
    jmethod METHOD_wait_1;
    jmethod METHOD_wait_2;
    jmethod METHOD_wait;
}; // class Object_class

} // namespace

#endif
