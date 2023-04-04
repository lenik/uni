/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_STRING_H
#define __JAVA_LANG_STRING_H

#include <jni.h>
#include <sweetjni.hxx>

#include "String_class.hxx"

namespace java::lang {

class String : public IWrapper {
    JNIEnv *_env;
    jstring _jobj;

public:
    /* wrapper constructor */
    String(JNIEnv *env, jstring jstr);
    static String *_wrap(jstring jstr);

    inline operator JNIEnv *() const { return _env; }
    inline operator jobject() const { return _jobj; }
    inline operator jstring() const { return _jobj; }

public:
    /* ctor-create methods */
    String(jobject arg0);
    String(jbyteArray arg0, jint arg1, jint arg2, jobject arg3);
    String(jbyteArray arg0, jstring arg1);
    String(jbyteArray arg0, jobject arg1);
    String(jbyteArray arg0, jint arg1, jint arg2);
    String(jbyteArray arg0);
    String(jcharArray arg0, jint arg1, jint arg2);
    String(jcharArray arg0);
    String(jstring arg0);
    String();
    String(jbyteArray arg0, jint arg1, jint arg2, jstring arg3);
    String(jbyteArray arg0, jint arg1);
    String(jbyteArray arg0, jint arg1, jint arg2, jint arg3);
    String(jintArray arg0, jint arg1, jint arg2);

public:
    /* field accessors */
    ObjectProperty<jobject> CASE_INSENSITIVE_ORDER = wrapfield(this, CLASS.FIELD_CASE_INSENSITIVE_ORDER);

public:
    /* method wrappers */
    jchar charAt(jint arg0);
    jobject chars();
    jint codePointAt(jint arg0);
    jint codePointBefore(jint arg0);
    jint codePointCount(jint arg0, jint arg1);
    jobject codePoints();
    jint compareTo_String(jstring arg0);
    jint compareTo_Object(jobject arg0);
    jint compareToIgnoreCase(jstring arg0);
    jstring concat(jstring arg0);
    jboolean contains(jobject arg0);
    jboolean contentEquals_CharSequence(jobject arg0);
    jboolean contentEquals_StringBuffer(jobject arg0);
    static jstring copyValueOf_1(jcharArray arg0);
    static jstring copyValueOf_3(jcharArray arg0, jint arg1, jint arg2);
    jobject describeConstable();
    jboolean endsWith(jstring arg0);
    jboolean equals(jobject arg0);
    jboolean equalsIgnoreCase(jstring arg0);
    static jstring format_2(jstring arg0, jobjectArray arg1);
    static jstring format_3(jobject arg0, jstring arg1, jobjectArray arg2);
    jstring formatted(jobjectArray arg0);
    jbyteArray getBytes();
    jbyteArray getBytes_Charset(jobject arg0);
    jbyteArray getBytes_String(jstring arg0);
    void getBytes_int_int_byteArray_int(jint arg0, jint arg1, jbyteArray arg2, jint arg3);
    void getChars(jint arg0, jint arg1, jcharArray arg2, jint arg3);
    jint hashCode();
    jstring indent(jint arg0);
    jint indexOf_String(jstring arg0);
    jint indexOf_String_int(jstring arg0, jint arg1);
    jint indexOf_int(jint arg0);
    jint indexOf_int_int(jint arg0, jint arg1);
    jstring intern();
    jboolean isBlank();
    jboolean isEmpty();
    static jstring join_CharSequence_CharSequenceArray(jobject arg0, jarray arg1);
    static jstring join_CharSequence_Iterable(jobject arg0, jobject arg1);
    jint lastIndexOf_int_int(jint arg0, jint arg1);
    jint lastIndexOf_int(jint arg0);
    jint lastIndexOf_String_int(jstring arg0, jint arg1);
    jint lastIndexOf_String(jstring arg0);
    jint length();
    jobject lines();
    jboolean matches(jstring arg0);
    jint offsetByCodePoints(jint arg0, jint arg1);
    jboolean regionMatches_5(jboolean arg0, jint arg1, jstring arg2, jint arg3, jint arg4);
    jboolean regionMatches_4(jint arg0, jstring arg1, jint arg2, jint arg3);
    jstring repeat(jint arg0);
    jstring replace_CharSequence_CharSequence(jobject arg0, jobject arg1);
    jstring replace_char_char(jchar arg0, jchar arg1);
    jstring replaceAll(jstring arg0, jstring arg1);
    jstring replaceFirst(jstring arg0, jstring arg1);
    jarray split_2(jstring arg0, jint arg1);
    jarray split_1(jstring arg0);
    jboolean startsWith_1(jstring arg0);
    jboolean startsWith_2(jstring arg0, jint arg1);
    jstring strip();
    jstring stripIndent();
    jstring stripLeading();
    jstring stripTrailing();
    jobject subSequence(jint arg0, jint arg1);
    jstring substring_2(jint arg0, jint arg1);
    jstring substring_1(jint arg0);
    jcharArray toCharArray();
    jstring toLowerCase();
    jstring toLowerCase_1(jobject arg0);
    jstring toString();
    jstring toUpperCase();
    jstring toUpperCase_1(jobject arg0);
    jobject transform(jobject arg0);
    jstring translateEscapes();
    jstring trim();
    static jstring valueOf_char(jchar arg0);
    static jstring valueOf_charArray_int_int(jcharArray arg0, jint arg1, jint arg2);
    static jstring valueOf_charArray(jcharArray arg0);
    static jstring valueOf_Object(jobject arg0);
    static jstring valueOf_boolean(jboolean arg0);
    static jstring valueOf_double(jdouble arg0);
    static jstring valueOf_long(jlong arg0);
    static jstring valueOf_int(jint arg0);
    static jstring valueOf_float(jfloat arg0);

public:
    static thread_local String_class CLASS;
}; // class String

} // namespace

#endif
