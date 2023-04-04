/** GENERATED FILE, PLEASE DON'T MODIFY. **/

#ifndef __JAVA_LANG_STRING_CLASS_H
#define __JAVA_LANG_STRING_CLASS_H

#include <jni.h>

namespace java::lang {

class String_class {
public:
    String_class();
    void dump();

public:
    jclass _class;

    jfield FIELD_CASE_INSENSITIVE_ORDER;

    jmethod INIT_StringBuffer;
    jmethod INIT_StringBuilder;
    jmethod INIT_byteArray_int_int_Charset;
    jmethod INIT_byteArray_String;
    jmethod INIT_byteArray_Charset;
    jmethod INIT_byteArray_int_int;
    jmethod INIT_byteArray;
    jmethod INIT_charArray_int_int;
    jmethod INIT_charArray;
    jmethod INIT_String;
    jmethod INIT;
    jmethod INIT_byteArray_int_int_String;
    jmethod INIT_byteArray_int;
    jmethod INIT_byteArray_int_int_int;
    jmethod INIT_intArray_int_int;

    jmethod METHOD_charAt;
    jmethod METHOD_chars;
    jmethod METHOD_codePointAt;
    jmethod METHOD_codePointBefore;
    jmethod METHOD_codePointCount;
    jmethod METHOD_codePoints;
    jmethod METHOD_compareTo_String;
    jmethod METHOD_compareTo_Object;
    jmethod METHOD_compareToIgnoreCase;
    jmethod METHOD_concat;
    jmethod METHOD_contains;
    jmethod METHOD_contentEquals_CharSequence;
    jmethod METHOD_contentEquals_StringBuffer;
    jmethod METHOD_copyValueOf_1;
    jmethod METHOD_copyValueOf_3;
    jmethod METHOD_describeConstable;
    jmethod METHOD_endsWith;
    jmethod METHOD_equals;
    jmethod METHOD_equalsIgnoreCase;
    jmethod METHOD_format_2;
    jmethod METHOD_format_3;
    jmethod METHOD_formatted;
    jmethod METHOD_getBytes;
    jmethod METHOD_getBytes_Charset;
    jmethod METHOD_getBytes_String;
    jmethod METHOD_getBytes_int_int_byteArray_int;
    jmethod METHOD_getChars;
    jmethod METHOD_hashCode;
    jmethod METHOD_indent;
    jmethod METHOD_indexOf_String;
    jmethod METHOD_indexOf_String_int;
    jmethod METHOD_indexOf_int;
    jmethod METHOD_indexOf_int_int;
    jmethod METHOD_intern;
    jmethod METHOD_isBlank;
    jmethod METHOD_isEmpty;
    jmethod METHOD_join_CharSequence_CharSequenceArray;
    jmethod METHOD_join_CharSequence_Iterable;
    jmethod METHOD_lastIndexOf_int_int;
    jmethod METHOD_lastIndexOf_int;
    jmethod METHOD_lastIndexOf_String_int;
    jmethod METHOD_lastIndexOf_String;
    jmethod METHOD_length;
    jmethod METHOD_lines;
    jmethod METHOD_matches;
    jmethod METHOD_offsetByCodePoints;
    jmethod METHOD_regionMatches_5;
    jmethod METHOD_regionMatches_4;
    jmethod METHOD_repeat;
    jmethod METHOD_replace_CharSequence_CharSequence;
    jmethod METHOD_replace_char_char;
    jmethod METHOD_replaceAll;
    jmethod METHOD_replaceFirst;
    jmethod METHOD_split_2;
    jmethod METHOD_split_1;
    jmethod METHOD_startsWith_1;
    jmethod METHOD_startsWith_2;
    jmethod METHOD_strip;
    jmethod METHOD_stripIndent;
    jmethod METHOD_stripLeading;
    jmethod METHOD_stripTrailing;
    jmethod METHOD_subSequence;
    jmethod METHOD_substring_2;
    jmethod METHOD_substring_1;
    jmethod METHOD_toCharArray;
    jmethod METHOD_toLowerCase;
    jmethod METHOD_toLowerCase_1;
    jmethod METHOD_toString;
    jmethod METHOD_toUpperCase;
    jmethod METHOD_toUpperCase_1;
    jmethod METHOD_transform;
    jmethod METHOD_translateEscapes;
    jmethod METHOD_trim;
    jmethod METHOD_valueOf_char;
    jmethod METHOD_valueOf_charArray_int_int;
    jmethod METHOD_valueOf_charArray;
    jmethod METHOD_valueOf_Object;
    jmethod METHOD_valueOf_boolean;
    jmethod METHOD_valueOf_double;
    jmethod METHOD_valueOf_long;
    jmethod METHOD_valueOf_int;
    jmethod METHOD_valueOf_float;
}; // class String_class

} // namespace

#endif
