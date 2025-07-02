/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_UTIL_LINKED_LIST_H
#define __JAVA_UTIL_LINKED_LIST_H

#include <jni.h>
#include <sweetjni.hxx>

#include <java/lang/Object.hxx>

#include "LinkedList_class.hxx"

namespace java::util {

class LinkedList : public java::lang::Object {
public: 
    /* wrapper constructor */
    LinkedList(JNIEnv *env);
    LinkedList(JNIEnv *env, jobject jobj);
    static LinkedList *_wrap(jobject jobj);

public: 
    /* ctor-create methods */
    LinkedList();
    LinkedList(jobject arg0);

public: 
    /* method wrappers */
    void add_arg0_arg1(jint arg0, jobject arg1);
    jboolean add_arg0(jobject arg0);
    jboolean addAll_arg0(jobject arg0);
    jboolean addAll_arg0_arg1(jint arg0, jobject arg1);
    void addFirst(jobject arg0);
    void addLast(jobject arg0);
    void clear();
    jobject clone();
    jboolean contains(jobject arg0);
    jboolean containsAll(jobject arg0);
    jobject descendingIterator();
    jobject element();
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
    jboolean offer(jobject arg0);
    jboolean offerFirst(jobject arg0);
    jboolean offerLast(jobject arg0);
    jobject parallelStream();
    jobject peek();
    jobject peekFirst();
    jobject peekLast();
    jobject poll();
    jobject pollFirst();
    jobject pollLast();
    jobject pop();
    void push(jobject arg0);
    jobject remove_int(jint arg0);
    jobject remove();
    jboolean remove_Object(jobject arg0);
    jboolean removeAll(jobject arg0);
    jobject removeFirst();
    jboolean removeFirstOccurrence(jobject arg0);
    jboolean removeIf(jobject arg0);
    jobject removeLast();
    jboolean removeLastOccurrence(jobject arg0);
    void replaceAll(jobject arg0);
    jboolean retainAll(jobject arg0);
    jobject set(jint arg0, jobject arg1);
    jint size();
    void sort(jobject arg0);
    jobject spliterator();
    jobject stream();
    jobject subList(jint arg0, jint arg1);
    jobjectArray toArray_ObjectArray(jobjectArray arg0);
    jobjectArray toArray();
    jobjectArray toArray_IntFunction(jobject arg0);
    jstring toString();

public: 
    static thread_local LinkedList_class CLASS;
}; // class LinkedList

} // namespace

#endif
