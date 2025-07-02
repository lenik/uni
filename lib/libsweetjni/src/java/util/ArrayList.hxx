/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_ARRAY_LIST_H
#define __JAVA_UTIL_ARRAY_LIST_H

#include <jni.h>
#include <sweetjni.hxx>

#include <java/lang/Object.hxx>

#include "ArrayList_class.hxx"

namespace java::util {

class ArrayList : public java::lang::Object {
public: 
    /* wrapper constructor */
    ArrayList(JNIEnv *env);
    ArrayList(JNIEnv *env, jobject jobj);
    static ArrayList *_wrap(jobject jobj);

public: 
    /* ctor-create methods */
    ArrayList(jobject arg0);
    ArrayList();
    ArrayList(jint arg0);

public: 
    /* method wrappers */
    void add_arg0_arg1(jint arg0, jobject arg1);
    jboolean add_arg0(jobject arg0);
    jboolean addAll_arg0_arg1(jint arg0, jobject arg1);
    jboolean addAll_arg0(jobject arg0);
    void addFirst(jobject arg0);
    void addLast(jobject arg0);
    void clear();
    jobject clone();
    jboolean contains(jobject arg0);
    jboolean containsAll(jobject arg0);
    void ensureCapacity(jint arg0);
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
    jobject listIterator_arg0(jint arg0);
    jobject listIterator();
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
    jstring toString();
    void trimToSize();

public: 
    static thread_local ArrayList_class CLASS;
}; // class ArrayList

} // namespace

#endif
