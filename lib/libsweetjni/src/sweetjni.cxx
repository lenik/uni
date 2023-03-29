#include <stdio.h>
#include <stdarg.h>
#include <string.h>

#include "sweetjni.hxx"

#include "java/lang/String.hxx"
//using namespace java::lang;

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

jfieldID getFieldID(JNIEnv *env, jclass clazz, const char *name, const char *sig) {
    jfieldID id = env->GetFieldID(clazz, name, sig);
    env->ExceptionClear();
    return id;
}

jfieldID getStaticFieldID(JNIEnv *env, jclass clazz, const char *name, const char *sig) {
    jfieldID id = env->GetStaticFieldID(clazz, name, sig);
    env->ExceptionClear();
    return id;
}

jmethodID getMethodID(JNIEnv *env, jclass clazz, const char *name, const char *sig) {
    jmethodID id = env->GetMethodID(clazz, name, sig);
    env->ExceptionClear();
    return id;
}

jmethodID getStaticMethodID(JNIEnv *env, jclass clazz, const char *name, const char *sig) {
    jmethodID id = env->GetStaticMethodID(clazz, name, sig);
    env->ExceptionClear();
    return id;
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

jstring newString(const char *data) {
    JNIEnv *env = getEnv();
    if (env == NULL) return NULL;
    return newString(getEnv(), data);
}

jstring newString(const char *data, int off, int len) {
    JNIEnv *env = getEnv();
    if (env == NULL) return NULL;
    return newString(getEnv(), data, off, len);
}

jstring newString(JNIEnv *env, const char *data) {
    size_t len = strlen(data);
    return newString(env, data, 0, len);
}

jstring newString(JNIEnv *env, const char *data, int off, int len) {
    jbyteArray array = env->NewByteArray(len);
    env->SetByteArrayRegion(array, 0, len, (jbyte *) data);
    java::lang::String copy(array);
    env->DeleteLocalRef(array);
    return copy;
}
