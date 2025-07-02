/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_SET_H
#define __JAVA_UTIL_SET_H

#include <jni.h>
#include <sweetjni.hxx>


#include "Set_class.hxx"

namespace java::util {

class Set : public IWrapper {
    JNIEnv *_env;
    jobject _jobj;

public: 
    /* wrapper constructor */
    Set(JNIEnv *env);
    Set(JNIEnv *env, jobject jobj);
    static Set *_wrap(jobject jobj);

    inline operator JNIEnv *() const { return _env; }
    inline operator jobject() const { return _jobj; }

public: 
    /* method wrappers */
    jboolean add(jobject arg0);
    jboolean addAll(jobject arg0);
    void clear();
    jboolean contains(jobject arg0);
    jboolean containsAll(jobject arg0);
    static jobject copyOf(jobject arg0);
    jboolean equals(jobject arg0);
    void forEach(jobject arg0);
    jint hashCode();
    jboolean isEmpty();
    jobject iterator();
    static jobject of_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5);
    static jobject of_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4);
    static jobject of_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3);
    static jobject of();
    static jobject of_Object_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8, jobject arg9);
    static jobject of_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8);
    static jobject of_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7);
    static jobject of_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6);
    static jobject of_ObjectArray(jobjectArray arg0);
    static jobject of_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2);
    static jobject of_Object(jobject arg0);
    static jobject of_Object_Object(jobject arg0, jobject arg1);
    jobject parallelStream();
    jboolean remove(jobject arg0);
    jboolean removeAll(jobject arg0);
    jboolean removeIf(jobject arg0);
    jboolean retainAll(jobject arg0);
    jint size();
    jobject spliterator();
    jobject stream();
    jobjectArray toArray();
    jobjectArray toArray_ObjectArray(jobjectArray arg0);
    jobjectArray toArray_IntFunction(jobject arg0);

public: 
    static thread_local Set_class CLASS;
}; // class Set

} // namespace

#endif
