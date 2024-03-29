#include <sweetjni.hxx>

#include "String_class.hxx"

using namespace java::lang;

String_class::String_class() {
    JNIEnv *env = getEnv();
    if (env == NULL) return;
    _class = findClass(env, "java/lang/String");
    if (_class == NULL) return;
    FIELD_CASE_INSENSITIVE_ORDER = getStaticField(env, _class, "CASE_INSENSITIVE_ORDER", "Ljava/util/Comparator;");
    INIT_StringBuffer = getMethod(env, _class, "<init>", "(Ljava/lang/StringBuffer;)V");
    INIT_StringBuilder = getMethod(env, _class, "<init>", "(Ljava/lang/StringBuilder;)V");
    INIT_byteArray_int_int_Charset = getMethod(env, _class, "<init>", "([BIILjava/nio/charset/Charset;)V");
    INIT_byteArray_String = getMethod(env, _class, "<init>", "([BLjava/lang/String;)V");
    INIT_byteArray_Charset = getMethod(env, _class, "<init>", "([BLjava/nio/charset/Charset;)V");
    INIT_byteArray_int_int = getMethod(env, _class, "<init>", "([BII)V");
    INIT_byteArray = getMethod(env, _class, "<init>", "([B)V");
    INIT_charArray_int_int = getMethod(env, _class, "<init>", "([CII)V");
    INIT_charArray = getMethod(env, _class, "<init>", "([C)V");
    INIT_String = getMethod(env, _class, "<init>", "(Ljava/lang/String;)V");
    INIT = getMethod(env, _class, "<init>", "()V");
    INIT_byteArray_int_int_String = getMethod(env, _class, "<init>", "([BIILjava/lang/String;)V");
    INIT_byteArray_int = getMethod(env, _class, "<init>", "([BI)V");
    INIT_byteArray_int_int_int = getMethod(env, _class, "<init>", "([BIII)V");
    INIT_intArray_int_int = getMethod(env, _class, "<init>", "([III)V");
    METHOD_charAt = getMethod(env, _class, "charAt", "(I)C");
    METHOD_chars = getMethod(env, _class, "chars", "()Ljava/util/stream/IntStream;");
    METHOD_codePointAt = getMethod(env, _class, "codePointAt", "(I)I");
    METHOD_codePointBefore = getMethod(env, _class, "codePointBefore", "(I)I");
    METHOD_codePointCount = getMethod(env, _class, "codePointCount", "(II)I");
    METHOD_codePoints = getMethod(env, _class, "codePoints", "()Ljava/util/stream/IntStream;");
    METHOD_compareTo_String = getMethod(env, _class, "compareTo", "(Ljava/lang/String;)I");
    METHOD_compareTo_Object = getMethod(env, _class, "compareTo", "(Ljava/lang/Object;)I");
    METHOD_compareToIgnoreCase = getMethod(env, _class, "compareToIgnoreCase", "(Ljava/lang/String;)I");
    METHOD_concat = getMethod(env, _class, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
    METHOD_contains = getMethod(env, _class, "contains", "(Ljava/lang/CharSequence;)Z");
    METHOD_contentEquals_CharSequence = getMethod(env, _class, "contentEquals", "(Ljava/lang/CharSequence;)Z");
    METHOD_contentEquals_StringBuffer = getMethod(env, _class, "contentEquals", "(Ljava/lang/StringBuffer;)Z");
    METHOD_copyValueOf_1 = getStaticMethod(env, _class, "copyValueOf", "([C)Ljava/lang/String;");
    METHOD_copyValueOf_3 = getStaticMethod(env, _class, "copyValueOf", "([CII)Ljava/lang/String;");
    METHOD_describeConstable = getMethod(env, _class, "describeConstable", "()Ljava/util/Optional;");
    METHOD_endsWith = getMethod(env, _class, "endsWith", "(Ljava/lang/String;)Z");
    METHOD_equals = getMethod(env, _class, "equals", "(Ljava/lang/Object;)Z");
    METHOD_equalsIgnoreCase = getMethod(env, _class, "equalsIgnoreCase", "(Ljava/lang/String;)Z");
    METHOD_format_2 = getStaticMethod(env, _class, "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_format_3 = getStaticMethod(env, _class, "format", "(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_formatted = getMethod(env, _class, "formatted", "([Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_getBytes = getMethod(env, _class, "getBytes", "()[B");
    METHOD_getBytes_Charset = getMethod(env, _class, "getBytes", "(Ljava/nio/charset/Charset;)[B");
    METHOD_getBytes_String = getMethod(env, _class, "getBytes", "(Ljava/lang/String;)[B");
    METHOD_getBytes_int_int_byteArray_int = getMethod(env, _class, "getBytes", "(II[BI)V");
    METHOD_getChars = getMethod(env, _class, "getChars", "(II[CI)V");
    METHOD_hashCode = getMethod(env, _class, "hashCode", "()I");
    METHOD_indent = getMethod(env, _class, "indent", "(I)Ljava/lang/String;");
    METHOD_indexOf_String = getMethod(env, _class, "indexOf", "(Ljava/lang/String;)I");
    METHOD_indexOf_String_int = getMethod(env, _class, "indexOf", "(Ljava/lang/String;I)I");
    METHOD_indexOf_int = getMethod(env, _class, "indexOf", "(I)I");
    METHOD_indexOf_int_int = getMethod(env, _class, "indexOf", "(II)I");
    METHOD_intern = getMethod(env, _class, "intern", "()Ljava/lang/String;");
    METHOD_isBlank = getMethod(env, _class, "isBlank", "()Z");
    METHOD_isEmpty = getMethod(env, _class, "isEmpty", "()Z");
    METHOD_join_CharSequence_CharSequenceArray = getStaticMethod(env, _class, "join", "(Ljava/lang/CharSequence;[Ljava/lang/CharSequence;)Ljava/lang/String;");
    METHOD_join_CharSequence_Iterable = getStaticMethod(env, _class, "join", "(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;");
    METHOD_lastIndexOf_int_int = getMethod(env, _class, "lastIndexOf", "(II)I");
    METHOD_lastIndexOf_int = getMethod(env, _class, "lastIndexOf", "(I)I");
    METHOD_lastIndexOf_String_int = getMethod(env, _class, "lastIndexOf", "(Ljava/lang/String;I)I");
    METHOD_lastIndexOf_String = getMethod(env, _class, "lastIndexOf", "(Ljava/lang/String;)I");
    METHOD_length = getMethod(env, _class, "length", "()I");
    METHOD_lines = getMethod(env, _class, "lines", "()Ljava/util/stream/Stream;");
    METHOD_matches = getMethod(env, _class, "matches", "(Ljava/lang/String;)Z");
    METHOD_offsetByCodePoints = getMethod(env, _class, "offsetByCodePoints", "(II)I");
    METHOD_regionMatches_5 = getMethod(env, _class, "regionMatches", "(ZILjava/lang/String;II)Z");
    METHOD_regionMatches_4 = getMethod(env, _class, "regionMatches", "(ILjava/lang/String;II)Z");
    METHOD_repeat = getMethod(env, _class, "repeat", "(I)Ljava/lang/String;");
    METHOD_replace_CharSequence_CharSequence = getMethod(env, _class, "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
    METHOD_replace_char_char = getMethod(env, _class, "replace", "(CC)Ljava/lang/String;");
    METHOD_replaceAll = getMethod(env, _class, "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    METHOD_replaceFirst = getMethod(env, _class, "replaceFirst", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    METHOD_split_2 = getMethod(env, _class, "split", "(Ljava/lang/String;I)[Ljava/lang/String;");
    METHOD_split_1 = getMethod(env, _class, "split", "(Ljava/lang/String;)[Ljava/lang/String;");
    METHOD_startsWith_1 = getMethod(env, _class, "startsWith", "(Ljava/lang/String;)Z");
    METHOD_startsWith_2 = getMethod(env, _class, "startsWith", "(Ljava/lang/String;I)Z");
    METHOD_strip = getMethod(env, _class, "strip", "()Ljava/lang/String;");
    METHOD_stripIndent = getMethod(env, _class, "stripIndent", "()Ljava/lang/String;");
    METHOD_stripLeading = getMethod(env, _class, "stripLeading", "()Ljava/lang/String;");
    METHOD_stripTrailing = getMethod(env, _class, "stripTrailing", "()Ljava/lang/String;");
    METHOD_subSequence = getMethod(env, _class, "subSequence", "(II)Ljava/lang/CharSequence;");
    METHOD_substring_2 = getMethod(env, _class, "substring", "(II)Ljava/lang/String;");
    METHOD_substring_1 = getMethod(env, _class, "substring", "(I)Ljava/lang/String;");
    METHOD_toCharArray = getMethod(env, _class, "toCharArray", "()[C");
    METHOD_toLowerCase = getMethod(env, _class, "toLowerCase", "()Ljava/lang/String;");
    METHOD_toLowerCase_1 = getMethod(env, _class, "toLowerCase", "(Ljava/util/Locale;)Ljava/lang/String;");
    METHOD_toString = getMethod(env, _class, "toString", "()Ljava/lang/String;");
    METHOD_toUpperCase = getMethod(env, _class, "toUpperCase", "()Ljava/lang/String;");
    METHOD_toUpperCase_1 = getMethod(env, _class, "toUpperCase", "(Ljava/util/Locale;)Ljava/lang/String;");
    METHOD_transform = getMethod(env, _class, "transform", "(Ljava/util/function/Function;)Ljava/lang/Object;");
    METHOD_translateEscapes = getMethod(env, _class, "translateEscapes", "()Ljava/lang/String;");
    METHOD_trim = getMethod(env, _class, "trim", "()Ljava/lang/String;");
    METHOD_valueOf_char = getStaticMethod(env, _class, "valueOf", "(C)Ljava/lang/String;");
    METHOD_valueOf_charArray_int_int = getStaticMethod(env, _class, "valueOf", "([CII)Ljava/lang/String;");
    METHOD_valueOf_charArray = getStaticMethod(env, _class, "valueOf", "([C)Ljava/lang/String;");
    METHOD_valueOf_Object = getStaticMethod(env, _class, "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_valueOf_boolean = getStaticMethod(env, _class, "valueOf", "(Z)Ljava/lang/String;");
    METHOD_valueOf_double = getStaticMethod(env, _class, "valueOf", "(D)Ljava/lang/String;");
    METHOD_valueOf_long = getStaticMethod(env, _class, "valueOf", "(J)Ljava/lang/String;");
    METHOD_valueOf_int = getStaticMethod(env, _class, "valueOf", "(I)Ljava/lang/String;");
    METHOD_valueOf_float = getStaticMethod(env, _class, "valueOf", "(F)Ljava/lang/String;");
}

void String_class::dump() {
    printf("FIELD_CASE_INSENSITIVE_ORDER: %p\n", FIELD_CASE_INSENSITIVE_ORDER.id);
    printf("INIT_StringBuffer: %p\n", INIT_StringBuffer.id);
    printf("INIT_StringBuilder: %p\n", INIT_StringBuilder.id);
    printf("INIT_byteArray_int_int_Charset: %p\n", INIT_byteArray_int_int_Charset.id);
    printf("INIT_byteArray_String: %p\n", INIT_byteArray_String.id);
    printf("INIT_byteArray_Charset: %p\n", INIT_byteArray_Charset.id);
    printf("INIT_byteArray_int_int: %p\n", INIT_byteArray_int_int.id);
    printf("INIT_byteArray: %p\n", INIT_byteArray.id);
    printf("INIT_charArray_int_int: %p\n", INIT_charArray_int_int.id);
    printf("INIT_charArray: %p\n", INIT_charArray.id);
    printf("INIT_String: %p\n", INIT_String.id);
    printf("INIT: %p\n", INIT.id);
    printf("INIT_byteArray_int_int_String: %p\n", INIT_byteArray_int_int_String.id);
    printf("INIT_byteArray_int: %p\n", INIT_byteArray_int.id);
    printf("INIT_byteArray_int_int_int: %p\n", INIT_byteArray_int_int_int.id);
    printf("INIT_intArray_int_int: %p\n", INIT_intArray_int_int.id);
    printf("METHOD_charAt: %p\n", METHOD_charAt.id);
    printf("METHOD_chars: %p\n", METHOD_chars.id);
    printf("METHOD_codePointAt: %p\n", METHOD_codePointAt.id);
    printf("METHOD_codePointBefore: %p\n", METHOD_codePointBefore.id);
    printf("METHOD_codePointCount: %p\n", METHOD_codePointCount.id);
    printf("METHOD_codePoints: %p\n", METHOD_codePoints.id);
    printf("METHOD_compareTo_String: %p\n", METHOD_compareTo_String.id);
    printf("METHOD_compareTo_Object: %p\n", METHOD_compareTo_Object.id);
    printf("METHOD_compareToIgnoreCase: %p\n", METHOD_compareToIgnoreCase.id);
    printf("METHOD_concat: %p\n", METHOD_concat.id);
    printf("METHOD_contains: %p\n", METHOD_contains.id);
    printf("METHOD_contentEquals_CharSequence: %p\n", METHOD_contentEquals_CharSequence.id);
    printf("METHOD_contentEquals_StringBuffer: %p\n", METHOD_contentEquals_StringBuffer.id);
    printf("METHOD_copyValueOf_1: %p\n", METHOD_copyValueOf_1.id);
    printf("METHOD_copyValueOf_3: %p\n", METHOD_copyValueOf_3.id);
    printf("METHOD_describeConstable: %p\n", METHOD_describeConstable.id);
    printf("METHOD_endsWith: %p\n", METHOD_endsWith.id);
    printf("METHOD_equals: %p\n", METHOD_equals.id);
    printf("METHOD_equalsIgnoreCase: %p\n", METHOD_equalsIgnoreCase.id);
    printf("METHOD_format_2: %p\n", METHOD_format_2.id);
    printf("METHOD_format_3: %p\n", METHOD_format_3.id);
    printf("METHOD_formatted: %p\n", METHOD_formatted.id);
    printf("METHOD_getBytes: %p\n", METHOD_getBytes.id);
    printf("METHOD_getBytes_Charset: %p\n", METHOD_getBytes_Charset.id);
    printf("METHOD_getBytes_String: %p\n", METHOD_getBytes_String.id);
    printf("METHOD_getBytes_int_int_byteArray_int: %p\n", METHOD_getBytes_int_int_byteArray_int.id);
    printf("METHOD_getChars: %p\n", METHOD_getChars.id);
    printf("METHOD_hashCode: %p\n", METHOD_hashCode.id);
    printf("METHOD_indent: %p\n", METHOD_indent.id);
    printf("METHOD_indexOf_String: %p\n", METHOD_indexOf_String.id);
    printf("METHOD_indexOf_String_int: %p\n", METHOD_indexOf_String_int.id);
    printf("METHOD_indexOf_int: %p\n", METHOD_indexOf_int.id);
    printf("METHOD_indexOf_int_int: %p\n", METHOD_indexOf_int_int.id);
    printf("METHOD_intern: %p\n", METHOD_intern.id);
    printf("METHOD_isBlank: %p\n", METHOD_isBlank.id);
    printf("METHOD_isEmpty: %p\n", METHOD_isEmpty.id);
    printf("METHOD_join_CharSequence_CharSequenceArray: %p\n", METHOD_join_CharSequence_CharSequenceArray.id);
    printf("METHOD_join_CharSequence_Iterable: %p\n", METHOD_join_CharSequence_Iterable.id);
    printf("METHOD_lastIndexOf_int_int: %p\n", METHOD_lastIndexOf_int_int.id);
    printf("METHOD_lastIndexOf_int: %p\n", METHOD_lastIndexOf_int.id);
    printf("METHOD_lastIndexOf_String_int: %p\n", METHOD_lastIndexOf_String_int.id);
    printf("METHOD_lastIndexOf_String: %p\n", METHOD_lastIndexOf_String.id);
    printf("METHOD_length: %p\n", METHOD_length.id);
    printf("METHOD_lines: %p\n", METHOD_lines.id);
    printf("METHOD_matches: %p\n", METHOD_matches.id);
    printf("METHOD_offsetByCodePoints: %p\n", METHOD_offsetByCodePoints.id);
    printf("METHOD_regionMatches_5: %p\n", METHOD_regionMatches_5.id);
    printf("METHOD_regionMatches_4: %p\n", METHOD_regionMatches_4.id);
    printf("METHOD_repeat: %p\n", METHOD_repeat.id);
    printf("METHOD_replace_CharSequence_CharSequence: %p\n", METHOD_replace_CharSequence_CharSequence.id);
    printf("METHOD_replace_char_char: %p\n", METHOD_replace_char_char.id);
    printf("METHOD_replaceAll: %p\n", METHOD_replaceAll.id);
    printf("METHOD_replaceFirst: %p\n", METHOD_replaceFirst.id);
    printf("METHOD_split_2: %p\n", METHOD_split_2.id);
    printf("METHOD_split_1: %p\n", METHOD_split_1.id);
    printf("METHOD_startsWith_1: %p\n", METHOD_startsWith_1.id);
    printf("METHOD_startsWith_2: %p\n", METHOD_startsWith_2.id);
    printf("METHOD_strip: %p\n", METHOD_strip.id);
    printf("METHOD_stripIndent: %p\n", METHOD_stripIndent.id);
    printf("METHOD_stripLeading: %p\n", METHOD_stripLeading.id);
    printf("METHOD_stripTrailing: %p\n", METHOD_stripTrailing.id);
    printf("METHOD_subSequence: %p\n", METHOD_subSequence.id);
    printf("METHOD_substring_2: %p\n", METHOD_substring_2.id);
    printf("METHOD_substring_1: %p\n", METHOD_substring_1.id);
    printf("METHOD_toCharArray: %p\n", METHOD_toCharArray.id);
    printf("METHOD_toLowerCase: %p\n", METHOD_toLowerCase.id);
    printf("METHOD_toLowerCase_1: %p\n", METHOD_toLowerCase_1.id);
    printf("METHOD_toString: %p\n", METHOD_toString.id);
    printf("METHOD_toUpperCase: %p\n", METHOD_toUpperCase.id);
    printf("METHOD_toUpperCase_1: %p\n", METHOD_toUpperCase_1.id);
    printf("METHOD_transform: %p\n", METHOD_transform.id);
    printf("METHOD_translateEscapes: %p\n", METHOD_translateEscapes.id);
    printf("METHOD_trim: %p\n", METHOD_trim.id);
    printf("METHOD_valueOf_char: %p\n", METHOD_valueOf_char.id);
    printf("METHOD_valueOf_charArray_int_int: %p\n", METHOD_valueOf_charArray_int_int.id);
    printf("METHOD_valueOf_charArray: %p\n", METHOD_valueOf_charArray.id);
    printf("METHOD_valueOf_Object: %p\n", METHOD_valueOf_Object.id);
    printf("METHOD_valueOf_boolean: %p\n", METHOD_valueOf_boolean.id);
    printf("METHOD_valueOf_double: %p\n", METHOD_valueOf_double.id);
    printf("METHOD_valueOf_long: %p\n", METHOD_valueOf_long.id);
    printf("METHOD_valueOf_int: %p\n", METHOD_valueOf_int.id);
    printf("METHOD_valueOf_float: %p\n", METHOD_valueOf_float.id);
}
