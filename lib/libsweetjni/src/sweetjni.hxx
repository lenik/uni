#ifndef __LIBRARY_HXX
#define __LIBRARY_HXX

#include <jni.h>

extern "C" {
    extern JavaVM *vm;
    JNIEnv *getEnv();
}

jclass findClass(const char *name);
jclass findClass(JNIEnv *env, const char *name);

struct jfield {
    jclass clazz;
    jfieldID id;
    inline jfield() {}
    inline jfield(jclass c, jfieldID id) : clazz(c), id(id) {}
};

struct jmethod {
    jclass clazz;
    jmethodID id;
    inline jmethod() {}
    inline jmethod(jclass c, jmethodID id) : clazz(c), id(id) {}
};

jfield getField(JNIEnv *env, jclass clazz, const char *name, const char *sig);
jfield getStaticField(JNIEnv *env, jclass clazz, const char *name, const char *sig);
jmethod getMethod(JNIEnv *env, jclass clazz, const char *name, const char *sig);
jmethod getStaticMethod(JNIEnv *env, jclass clazz, const char *name, const char *sig);

jobject newObject(jmethod method, ...);
jobject newObject(JNIEnv *env, jmethod method, ...);

jstring newString(const char *data);
jstring newString(const char *data, int off, int len);
jstring newString(JNIEnv *env, const char *data);
jstring newString(JNIEnv *env, const char *data, int off, int len);

class IWrapper {
public:
    virtual operator JNIEnv *() const = 0;
    virtual operator jobject() const = 0;
};

struct WrapperAndField {
    IWrapper *wrapper;
    jfield field;
};
inline WrapperAndField wrapfield(IWrapper *wrapper, jfield field) {
    WrapperAndField waf = { wrapper, field };
    return waf;
}

template<class val_t>
class ObjectProperty {
    IWrapper *ctx;
    jfield field;

public:
    inline ObjectProperty(IWrapper *ctx, jfield field)
            : ctx(ctx), field(field) {
    }
    inline ObjectProperty(WrapperAndField waf)
            : ctx(waf.wrapper), field(waf.field) {
    }

public:
    inline operator val_t() const {
        JNIEnv *env = *ctx;
        jobject jobj = *ctx;
        return (val_t) (env->GetObjectField(jobj, field.id));
    }
    
    inline ObjectProperty<val_t>& operator =(val_t v) {
        JNIEnv *env = *ctx;
        jobject jobj = *ctx;
        env->SetObjectField(jobj, field.id, v);
        return *this;
    }
};

#define _jnitype_Byte       jbyte
#define _jnitype_Short      jshort
#define _jnitype_Int        jint
#define _jnitype_Long       jlong
#define _jnitype_Float      jfloat
#define _jnitype_Double     jdouble
#define _jnitype_Boolean    jboolean
#define _jnitype_Char       jchar

#define property_type2(proptype, jniword) \
    class proptype { \
        IWrapper *ctx; \
        jfield field; \
    public: \
        inline proptype(IWrapper *ctx, jfield field) \
            : ctx(ctx), field(field) { \
        } \
        inline proptype(WrapperAndField waf) \
            : ctx(waf.wrapper), field(waf.field) { \
        } \
    public: \
        inline operator _jnitype_##jniword() const { \
            JNIEnv *env = *ctx; \
            jobject jobj = *ctx; \
            return (_jnitype_##jniword) (env->Get##jniword##Field(jobj, field.id)); \
        } \
        inline proptype& operator =(const _jnitype_##jniword& v) { \
            JNIEnv *env = *ctx; \
            jobject jobj = *ctx; \
            env->Set##jniword##Field(jobj, field.id, (_jnitype_##jniword) v); \
            return *this; \
        } \
    }

#define property_type3(proptype, type, jniword) \
    class proptype { \
        IWrapper *ctx; \
        jfield field; \
    public: \
        inline proptype(IWrapper *ctx, jfield field) \
            : ctx(ctx), field(field) { \
        } \
        inline proptype(WrapperAndField waf) \
            : ctx(waf.wrapper), field(waf.field) { \
        } \
    public: \
        inline operator type() const { \
            JNIEnv *env = *ctx; \
            jobject jobj = *ctx; \
            return (type) (env->Get##jniword##Field(jobj, field.id)); \
        } \
        inline operator _jnitype_##jniword() const { \
            return (_jnitype_##jniword) (type) *this; \
        } \
        inline proptype& operator =(const type& v) { \
            JNIEnv *env = *ctx; \
            jobject jobj = *ctx; \
            env->Set##jniword##Field(jobj, field.id, (_jnitype_##jniword) v); \
            return *this; \
        } \
    }

#define __get_macro3(_1, _2, _3, name, ...) name
#define property_type(...) __get_macro3(__VA_ARGS__, property_type3, property_type2)(__VA_ARGS__)

property_type(ByteProperty,                 Byte);
property_type(ShortProperty,                Short);
property_type(IntProperty,                  Int);
property_type(LongProperty,                 Long);
property_type(FloatProperty,                Float);
property_type(DoubleProperty,               Double);
property_type(BooleanProperty,              Boolean);
property_type(CharProperty,                 Char);
property_type(PointerProperty,  void *,     Long);

#endif
