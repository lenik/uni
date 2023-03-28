/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_STRING_H
#define __JAVA_LANG_STRING_H

#include <jni.h>
#include <jnigen.hxx>

namespace java::lang {

class String_class {
public:
    String_class();
    void dump();

public:
    jclass _class;

    jfieldID FIELD_CASE_INSENSITIVE_ORDER;

    jmethodID INIT_StringBuffer;
    jmethodID INIT_StringBuilder;
    jmethodID INIT_byteArray_int_int_Charset;
    jmethodID INIT_byteArray_String;
    jmethodID INIT_byteArray_Charset;
    jmethodID INIT_byteArray_int_int;
    jmethodID INIT_byteArray;
    jmethodID INIT_charArray_int_int;
    jmethodID INIT_charArray;
    jmethodID INIT_String;
    jmethodID INIT;
    jmethodID INIT_byteArray_int_int_String;
    jmethodID INIT_byteArray_int;
    jmethodID INIT_byteArray_int_int_int;
    jmethodID INIT_intArray_int_int;

    jmethodID METHOD_equals;
    jmethodID METHOD_length;
    jmethodID METHOD_toString;
    jmethodID METHOD_hashCode;
    jmethodID METHOD_getChars;
    jmethodID METHOD_compareTo_String;
    jmethodID METHOD_compareTo_Object;
    jmethodID METHOD_indexOf_String;
    jmethodID METHOD_indexOf_String_int;
    jmethodID METHOD_indexOf_int;
    jmethodID METHOD_indexOf_int_int;
    jmethodID METHOD_valueOf_char;
    jmethodID METHOD_valueOf_charArray_int_int;
    jmethodID METHOD_valueOf_charArray;
    jmethodID METHOD_valueOf_Object;
    jmethodID METHOD_valueOf_boolean;
    jmethodID METHOD_valueOf_double;
    jmethodID METHOD_valueOf_long;
    jmethodID METHOD_valueOf_int;
    jmethodID METHOD_valueOf_float;
    jmethodID METHOD_charAt;
    jmethodID METHOD_codePointAt;
    jmethodID METHOD_codePointBefore;
    jmethodID METHOD_codePointCount;
    jmethodID METHOD_offsetByCodePoints;
    jmethodID METHOD_getBytes;
    jmethodID METHOD_getBytes_Charset;
    jmethodID METHOD_getBytes_String;
    jmethodID METHOD_getBytes_int_int_byteArray_int;
    jmethodID METHOD_contentEquals_CharSequence;
    jmethodID METHOD_contentEquals_StringBuffer;
    jmethodID METHOD_regionMatches_5;
    jmethodID METHOD_regionMatches_4;
    jmethodID METHOD_startsWith_1;
    jmethodID METHOD_startsWith_2;
    jmethodID METHOD_lastIndexOf_int_int;
    jmethodID METHOD_lastIndexOf_int;
    jmethodID METHOD_lastIndexOf_String_int;
    jmethodID METHOD_lastIndexOf_String;
    jmethodID METHOD_substring_2;
    jmethodID METHOD_substring_1;
    jmethodID METHOD_isEmpty;
    jmethodID METHOD_replace_CharSequence_CharSequence;
    jmethodID METHOD_replace_char_char;
    jmethodID METHOD_matches;
    jmethodID METHOD_replaceFirst;
    jmethodID METHOD_replaceAll;
    jmethodID METHOD_split_2;
    jmethodID METHOD_split_1;
    jmethodID METHOD_join_CharSequence_CharSequenceArray;
    jmethodID METHOD_join_CharSequence_Iterable;
    jmethodID METHOD_toLowerCase;
    jmethodID METHOD_toLowerCase_1;
    jmethodID METHOD_toUpperCase;
    jmethodID METHOD_toUpperCase_1;
    jmethodID METHOD_trim;
    jmethodID METHOD_strip;
    jmethodID METHOD_stripLeading;
    jmethodID METHOD_stripTrailing;
    jmethodID METHOD_lines;
    jmethodID METHOD_repeat;
    jmethodID METHOD_isBlank;
    jmethodID METHOD_toCharArray;
    jmethodID METHOD_format_2;
    jmethodID METHOD_format_3;
    jmethodID METHOD_codePoints;
    jmethodID METHOD_equalsIgnoreCase;
    jmethodID METHOD_compareToIgnoreCase;
    jmethodID METHOD_endsWith;
    jmethodID METHOD_subSequence;
    jmethodID METHOD_concat;
    jmethodID METHOD_contains;
    jmethodID METHOD_indent;
    jmethodID METHOD_stripIndent;
    jmethodID METHOD_translateEscapes;
    jmethodID METHOD_chars;
    jmethodID METHOD_transform;
    jmethodID METHOD_formatted;
    jmethodID METHOD_copyValueOf_1;
    jmethodID METHOD_copyValueOf_3;
    jmethodID METHOD_intern;
    jmethodID METHOD_describeConstable;
}; // class String_class

class String : public IWrapper {
    jstring _this;
    JNIEnv *_env;

public:
    /* wrapper constructor */
    String(JNIEnv *env, jstring _this);
    static String *_wrap(jstring _this);

    inline JNIEnv *__env() { return _env; }
    inline jstring __this() { return _this; }
    inline operator jstring() { return _this; }
    
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
    jboolean equals(jobject arg0);
    jint length();
    jstring toString();
    jint hashCode();
    void getChars(jint arg0, jint arg1, jcharArray arg2, jint arg3);
    jint compareTo_String(jstring arg0);
    jint compareTo_Object(jobject arg0);
    jint indexOf_String(jstring arg0);
    jint indexOf_String_int(jstring arg0, jint arg1);
    jint indexOf_int(jint arg0);
    jint indexOf_int_int(jint arg0, jint arg1);
    static jstring valueOf_char(jchar arg0);
    static jstring valueOf_charArray_int_int(jcharArray arg0, jint arg1, jint arg2);
    static jstring valueOf_charArray(jcharArray arg0);
    static jstring valueOf_Object(jobject arg0);
    static jstring valueOf_boolean(jboolean arg0);
    static jstring valueOf_double(jdouble arg0);
    static jstring valueOf_long(jlong arg0);
    static jstring valueOf_int(jint arg0);
    static jstring valueOf_float(jfloat arg0);
    jchar charAt(jint arg0);
    jint codePointAt(jint arg0);
    jint codePointBefore(jint arg0);
    jint codePointCount(jint arg0, jint arg1);
    jint offsetByCodePoints(jint arg0, jint arg1);
    jbyteArray getBytes();
    jbyteArray getBytes_Charset(jobject arg0);
    jbyteArray getBytes_String(jstring arg0);
    void getBytes_int_int_byteArray_int(jint arg0, jint arg1, jbyteArray arg2, jint arg3);
    jboolean contentEquals_CharSequence(jobject arg0);
    jboolean contentEquals_StringBuffer(jobject arg0);
    jboolean regionMatches_5(jboolean arg0, jint arg1, jstring arg2, jint arg3, jint arg4);
    jboolean regionMatches_4(jint arg0, jstring arg1, jint arg2, jint arg3);
    jboolean startsWith_1(jstring arg0);
    jboolean startsWith_2(jstring arg0, jint arg1);
    jint lastIndexOf_int_int(jint arg0, jint arg1);
    jint lastIndexOf_int(jint arg0);
    jint lastIndexOf_String_int(jstring arg0, jint arg1);
    jint lastIndexOf_String(jstring arg0);
    jstring substring_2(jint arg0, jint arg1);
    jstring substring_1(jint arg0);
    jboolean isEmpty();
    jstring replace_CharSequence_CharSequence(jobject arg0, jobject arg1);
    jstring replace_char_char(jchar arg0, jchar arg1);
    jboolean matches(jstring arg0);
    jstring replaceFirst(jstring arg0, jstring arg1);
    jstring replaceAll(jstring arg0, jstring arg1);
    jarray split_2(jstring arg0, jint arg1);
    jarray split_1(jstring arg0);
    static jstring join_CharSequence_CharSequenceArray(jobject arg0, jarray arg1);
    static jstring join_CharSequence_Iterable(jobject arg0, jobject arg1);
    jstring toLowerCase();
    jstring toLowerCase_1(jobject arg0);
    jstring toUpperCase();
    jstring toUpperCase_1(jobject arg0);
    jstring trim();
    jstring strip();
    jstring stripLeading();
    jstring stripTrailing();
    jobject lines();
    jstring repeat(jint arg0);
    jboolean isBlank();
    jcharArray toCharArray();
    static jstring format_2(jstring arg0, jobjectArray arg1);
    static jstring format_3(jobject arg0, jstring arg1, jobjectArray arg2);
    jobject codePoints();
    jboolean equalsIgnoreCase(jstring arg0);
    jint compareToIgnoreCase(jstring arg0);
    jboolean endsWith(jstring arg0);
    jobject subSequence(jint arg0, jint arg1);
    jstring concat(jstring arg0);
    jboolean contains(jobject arg0);
    jstring indent(jint arg0);
    jstring stripIndent();
    jstring translateEscapes();
    jobject chars();
    jobject transform(jobject arg0);
    jstring formatted(jobjectArray arg0);
    static jstring copyValueOf_1(jcharArray arg0);
    static jstring copyValueOf_3(jcharArray arg0, jint arg1, jint arg2);
    jstring intern();
    jobject describeConstable();

public:
    static thread_local String_class CLASS;
}; // class String

} // namespace

#endif
