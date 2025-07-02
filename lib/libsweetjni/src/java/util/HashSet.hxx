/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_HASH_SET_H
#define __JAVA_UTIL_HASH_SET_H

#include <jni.h>
#include <sweetjni.hxx>

#include <java/lang/Object.hxx>

#include "HashSet_class.hxx"

namespace java::util {

class HashSet : public java::lang::Object {
public: 
    /* wrapper constructor */
    HashSet(JNIEnv *env);
    HashSet(JNIEnv *env, jobject jobj);
    static HashSet *_wrap(jobject jobj);

public: 
    /* ctor-create methods */
    HashSet(jint arg0);
    HashSet(jint arg0, jfloat arg1);
    HashSet(jobject arg0);
    HashSet();

public: 
    /* method wrappers */
    jboolean add(jobject arg0);
    jboolean addAll(jobject arg0);
    void clear();
    jobject clone();
    jboolean contains(jobject arg0);
    jboolean containsAll(jobject arg0);
    jboolean equals(jobject arg0);
    void forEach(jobject arg0);
    jint hashCode();
    jboolean isEmpty();
    jobject iterator();
    static jobject newHashSet(jint arg0);
    jobject parallelStream();
    jboolean remove(jobject arg0);
    jboolean removeAll(jobject arg0);
    jboolean removeIf(jobject arg0);
    jboolean retainAll(jobject arg0);
    jint size();
    jobject spliterator();
    jobject stream();
    jobjectArray toArray_ObjectArray(jobjectArray arg0);
    jobjectArray toArray();
    jobjectArray toArray_IntFunction(jobject arg0);
    jstring toString();

public: 
    static thread_local HashSet_class CLASS;
}; // class HashSet

} // namespace

#endif
