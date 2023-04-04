/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_THROWABLE_H
#define __JAVA_LANG_THROWABLE_H

#include <jni.h>
#include <sweetjni.hxx>

#include <java/lang/Object.hxx>

#include "Throwable_class.hxx"

namespace java::lang {

class Throwable : public java::lang::Object {
public: 
    /* wrapper constructor */
    Throwable(JNIEnv *env);
    Throwable(JNIEnv *env, jobject jobj);
    static Throwable *_wrap(jobject jobj);

    inline operator jthrowable() { return (jthrowable) _jobj; }
public: 
    /* ctor-create methods */
    Throwable(jthrowable arg0);
    Throwable(jstring arg0, jthrowable arg1);
    Throwable(jstring arg0);
    Throwable();

public: 
    /* method wrappers */
    void addSuppressed(jthrowable arg0);
    jthrowable fillInStackTrace();
    jthrowable getCause();
    jstring getLocalizedMessage();
    jstring getMessage();
    jarray getStackTrace();
    jarray getSuppressed();
    jthrowable initCause(jthrowable arg0);
    void printStackTrace_PrintWriter(jobject arg0);
    void printStackTrace();
    void printStackTrace_PrintStream(jobject arg0);
    void setStackTrace(jarray arg0);
    jstring toString();

public: 
    static thread_local Throwable_class CLASS;
}; // class Throwable

} // namespace

#endif
