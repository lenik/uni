/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_THROWABLE_CLASS_H
#define __JAVA_LANG_THROWABLE_CLASS_H

#include <jni.h>

#include <java/lang/Object_class.hxx>

namespace java::lang {

class Throwable_class : public java::lang::Object_class {
public:
    Throwable_class();
    void dump();

public:
    jclass _class;

    jmethod INIT_Throwable;
    jmethod INIT_String_Throwable;
    jmethod INIT_String;
    jmethod INIT;

    jmethod METHOD_addSuppressed;
    jmethod METHOD_fillInStackTrace;
    jmethod METHOD_getCause;
    jmethod METHOD_getLocalizedMessage;
    jmethod METHOD_getMessage;
    jmethod METHOD_getStackTrace;
    jmethod METHOD_getSuppressed;
    jmethod METHOD_initCause;
    jmethod METHOD_printStackTrace_PrintWriter;
    jmethod METHOD_printStackTrace;
    jmethod METHOD_printStackTrace_PrintStream;
    jmethod METHOD_setStackTrace;
    jmethod METHOD_toString;
}; // class Throwable_class

} // namespace

#endif
