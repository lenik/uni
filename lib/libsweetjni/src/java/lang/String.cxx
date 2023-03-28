#include "String.hxx"

using namespace java::lang;

String::String(JNIEnv *env, jobject _this) {
    this->_this = _this;
    this->_env = env;
}

String *String::_wrap(jobject _this) {
    JNIEnv *env = getEnv();
    return new String(env, _this);
}

/* ctor-create methods */
String::String(jobject arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_StringBuffer, arg0);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jobject arg3){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_int_int_Charset, arg0, arg1, arg2, arg3);
}

String::String(jbyteArray arg0, jstring arg1){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_String, arg0, arg1);
}

String::String(jbyteArray arg0, jobject arg1){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_Charset, arg0, arg1);
}

String::String(jbyteArray arg0, jint arg1, jint arg2){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_int_int, arg0, arg1, arg2);
}

String::String(jbyteArray arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray, arg0);
}

String::String(jcharArray arg0, jint arg1, jint arg2){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_charArray_int_int, arg0, arg1, arg2);
}

String::String(jcharArray arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_charArray, arg0);
}

String::String(jstring arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_String, arg0);
}

String::String(){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jstring arg3){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_int_int_String, arg0, arg1, arg2, arg3);
}

String::String(jbyteArray arg0, jint arg1){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_int, arg0, arg1);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jint arg3){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_byteArray_int_int_int, arg0, arg1, arg2, arg3);
}

String::String(jintArray arg0, jint arg1, jint arg2){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = newObject(_env, clazz, CLASS.INIT_intArray_int_int, arg0, arg1, arg2);
}

jboolean String::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_equals, arg0);
    return ret;
}

jint String::length(){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_length);
    return ret;
}

jstring String::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_toString));
    return ret;
}

jint String::hashCode(){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_hashCode);
    return ret;
}

void String::getChars(jint arg0, jint arg1, jcharArray arg2, jint arg3){
    _env->CallObjectMethod(_this, CLASS.METHOD_getChars, arg0, arg1, arg2, arg3);
}

jint String::compareTo_String(jstring arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_compareTo_String, arg0);
    return ret;
}

jint String::compareTo_Object(jobject arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_compareTo_Object, arg0);
    return ret;
}

jint String::indexOf_String(jstring arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_indexOf_String, arg0);
    return ret;
}

jint String::indexOf_String_int(jstring arg0, jint arg1){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_indexOf_String_int, arg0, arg1);
    return ret;
}

jint String::indexOf_int(jint arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_indexOf_int, arg0);
    return ret;
}

jint String::indexOf_int_int(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_indexOf_int_int, arg0, arg1);
    return ret;
}

jstring String::valueOf_char(jchar arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_char, arg0));
    return ret;
}

jstring String::valueOf_charArray_int_int(jcharArray arg0, jint arg1, jint arg2){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_charArray_int_int, arg0, arg1, arg2));
    return ret;
}

jstring String::valueOf_charArray(jcharArray arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_charArray, arg0));
    return ret;
}

jstring String::valueOf_Object(jobject arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_Object, arg0));
    return ret;
}

jstring String::valueOf_boolean(jboolean arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_boolean, arg0));
    return ret;
}

jstring String::valueOf_double(jdouble arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_double, arg0));
    return ret;
}

jstring String::valueOf_long(jlong arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_long, arg0));
    return ret;
}

jstring String::valueOf_int(jint arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_int, arg0));
    return ret;
}

jstring String::valueOf_float(jfloat arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_float, arg0));
    return ret;
}

jchar String::charAt(jint arg0){
    jchar ret = _env->CallCharMethod(_this, CLASS.METHOD_charAt, arg0);
    return ret;
}

jint String::codePointAt(jint arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_codePointAt, arg0);
    return ret;
}

jint String::codePointBefore(jint arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_codePointBefore, arg0);
    return ret;
}

jint String::codePointCount(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_codePointCount, arg0, arg1);
    return ret;
}

jint String::offsetByCodePoints(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_offsetByCodePoints, arg0, arg1);
    return ret;
}

jbyteArray String::getBytes(){
    jbyteArray ret = (jbyteArray) (_env->CallObjectMethod(_this, CLASS.METHOD_getBytes));
    return ret;
}

jbyteArray String::getBytes_Charset(jobject arg0){
    jbyteArray ret = (jbyteArray) (_env->CallObjectMethod(_this, CLASS.METHOD_getBytes_Charset, arg0));
    return ret;
}

jbyteArray String::getBytes_String(jstring arg0){
    jbyteArray ret = (jbyteArray) (_env->CallObjectMethod(_this, CLASS.METHOD_getBytes_String, arg0));
    return ret;
}

void String::getBytes_int_int_byteArray_int(jint arg0, jint arg1, jbyteArray arg2, jint arg3){
    _env->CallObjectMethod(_this, CLASS.METHOD_getBytes_int_int_byteArray_int, arg0, arg1, arg2, arg3);
}

jboolean String::contentEquals_CharSequence(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_contentEquals_CharSequence, arg0);
    return ret;
}

jboolean String::contentEquals_StringBuffer(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_contentEquals_StringBuffer, arg0);
    return ret;
}

jboolean String::regionMatches_5(jboolean arg0, jint arg1, jstring arg2, jint arg3, jint arg4){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_regionMatches_5, arg0, arg1, arg2, arg3, arg4);
    return ret;
}

jboolean String::regionMatches_4(jint arg0, jstring arg1, jint arg2, jint arg3){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_regionMatches_4, arg0, arg1, arg2, arg3);
    return ret;
}

jboolean String::startsWith_1(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_startsWith_1, arg0);
    return ret;
}

jboolean String::startsWith_2(jstring arg0, jint arg1){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_startsWith_2, arg0, arg1);
    return ret;
}

jint String::lastIndexOf_int_int(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_lastIndexOf_int_int, arg0, arg1);
    return ret;
}

jint String::lastIndexOf_int(jint arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_lastIndexOf_int, arg0);
    return ret;
}

jint String::lastIndexOf_String_int(jstring arg0, jint arg1){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_lastIndexOf_String_int, arg0, arg1);
    return ret;
}

jint String::lastIndexOf_String(jstring arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_lastIndexOf_String, arg0);
    return ret;
}

jstring String::substring_2(jint arg0, jint arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_substring_2, arg0, arg1));
    return ret;
}

jstring String::substring_1(jint arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_substring_1, arg0));
    return ret;
}

jboolean String::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_isEmpty);
    return ret;
}

jstring String::replace_CharSequence_CharSequence(jobject arg0, jobject arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_replace_CharSequence_CharSequence, arg0, arg1));
    return ret;
}

jstring String::replace_char_char(jchar arg0, jchar arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_replace_char_char, arg0, arg1));
    return ret;
}

jboolean String::matches(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_matches, arg0);
    return ret;
}

jstring String::replaceFirst(jstring arg0, jstring arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_replaceFirst, arg0, arg1));
    return ret;
}

jstring String::replaceAll(jstring arg0, jstring arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_replaceAll, arg0, arg1));
    return ret;
}

jarray String::split_2(jstring arg0, jint arg1){
    jarray ret = (jarray) (_env->CallObjectMethod(_this, CLASS.METHOD_split_2, arg0, arg1));
    return ret;
}

jarray String::split_1(jstring arg0){
    jarray ret = (jarray) (_env->CallObjectMethod(_this, CLASS.METHOD_split_1, arg0));
    return ret;
}

jstring String::join_CharSequence_CharSequenceArray(jobject arg0, jarray arg1){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_join_CharSequence_CharSequenceArray, arg0, arg1));
    return ret;
}

jstring String::join_CharSequence_Iterable(jobject arg0, jobject arg1){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_join_CharSequence_Iterable, arg0, arg1));
    return ret;
}

jstring String::toLowerCase(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_toLowerCase));
    return ret;
}

jstring String::toLowerCase_1(jobject arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_toLowerCase_1, arg0));
    return ret;
}

jstring String::toUpperCase(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_toUpperCase));
    return ret;
}

jstring String::toUpperCase_1(jobject arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_toUpperCase_1, arg0));
    return ret;
}

jstring String::trim(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_trim));
    return ret;
}

jstring String::strip(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_strip));
    return ret;
}

jstring String::stripLeading(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_stripLeading));
    return ret;
}

jstring String::stripTrailing(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_stripTrailing));
    return ret;
}

jobject String::lines(){
    jobject ret = _env->CallObjectMethod(_this, CLASS.METHOD_lines);
    return ret;
}

jstring String::repeat(jint arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_repeat, arg0));
    return ret;
}

jboolean String::isBlank(){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_isBlank);
    return ret;
}

jcharArray String::toCharArray(){
    jcharArray ret = (jcharArray) (_env->CallObjectMethod(_this, CLASS.METHOD_toCharArray));
    return ret;
}

jstring String::format_2(jstring arg0, jobjectArray arg1){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_format_2, arg0, arg1));
    return ret;
}

jstring String::format_3(jobject arg0, jstring arg1, jobjectArray arg2){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_format_3, arg0, arg1, arg2));
    return ret;
}

jobject String::codePoints(){
    jobject ret = _env->CallObjectMethod(_this, CLASS.METHOD_codePoints);
    return ret;
}

jboolean String::equalsIgnoreCase(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_equalsIgnoreCase, arg0);
    return ret;
}

jint String::compareToIgnoreCase(jstring arg0){
    jint ret = _env->CallIntMethod(_this, CLASS.METHOD_compareToIgnoreCase, arg0);
    return ret;
}

jboolean String::endsWith(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_endsWith, arg0);
    return ret;
}

jobject String::subSequence(jint arg0, jint arg1){
    jobject ret = _env->CallObjectMethod(_this, CLASS.METHOD_subSequence, arg0, arg1);
    return ret;
}

jstring String::concat(jstring arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_concat, arg0));
    return ret;
}

jboolean String::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_this, CLASS.METHOD_contains, arg0);
    return ret;
}

jstring String::indent(jint arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_indent, arg0));
    return ret;
}

jstring String::stripIndent(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_stripIndent));
    return ret;
}

jstring String::translateEscapes(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_translateEscapes));
    return ret;
}

jobject String::chars(){
    jobject ret = _env->CallObjectMethod(_this, CLASS.METHOD_chars);
    return ret;
}

jobject String::transform(jobject arg0){
    jobject ret = _env->CallObjectMethod(_this, CLASS.METHOD_transform, arg0);
    return ret;
}

jstring String::formatted(jobjectArray arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_formatted, arg0));
    return ret;
}

jstring String::copyValueOf_1(jcharArray arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_copyValueOf_1, arg0));
    return ret;
}

jstring String::copyValueOf_3(jcharArray arg0, jint arg1, jint arg2){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_copyValueOf_3, arg0, arg1, arg2));
    return ret;
}

jstring String::intern(){
    jstring ret = (jstring) (_env->CallObjectMethod(_this, CLASS.METHOD_intern));
    return ret;
}

jobject String::describeConstable(){
    jobject ret = _env->CallObjectMethod(_this, CLASS.METHOD_describeConstable);
    return ret;
}

thread_local String_class String::CLASS;

String_class::String_class() {
    JNIEnv *env = getEnv();
    if (env == NULL) return;
    _class = findClass(env, "java/lang/String");
    if (_class == NULL) return;
    FIELD_CASE_INSENSITIVE_ORDER = env->GetFieldID(_class, "CASE_INSENSITIVE_ORDER", "Ljava/util/Comparator;");
    INIT_StringBuffer = env->GetMethodID(_class, "<init>", "(Ljava/lang/StringBuffer;)V");
    INIT_StringBuilder = env->GetMethodID(_class, "<init>", "(Ljava/lang/StringBuilder;)V");
    INIT_byteArray_int_int_Charset = env->GetMethodID(_class, "<init>", "([BIILjava/nio/charset/Charset;)V");
    INIT_byteArray_String = env->GetMethodID(_class, "<init>", "([BLjava/lang/String;)V");
    INIT_byteArray_Charset = env->GetMethodID(_class, "<init>", "([BLjava/nio/charset/Charset;)V");
    INIT_byteArray_int_int = env->GetMethodID(_class, "<init>", "([BII)V");
    INIT_byteArray = env->GetMethodID(_class, "<init>", "([B)V");
    INIT_charArray_int_int = env->GetMethodID(_class, "<init>", "([CII)V");
    INIT_charArray = env->GetMethodID(_class, "<init>", "([C)V");
    INIT_String = env->GetMethodID(_class, "<init>", "(Ljava/lang/String;)V");
    INIT = env->GetMethodID(_class, "<init>", "()V");
    INIT_byteArray_int_int_String = env->GetMethodID(_class, "<init>", "([BIILjava/lang/String;)V");
    INIT_byteArray_int = env->GetMethodID(_class, "<init>", "([BI)V");
    INIT_byteArray_int_int_int = env->GetMethodID(_class, "<init>", "([BIII)V");
    INIT_intArray_int_int = env->GetMethodID(_class, "<init>", "([III)V");
    METHOD_equals = env->GetMethodID(_class, "equals", "(Ljava/lang/Object;)Z");
    METHOD_length = env->GetMethodID(_class, "length", "()I");
    METHOD_toString = env->GetMethodID(_class, "toString", "()Ljava/lang/String;");
    METHOD_hashCode = env->GetMethodID(_class, "hashCode", "()I");
    METHOD_getChars = env->GetMethodID(_class, "getChars", "(II[CI)V");
    METHOD_compareTo_String = env->GetMethodID(_class, "compareTo", "(Ljava/lang/String;)I");
    METHOD_compareTo_Object = env->GetMethodID(_class, "compareTo", "(Ljava/lang/Object;)I");
    METHOD_indexOf_String = env->GetMethodID(_class, "indexOf", "(Ljava/lang/String;)I");
    METHOD_indexOf_String_int = env->GetMethodID(_class, "indexOf", "(Ljava/lang/String;I)I");
    METHOD_indexOf_int = env->GetMethodID(_class, "indexOf", "(I)I");
    METHOD_indexOf_int_int = env->GetMethodID(_class, "indexOf", "(II)I");
    METHOD_valueOf_char = env->GetStaticMethodID(_class, "valueOf", "(C)Ljava/lang/String;");
    METHOD_valueOf_charArray_int_int = env->GetStaticMethodID(_class, "valueOf", "([CII)Ljava/lang/String;");
    METHOD_valueOf_charArray = env->GetStaticMethodID(_class, "valueOf", "([C)Ljava/lang/String;");
    METHOD_valueOf_Object = env->GetStaticMethodID(_class, "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_valueOf_boolean = env->GetStaticMethodID(_class, "valueOf", "(Z)Ljava/lang/String;");
    METHOD_valueOf_double = env->GetStaticMethodID(_class, "valueOf", "(D)Ljava/lang/String;");
    METHOD_valueOf_long = env->GetStaticMethodID(_class, "valueOf", "(J)Ljava/lang/String;");
    METHOD_valueOf_int = env->GetStaticMethodID(_class, "valueOf", "(I)Ljava/lang/String;");
    METHOD_valueOf_float = env->GetStaticMethodID(_class, "valueOf", "(F)Ljava/lang/String;");
    METHOD_charAt = env->GetMethodID(_class, "charAt", "(I)C");
    METHOD_codePointAt = env->GetMethodID(_class, "codePointAt", "(I)I");
    METHOD_codePointBefore = env->GetMethodID(_class, "codePointBefore", "(I)I");
    METHOD_codePointCount = env->GetMethodID(_class, "codePointCount", "(II)I");
    METHOD_offsetByCodePoints = env->GetMethodID(_class, "offsetByCodePoints", "(II)I");
    METHOD_getBytes = env->GetMethodID(_class, "getBytes", "()[B");
    METHOD_getBytes_Charset = env->GetMethodID(_class, "getBytes", "(Ljava/nio/charset/Charset;)[B");
    METHOD_getBytes_String = env->GetMethodID(_class, "getBytes", "(Ljava/lang/String;)[B");
    METHOD_getBytes_int_int_byteArray_int = env->GetMethodID(_class, "getBytes", "(II[BI)V");
    METHOD_contentEquals_CharSequence = env->GetMethodID(_class, "contentEquals", "(Ljava/lang/CharSequence;)Z");
    METHOD_contentEquals_StringBuffer = env->GetMethodID(_class, "contentEquals", "(Ljava/lang/StringBuffer;)Z");
    METHOD_regionMatches_5 = env->GetMethodID(_class, "regionMatches", "(ZILjava/lang/String;II)Z");
    METHOD_regionMatches_4 = env->GetMethodID(_class, "regionMatches", "(ILjava/lang/String;II)Z");
    METHOD_startsWith_1 = env->GetMethodID(_class, "startsWith", "(Ljava/lang/String;)Z");
    METHOD_startsWith_2 = env->GetMethodID(_class, "startsWith", "(Ljava/lang/String;I)Z");
    METHOD_lastIndexOf_int_int = env->GetMethodID(_class, "lastIndexOf", "(II)I");
    METHOD_lastIndexOf_int = env->GetMethodID(_class, "lastIndexOf", "(I)I");
    METHOD_lastIndexOf_String_int = env->GetMethodID(_class, "lastIndexOf", "(Ljava/lang/String;I)I");
    METHOD_lastIndexOf_String = env->GetMethodID(_class, "lastIndexOf", "(Ljava/lang/String;)I");
    METHOD_substring_2 = env->GetMethodID(_class, "substring", "(II)Ljava/lang/String;");
    METHOD_substring_1 = env->GetMethodID(_class, "substring", "(I)Ljava/lang/String;");
    METHOD_isEmpty = env->GetMethodID(_class, "isEmpty", "()Z");
    METHOD_replace_CharSequence_CharSequence = env->GetMethodID(_class, "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
    METHOD_replace_char_char = env->GetMethodID(_class, "replace", "(CC)Ljava/lang/String;");
    METHOD_matches = env->GetMethodID(_class, "matches", "(Ljava/lang/String;)Z");
    METHOD_replaceFirst = env->GetMethodID(_class, "replaceFirst", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    METHOD_replaceAll = env->GetMethodID(_class, "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    METHOD_split_2 = env->GetMethodID(_class, "split", "(Ljava/lang/String;I)[Ljava/lang/String;");
    METHOD_split_1 = env->GetMethodID(_class, "split", "(Ljava/lang/String;)[Ljava/lang/String;");
    METHOD_join_CharSequence_CharSequenceArray = env->GetStaticMethodID(_class, "join", "(Ljava/lang/CharSequence;[Ljava/lang/CharSequence;)Ljava/lang/String;");
    METHOD_join_CharSequence_Iterable = env->GetStaticMethodID(_class, "join", "(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;");
    METHOD_toLowerCase = env->GetMethodID(_class, "toLowerCase", "()Ljava/lang/String;");
    METHOD_toLowerCase_1 = env->GetMethodID(_class, "toLowerCase", "(Ljava/util/Locale;)Ljava/lang/String;");
    METHOD_toUpperCase = env->GetMethodID(_class, "toUpperCase", "()Ljava/lang/String;");
    METHOD_toUpperCase_1 = env->GetMethodID(_class, "toUpperCase", "(Ljava/util/Locale;)Ljava/lang/String;");
    METHOD_trim = env->GetMethodID(_class, "trim", "()Ljava/lang/String;");
    METHOD_strip = env->GetMethodID(_class, "strip", "()Ljava/lang/String;");
    METHOD_stripLeading = env->GetMethodID(_class, "stripLeading", "()Ljava/lang/String;");
    METHOD_stripTrailing = env->GetMethodID(_class, "stripTrailing", "()Ljava/lang/String;");
    METHOD_lines = env->GetMethodID(_class, "lines", "()Ljava/util/stream/Stream;");
    METHOD_repeat = env->GetMethodID(_class, "repeat", "(I)Ljava/lang/String;");
    METHOD_isBlank = env->GetMethodID(_class, "isBlank", "()Z");
    METHOD_toCharArray = env->GetMethodID(_class, "toCharArray", "()[C");
    METHOD_format_2 = env->GetStaticMethodID(_class, "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_format_3 = env->GetStaticMethodID(_class, "format", "(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_codePoints = env->GetMethodID(_class, "codePoints", "()Ljava/util/stream/IntStream;");
    METHOD_equalsIgnoreCase = env->GetMethodID(_class, "equalsIgnoreCase", "(Ljava/lang/String;)Z");
    METHOD_compareToIgnoreCase = env->GetMethodID(_class, "compareToIgnoreCase", "(Ljava/lang/String;)I");
    METHOD_endsWith = env->GetMethodID(_class, "endsWith", "(Ljava/lang/String;)Z");
    METHOD_subSequence = env->GetMethodID(_class, "subSequence", "(II)Ljava/lang/CharSequence;");
    METHOD_concat = env->GetMethodID(_class, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
    METHOD_contains = env->GetMethodID(_class, "contains", "(Ljava/lang/CharSequence;)Z");
    METHOD_indent = env->GetMethodID(_class, "indent", "(I)Ljava/lang/String;");
    METHOD_stripIndent = env->GetMethodID(_class, "stripIndent", "()Ljava/lang/String;");
    METHOD_translateEscapes = env->GetMethodID(_class, "translateEscapes", "()Ljava/lang/String;");
    METHOD_chars = env->GetMethodID(_class, "chars", "()Ljava/util/stream/IntStream;");
    METHOD_transform = env->GetMethodID(_class, "transform", "(Ljava/util/function/Function;)Ljava/lang/Object;");
    METHOD_formatted = env->GetMethodID(_class, "formatted", "([Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_copyValueOf_1 = env->GetStaticMethodID(_class, "copyValueOf", "([C)Ljava/lang/String;");
    METHOD_copyValueOf_3 = env->GetStaticMethodID(_class, "copyValueOf", "([CII)Ljava/lang/String;");
    METHOD_intern = env->GetMethodID(_class, "intern", "()Ljava/lang/String;");
    METHOD_describeConstable = env->GetMethodID(_class, "describeConstable", "()Ljava/util/Optional;");
}

void String_class::dump() {
    printf("FIELD_CASE_INSENSITIVE_ORDER: %d\n", FIELD_CASE_INSENSITIVE_ORDER);
    printf("INIT_StringBuffer: %d\n", INIT_StringBuffer);
    printf("INIT_StringBuilder: %d\n", INIT_StringBuilder);
    printf("INIT_byteArray_int_int_Charset: %d\n", INIT_byteArray_int_int_Charset);
    printf("INIT_byteArray_String: %d\n", INIT_byteArray_String);
    printf("INIT_byteArray_Charset: %d\n", INIT_byteArray_Charset);
    printf("INIT_byteArray_int_int: %d\n", INIT_byteArray_int_int);
    printf("INIT_byteArray: %d\n", INIT_byteArray);
    printf("INIT_charArray_int_int: %d\n", INIT_charArray_int_int);
    printf("INIT_charArray: %d\n", INIT_charArray);
    printf("INIT_String: %d\n", INIT_String);
    printf("INIT: %d\n", INIT);
    printf("INIT_byteArray_int_int_String: %d\n", INIT_byteArray_int_int_String);
    printf("INIT_byteArray_int: %d\n", INIT_byteArray_int);
    printf("INIT_byteArray_int_int_int: %d\n", INIT_byteArray_int_int_int);
    printf("INIT_intArray_int_int: %d\n", INIT_intArray_int_int);
    printf("METHOD_equals: %d\n", METHOD_equals);
    printf("METHOD_length: %d\n", METHOD_length);
    printf("METHOD_toString: %d\n", METHOD_toString);
    printf("METHOD_hashCode: %d\n", METHOD_hashCode);
    printf("METHOD_getChars: %d\n", METHOD_getChars);
    printf("METHOD_compareTo_String: %d\n", METHOD_compareTo_String);
    printf("METHOD_compareTo_Object: %d\n", METHOD_compareTo_Object);
    printf("METHOD_indexOf_String: %d\n", METHOD_indexOf_String);
    printf("METHOD_indexOf_String_int: %d\n", METHOD_indexOf_String_int);
    printf("METHOD_indexOf_int: %d\n", METHOD_indexOf_int);
    printf("METHOD_indexOf_int_int: %d\n", METHOD_indexOf_int_int);
    printf("METHOD_valueOf_char: %d\n", METHOD_valueOf_char);
    printf("METHOD_valueOf_charArray_int_int: %d\n", METHOD_valueOf_charArray_int_int);
    printf("METHOD_valueOf_charArray: %d\n", METHOD_valueOf_charArray);
    printf("METHOD_valueOf_Object: %d\n", METHOD_valueOf_Object);
    printf("METHOD_valueOf_boolean: %d\n", METHOD_valueOf_boolean);
    printf("METHOD_valueOf_double: %d\n", METHOD_valueOf_double);
    printf("METHOD_valueOf_long: %d\n", METHOD_valueOf_long);
    printf("METHOD_valueOf_int: %d\n", METHOD_valueOf_int);
    printf("METHOD_valueOf_float: %d\n", METHOD_valueOf_float);
    printf("METHOD_charAt: %d\n", METHOD_charAt);
    printf("METHOD_codePointAt: %d\n", METHOD_codePointAt);
    printf("METHOD_codePointBefore: %d\n", METHOD_codePointBefore);
    printf("METHOD_codePointCount: %d\n", METHOD_codePointCount);
    printf("METHOD_offsetByCodePoints: %d\n", METHOD_offsetByCodePoints);
    printf("METHOD_getBytes: %d\n", METHOD_getBytes);
    printf("METHOD_getBytes_Charset: %d\n", METHOD_getBytes_Charset);
    printf("METHOD_getBytes_String: %d\n", METHOD_getBytes_String);
    printf("METHOD_getBytes_int_int_byteArray_int: %d\n", METHOD_getBytes_int_int_byteArray_int);
    printf("METHOD_contentEquals_CharSequence: %d\n", METHOD_contentEquals_CharSequence);
    printf("METHOD_contentEquals_StringBuffer: %d\n", METHOD_contentEquals_StringBuffer);
    printf("METHOD_regionMatches_5: %d\n", METHOD_regionMatches_5);
    printf("METHOD_regionMatches_4: %d\n", METHOD_regionMatches_4);
    printf("METHOD_startsWith_1: %d\n", METHOD_startsWith_1);
    printf("METHOD_startsWith_2: %d\n", METHOD_startsWith_2);
    printf("METHOD_lastIndexOf_int_int: %d\n", METHOD_lastIndexOf_int_int);
    printf("METHOD_lastIndexOf_int: %d\n", METHOD_lastIndexOf_int);
    printf("METHOD_lastIndexOf_String_int: %d\n", METHOD_lastIndexOf_String_int);
    printf("METHOD_lastIndexOf_String: %d\n", METHOD_lastIndexOf_String);
    printf("METHOD_substring_2: %d\n", METHOD_substring_2);
    printf("METHOD_substring_1: %d\n", METHOD_substring_1);
    printf("METHOD_isEmpty: %d\n", METHOD_isEmpty);
    printf("METHOD_replace_CharSequence_CharSequence: %d\n", METHOD_replace_CharSequence_CharSequence);
    printf("METHOD_replace_char_char: %d\n", METHOD_replace_char_char);
    printf("METHOD_matches: %d\n", METHOD_matches);
    printf("METHOD_replaceFirst: %d\n", METHOD_replaceFirst);
    printf("METHOD_replaceAll: %d\n", METHOD_replaceAll);
    printf("METHOD_split_2: %d\n", METHOD_split_2);
    printf("METHOD_split_1: %d\n", METHOD_split_1);
    printf("METHOD_join_CharSequence_CharSequenceArray: %d\n", METHOD_join_CharSequence_CharSequenceArray);
    printf("METHOD_join_CharSequence_Iterable: %d\n", METHOD_join_CharSequence_Iterable);
    printf("METHOD_toLowerCase: %d\n", METHOD_toLowerCase);
    printf("METHOD_toLowerCase_1: %d\n", METHOD_toLowerCase_1);
    printf("METHOD_toUpperCase: %d\n", METHOD_toUpperCase);
    printf("METHOD_toUpperCase_1: %d\n", METHOD_toUpperCase_1);
    printf("METHOD_trim: %d\n", METHOD_trim);
    printf("METHOD_strip: %d\n", METHOD_strip);
    printf("METHOD_stripLeading: %d\n", METHOD_stripLeading);
    printf("METHOD_stripTrailing: %d\n", METHOD_stripTrailing);
    printf("METHOD_lines: %d\n", METHOD_lines);
    printf("METHOD_repeat: %d\n", METHOD_repeat);
    printf("METHOD_isBlank: %d\n", METHOD_isBlank);
    printf("METHOD_toCharArray: %d\n", METHOD_toCharArray);
    printf("METHOD_format_2: %d\n", METHOD_format_2);
    printf("METHOD_format_3: %d\n", METHOD_format_3);
    printf("METHOD_codePoints: %d\n", METHOD_codePoints);
    printf("METHOD_equalsIgnoreCase: %d\n", METHOD_equalsIgnoreCase);
    printf("METHOD_compareToIgnoreCase: %d\n", METHOD_compareToIgnoreCase);
    printf("METHOD_endsWith: %d\n", METHOD_endsWith);
    printf("METHOD_subSequence: %d\n", METHOD_subSequence);
    printf("METHOD_concat: %d\n", METHOD_concat);
    printf("METHOD_contains: %d\n", METHOD_contains);
    printf("METHOD_indent: %d\n", METHOD_indent);
    printf("METHOD_stripIndent: %d\n", METHOD_stripIndent);
    printf("METHOD_translateEscapes: %d\n", METHOD_translateEscapes);
    printf("METHOD_chars: %d\n", METHOD_chars);
    printf("METHOD_transform: %d\n", METHOD_transform);
    printf("METHOD_formatted: %d\n", METHOD_formatted);
    printf("METHOD_copyValueOf_1: %d\n", METHOD_copyValueOf_1);
    printf("METHOD_copyValueOf_3: %d\n", METHOD_copyValueOf_3);
    printf("METHOD_intern: %d\n", METHOD_intern);
    printf("METHOD_describeConstable: %d\n", METHOD_describeConstable);
}
