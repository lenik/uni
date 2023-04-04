#include <sweetjni.hxx>

#include "Object_class.hxx"

using namespace java::lang;

Object_class::Object_class() {
    JNIEnv *env = getEnv();
    if (env == NULL) return;
    _class = findClass(env, "java/lang/Object");
    if (_class == NULL) return;
    INIT = getMethod(env, _class, "<init>", "()V");
    METHOD_equals = getMethod(env, _class, "equals", "(Ljava/lang/Object;)Z");
    METHOD_getClass = getMethod(env, _class, "getClass", "()Ljava/lang/Class;");
    METHOD_hashCode = getMethod(env, _class, "hashCode", "()I");
    METHOD_notify = getMethod(env, _class, "notify", "()V");
    METHOD_notifyAll = getMethod(env, _class, "notifyAll", "()V");
    METHOD_toString = getMethod(env, _class, "toString", "()Ljava/lang/String;");
    METHOD_wait_1 = getMethod(env, _class, "wait", "(J)V");
    METHOD_wait_2 = getMethod(env, _class, "wait", "(JI)V");
    METHOD_wait = getMethod(env, _class, "wait", "()V");
}

void Object_class::dump() {
    printf("INIT: %p\n", INIT.id);
    printf("METHOD_equals: %p\n", METHOD_equals.id);
    printf("METHOD_getClass: %p\n", METHOD_getClass.id);
    printf("METHOD_hashCode: %p\n", METHOD_hashCode.id);
    printf("METHOD_notify: %p\n", METHOD_notify.id);
    printf("METHOD_notifyAll: %p\n", METHOD_notifyAll.id);
    printf("METHOD_toString: %p\n", METHOD_toString.id);
    printf("METHOD_wait_1: %p\n", METHOD_wait_1.id);
    printf("METHOD_wait_2: %p\n", METHOD_wait_2.id);
    printf("METHOD_wait: %p\n", METHOD_wait.id);
}
