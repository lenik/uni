#ifndef __LIBRARY_HXX
#define __LIBRARY_HXX

#include <jni.h>

extern "C" {
    extern JavaVM *vm;
    JNIEnv *getEnv();
}

jclass findClass(const char *name);
jclass findClass(JNIEnv *env, const char *name);

jobject newObject(jclass clazz, jmethodID methodId, ...);
jobject newObject(JNIEnv *env, jclass clazz, jmethodID methodId, ...);

class IWrapper {
public:
    virtual JNIEnv *__env() = 0;
    virtual jobject __this() = 0;
};

struct WrapperAndField {
    IWrapper *wrapper;
    jfieldID fieldId;
};
inline WrapperAndField wrapfield(IWrapper *wrapper, jfieldID fieldId) {
    WrapperAndField waf = { wrapper, fieldId };
    return waf;
}

template<class val_t>
class ObjectProperty {
    IWrapper *ctx;
    jfieldID fieldId;

public:
    inline ObjectProperty(IWrapper *ctx, jfieldID fieldId) {
        this->ctx = ctx;
        this->fieldId = fieldId;
    }
    inline ObjectProperty(WrapperAndField waf) {
        this->ctx = waf.wrapper;
        this->fieldId = waf.fieldId;
    }

public:
    inline operator val_t() const {
        JNIEnv *env = ctx->__env();
        jobject _this = ctx->__this();
        return (val_t) (env->GetObjectField(_this, fieldId));
    }
    
    inline ObjectProperty<val_t>& operator =(val_t v) {
        JNIEnv *env = ctx->__env();
        jobject _this = ctx->__this();
        env->SetObjectField(_this,fieldId, v);
        return *this;
    }
};

#define property_type(type, m) \
    class m##Property { \
        IWrapper *ctx; \
        jfieldID fieldId; \
    public: \
        inline m##Property(IWrapper *ctx, jfieldID fieldId) { \
            this->ctx = ctx; \
            this->fieldId = fieldId; \
        } \
        inline m##Property(WrapperAndField waf) { \
            this->ctx = waf.wrapper; \
            this->fieldId = waf.fieldId; \
        } \
    public: \
        inline operator type() const { \
            JNIEnv *env = ctx->__env(); \
            jobject _this = ctx->__this(); \
            return (type) (env->Get##m##Field(_this, fieldId)); \
        } \
        inline m##Property& operator =(const type& v) { \
            JNIEnv *env = ctx->__env(); \
            jobject _this = ctx->__this(); \
            env->Set##m##Field(_this,fieldId, v); \
            return *this; \
        } \
    }
property_type(jbyte, Byte);
property_type(jshort, Short);
property_type(jint, Int);
property_type(jlong, Long);
property_type(jfloat, Float);
property_type(jdouble, Double);
property_type(jboolean, Boolean);
property_type(jchar, Char);

#endif
