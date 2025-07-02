/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_LIST_H
#define __JAVA_UTIL_LIST_H

#include <jni.h>
#include <sweetjni.hxx>


#include "List_class.hxx"

namespace java::util {

class List : public IWrapper {
    JNIEnv *_env;
    jobject _jobj;

public: 
    /* wrapper constructor */
    List(JNIEnv *env);
    List(JNIEnv *env, jobject jobj);
    static List *_wrap(jobject jobj);

    inline operator JNIEnv *() const { return _env; }
    inline operator jobject() const { return _jobj; }

public: 
    /* method wrappers */
    jboolean add_arg0(jobject arg0);
    void add_arg0_arg1(jint arg0, jobject arg1);
    jboolean addAll_arg0(jobject arg0);
    jboolean addAll_arg0_arg1(jint arg0, jobject arg1);
    void addFirst(jobject arg0);
    void addLast(jobject arg0);
    void clear();
    jboolean contains(jobject arg0);
    jboolean containsAll(jobject arg0);
    static jobject copyOf(jobject arg0);
    jboolean equals(jobject arg0);
    void forEach(jobject arg0);
    jobject get(jint arg0);
    jobject getFirst();
    jobject getLast();
    jint hashCode();
    jint indexOf(jobject arg0);
    jboolean isEmpty();
    jobject iterator();
    jint lastIndexOf(jobject arg0);
    jobject listIterator();
    jobject listIterator_arg0(jint arg0);
    static jobject of_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2);
    static jobject of_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3);
    static jobject of_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4);
    static jobject of_Object_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8, jobject arg9);
    static jobject of_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8);
    static jobject of();
    static jobject of_Object(jobject arg0);
    static jobject of_Object_Object(jobject arg0, jobject arg1);
    static jobject of_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6);
    static jobject of_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7);
    static jobject of_ObjectArray(jobjectArray arg0);
    static jobject of_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5);
    jobject parallelStream();
    jboolean remove_Object(jobject arg0);
    jobject remove_int(jint arg0);
    jboolean removeAll(jobject arg0);
    jobject removeFirst();
    jboolean removeIf(jobject arg0);
    jobject removeLast();
    void replaceAll(jobject arg0);
    jboolean retainAll(jobject arg0);
    jobject set(jint arg0, jobject arg1);
    jint size();
    void sort(jobject arg0);
    jobject spliterator();
    jobject stream();
    jobject subList(jint arg0, jint arg1);
    jobjectArray toArray();
    jobjectArray toArray_ObjectArray(jobjectArray arg0);
    jobjectArray toArray_IntFunction(jobject arg0);

public: 
    static thread_local List_class CLASS;
}; // class List

} // namespace

#endif
