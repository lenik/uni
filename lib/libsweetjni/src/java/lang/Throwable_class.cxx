#include <sweetjni.hxx>

#include "Throwable_class.hxx"

using namespace java::lang;

Throwable_class::Throwable_class() {
    JNIEnv *env = getEnv();
    if (env == NULL) return;
    _class = findClass(env, "java/lang/Throwable");
    if (_class == NULL) return;
    INIT_Throwable = getMethod(env, _class, "<init>", "(Ljava/lang/Throwable;)V");
    INIT_String_Throwable = getMethod(env, _class, "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V");
    INIT_String = getMethod(env, _class, "<init>", "(Ljava/lang/String;)V");
    INIT = getMethod(env, _class, "<init>", "()V");
    METHOD_addSuppressed = getMethod(env, _class, "addSuppressed", "(Ljava/lang/Throwable;)V");
    METHOD_fillInStackTrace = getMethod(env, _class, "fillInStackTrace", "()Ljava/lang/Throwable;");
    METHOD_getCause = getMethod(env, _class, "getCause", "()Ljava/lang/Throwable;");
    METHOD_getLocalizedMessage = getMethod(env, _class, "getLocalizedMessage", "()Ljava/lang/String;");
    METHOD_getMessage = getMethod(env, _class, "getMessage", "()Ljava/lang/String;");
    METHOD_getStackTrace = getMethod(env, _class, "getStackTrace", "()[Ljava/lang/StackTraceElement;");
    METHOD_getSuppressed = getMethod(env, _class, "getSuppressed", "()[Ljava/lang/Throwable;");
    METHOD_initCause = getMethod(env, _class, "initCause", "(Ljava/lang/Throwable;)Ljava/lang/Throwable;");
    METHOD_printStackTrace_PrintWriter = getMethod(env, _class, "printStackTrace", "(Ljava/io/PrintWriter;)V");
    METHOD_printStackTrace = getMethod(env, _class, "printStackTrace", "()V");
    METHOD_printStackTrace_PrintStream = getMethod(env, _class, "printStackTrace", "(Ljava/io/PrintStream;)V");
    METHOD_setStackTrace = getMethod(env, _class, "setStackTrace", "([Ljava/lang/StackTraceElement;)V");
    METHOD_toString = getMethod(env, _class, "toString", "()Ljava/lang/String;");
}

void Throwable_class::dump() {
    printf("INIT_Throwable: %p\n", INIT_Throwable.id);
    printf("INIT_String_Throwable: %p\n", INIT_String_Throwable.id);
    printf("INIT_String: %p\n", INIT_String.id);
    printf("INIT: %p\n", INIT.id);
    printf("METHOD_addSuppressed: %p\n", METHOD_addSuppressed.id);
    printf("METHOD_fillInStackTrace: %p\n", METHOD_fillInStackTrace.id);
    printf("METHOD_getCause: %p\n", METHOD_getCause.id);
    printf("METHOD_getLocalizedMessage: %p\n", METHOD_getLocalizedMessage.id);
    printf("METHOD_getMessage: %p\n", METHOD_getMessage.id);
    printf("METHOD_getStackTrace: %p\n", METHOD_getStackTrace.id);
    printf("METHOD_getSuppressed: %p\n", METHOD_getSuppressed.id);
    printf("METHOD_initCause: %p\n", METHOD_initCause.id);
    printf("METHOD_printStackTrace_PrintWriter: %p\n", METHOD_printStackTrace_PrintWriter.id);
    printf("METHOD_printStackTrace: %p\n", METHOD_printStackTrace.id);
    printf("METHOD_printStackTrace_PrintStream: %p\n", METHOD_printStackTrace_PrintStream.id);
    printf("METHOD_setStackTrace: %p\n", METHOD_setStackTrace.id);
    printf("METHOD_toString: %p\n", METHOD_toString.id);
}
