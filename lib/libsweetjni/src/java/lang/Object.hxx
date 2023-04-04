/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_OBJECT_H
#define __JAVA_LANG_OBJECT_H

#include <jni.h>
#include <sweetjni.hxx>

#include "Object_class.hxx"

namespace java::lang {

class Object : public IWrapper {
public:
    JNIEnv *_env;
    jobject _jobj;

public:
    /* wrapper constructor */
    Object(JNIEnv *env);
    Object(JNIEnv *env, jobject jobj);
    static Object *_wrap(jobject jobj);

    inline operator JNIEnv *() const { return _env; }
    inline operator jobject() const { return _jobj; }

public:
    /* ctor-create methods */
    Object();

public:
    /* method wrappers */
    jboolean equals(jobject arg0);
    jclass getClass();
    jint hashCode();
    void notify();
    void notifyAll();
    jstring toString();
    void wait_1(jlong arg0);
    void wait_2(jlong arg0, jint arg1);
    void wait();

public:
    static thread_local Object_class CLASS;
}; // class Object

} // namespace

#endif
