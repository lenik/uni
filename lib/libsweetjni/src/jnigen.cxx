#include "jnigen.hxx"

#include <stdio.h>
#include <stdarg.h>

JavaVM *vm;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    ::vm = vm;
    return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    ::vm = NULL;
}

JNIEnv *getEnv() {
    JNIEnv *env = NULL;
    int stat = vm->GetEnv((void **) &env, JNI_VERSION_1_8);
    switch (stat) {
        case JNI_OK:
            return env;

        case JNI_EDETACHED:
            fprintf(stderr, "GetEnv: not attached.\n");
            if (vm->AttachCurrentThread((void **) &env, NULL) != 0) {
                fprintf(stderr, "Failed to attach.\n");
                return NULL;
            }
            return env;
        
        case JNI_EVERSION:
            fprintf(stderr, "GetEnv: version not supported.\n");
            return NULL;
        
        default:
            fprintf(stderr, "GetEnv: unknown error: %d\n", stat);
            return NULL;
    }
}

jclass findClass(const char *name) {
    JNIEnv *env = getEnv();
    if (env == NULL) return NULL;
    return findClass(env, name);
}

jclass findClass(JNIEnv *env, const char *name) {
    jclass cls = env->FindClass(name);
    return cls;
}

jobject newObject(jclass clazz, jmethodID methodId, ...) {
    JNIEnv *env = getEnv();
    if (env == NULL) return NULL;
    va_list args;
    va_start(args, methodId);
    jobject ret = env->NewObjectV(clazz, methodId, args);
    va_end(args);
    return ret;
}

jobject newObject(JNIEnv *env, jclass clazz, jmethodID methodId, ...) {
    va_list args;
    va_start(args, methodId);
    jobject ret = env->NewObjectV(clazz, methodId, args);
    va_end(args);
    return ret;
}

