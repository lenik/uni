#include "String.hxx"

using namespace java::lang;

String::String(JNIEnv *env, jstring _this) {
    this->_this = _this;
    this->_env = env;
}

String *String::_wrap(jstring _this) {
    JNIEnv *env = getEnv();
    return new String(env, _this);
}

/* ctor-create methods */
String::String(jobject arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_StringBuffer, arg0);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jobject arg3){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_int_int_Charset, arg0, arg1, arg2, arg3);
}

String::String(jbyteArray arg0, jstring arg1){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_String, arg0, arg1);
}

String::String(jbyteArray arg0, jobject arg1){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_Charset, arg0, arg1);
}

String::String(jbyteArray arg0, jint arg1, jint arg2){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_int_int, arg0, arg1, arg2);
}

String::String(jbyteArray arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray, arg0);
}

String::String(jcharArray arg0, jint arg1, jint arg2){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_charArray_int_int, arg0, arg1, arg2);
}

String::String(jcharArray arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_charArray, arg0);
}

String::String(jstring arg0){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_String, arg0);
}

String::String(){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jstring arg3){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_int_int_String, arg0, arg1, arg2, arg3);
}

String::String(jbyteArray arg0, jint arg1){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_int, arg0, arg1);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jint arg3){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_byteArray_int_int_int, arg0, arg1, arg2, arg3);
}

String::String(jintArray arg0, jint arg1, jint arg2){
    jclass clazz = CLASS._class;
    this->_env = getEnv();
    this->_this = (jstring) newObject(_env, clazz, CLASS.INIT_intArray_int_int, arg0, arg1, arg2);
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
    FIELD_CASE_INSENSITIVE_ORDER = getStaticFieldID(env, _class, "CASE_INSENSITIVE_ORDER", "Ljava/util/Comparator;");
    INIT_StringBuffer = getMethodID(env, _class, "<init>", "(Ljava/lang/StringBuffer;)V");
    INIT_StringBuilder = getMethodID(env, _class, "<init>", "(Ljava/lang/StringBuilder;)V");
    INIT_byteArray_int_int_Charset = getMethodID(env, _class, "<init>", "([BIILjava/nio/charset/Charset;)V");
    INIT_byteArray_String = getMethodID(env, _class, "<init>", "([BLjava/lang/String;)V");
    INIT_byteArray_Charset = getMethodID(env, _class, "<init>", "([BLjava/nio/charset/Charset;)V");
    INIT_byteArray_int_int = getMethodID(env, _class, "<init>", "([BII)V");
    INIT_byteArray = getMethodID(env, _class, "<init>", "([B)V");
    INIT_charArray_int_int = getMethodID(env, _class, "<init>", "([CII)V");
    INIT_charArray = getMethodID(env, _class, "<init>", "([C)V");
    INIT_String = getMethodID(env, _class, "<init>", "(Ljava/lang/String;)V");
    INIT = getMethodID(env, _class, "<init>", "()V");
    INIT_byteArray_int_int_String = getMethodID(env, _class, "<init>", "([BIILjava/lang/String;)V");
    INIT_byteArray_int = getMethodID(env, _class, "<init>", "([BI)V");
    INIT_byteArray_int_int_int = getMethodID(env, _class, "<init>", "([BIII)V");
    INIT_intArray_int_int = getMethodID(env, _class, "<init>", "([III)V");
    METHOD_equals = getMethodID(env, _class, "equals", "(Ljava/lang/Object;)Z");
    METHOD_length = getMethodID(env, _class, "length", "()I");
    METHOD_toString = getMethodID(env, _class, "toString", "()Ljava/lang/String;");
    METHOD_hashCode = getMethodID(env, _class, "hashCode", "()I");
    METHOD_getChars = getMethodID(env, _class, "getChars", "(II[CI)V");
    METHOD_compareTo_String = getMethodID(env, _class, "compareTo", "(Ljava/lang/String;)I");
    METHOD_compareTo_Object = getMethodID(env, _class, "compareTo", "(Ljava/lang/Object;)I");
    METHOD_indexOf_String = getMethodID(env, _class, "indexOf", "(Ljava/lang/String;)I");
    METHOD_indexOf_String_int = getMethodID(env, _class, "indexOf", "(Ljava/lang/String;I)I");
    METHOD_indexOf_int = getMethodID(env, _class, "indexOf", "(I)I");
    METHOD_indexOf_int_int = getMethodID(env, _class, "indexOf", "(II)I");
    METHOD_valueOf_char = getStaticMethodID(env, _class, "valueOf", "(C)Ljava/lang/String;");
    METHOD_valueOf_charArray_int_int = getStaticMethodID(env, _class, "valueOf", "([CII)Ljava/lang/String;");
    METHOD_valueOf_charArray = getStaticMethodID(env, _class, "valueOf", "([C)Ljava/lang/String;");
    METHOD_valueOf_Object = getStaticMethodID(env, _class, "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_valueOf_boolean = getStaticMethodID(env, _class, "valueOf", "(Z)Ljava/lang/String;");
    METHOD_valueOf_double = getStaticMethodID(env, _class, "valueOf", "(D)Ljava/lang/String;");
    METHOD_valueOf_long = getStaticMethodID(env, _class, "valueOf", "(J)Ljava/lang/String;");
    METHOD_valueOf_int = getStaticMethodID(env, _class, "valueOf", "(I)Ljava/lang/String;");
    METHOD_valueOf_float = getStaticMethodID(env, _class, "valueOf", "(F)Ljava/lang/String;");
    METHOD_charAt = getMethodID(env, _class, "charAt", "(I)C");
    METHOD_codePointAt = getMethodID(env, _class, "codePointAt", "(I)I");
    METHOD_codePointBefore = getMethodID(env, _class, "codePointBefore", "(I)I");
    METHOD_codePointCount = getMethodID(env, _class, "codePointCount", "(II)I");
    METHOD_offsetByCodePoints = getMethodID(env, _class, "offsetByCodePoints", "(II)I");
    METHOD_getBytes = getMethodID(env, _class, "getBytes", "()[B");
    METHOD_getBytes_Charset = getMethodID(env, _class, "getBytes", "(Ljava/nio/charset/Charset;)[B");
    METHOD_getBytes_String = getMethodID(env, _class, "getBytes", "(Ljava/lang/String;)[B");
    METHOD_getBytes_int_int_byteArray_int = getMethodID(env, _class, "getBytes", "(II[BI)V");
    METHOD_contentEquals_CharSequence = getMethodID(env, _class, "contentEquals", "(Ljava/lang/CharSequence;)Z");
    METHOD_contentEquals_StringBuffer = getMethodID(env, _class, "contentEquals", "(Ljava/lang/StringBuffer;)Z");
    METHOD_regionMatches_5 = getMethodID(env, _class, "regionMatches", "(ZILjava/lang/String;II)Z");
    METHOD_regionMatches_4 = getMethodID(env, _class, "regionMatches", "(ILjava/lang/String;II)Z");
    METHOD_startsWith_1 = getMethodID(env, _class, "startsWith", "(Ljava/lang/String;)Z");
    METHOD_startsWith_2 = getMethodID(env, _class, "startsWith", "(Ljava/lang/String;I)Z");
    METHOD_lastIndexOf_int_int = getMethodID(env, _class, "lastIndexOf", "(II)I");
    METHOD_lastIndexOf_int = getMethodID(env, _class, "lastIndexOf", "(I)I");
    METHOD_lastIndexOf_String_int = getMethodID(env, _class, "lastIndexOf", "(Ljava/lang/String;I)I");
    METHOD_lastIndexOf_String = getMethodID(env, _class, "lastIndexOf", "(Ljava/lang/String;)I");
    METHOD_substring_2 = getMethodID(env, _class, "substring", "(II)Ljava/lang/String;");
    METHOD_substring_1 = getMethodID(env, _class, "substring", "(I)Ljava/lang/String;");
    METHOD_isEmpty = getMethodID(env, _class, "isEmpty", "()Z");
    METHOD_replace_CharSequence_CharSequence = getMethodID(env, _class, "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
    METHOD_replace_char_char = getMethodID(env, _class, "replace", "(CC)Ljava/lang/String;");
    METHOD_matches = getMethodID(env, _class, "matches", "(Ljava/lang/String;)Z");
    METHOD_replaceFirst = getMethodID(env, _class, "replaceFirst", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    METHOD_replaceAll = getMethodID(env, _class, "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    METHOD_split_2 = getMethodID(env, _class, "split", "(Ljava/lang/String;I)[Ljava/lang/String;");
    METHOD_split_1 = getMethodID(env, _class, "split", "(Ljava/lang/String;)[Ljava/lang/String;");
    METHOD_join_CharSequence_CharSequenceArray = getStaticMethodID(env, _class, "join", "(Ljava/lang/CharSequence;[Ljava/lang/CharSequence;)Ljava/lang/String;");
    METHOD_join_CharSequence_Iterable = getStaticMethodID(env, _class, "join", "(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;");
    METHOD_toLowerCase = getMethodID(env, _class, "toLowerCase", "()Ljava/lang/String;");
    METHOD_toLowerCase_1 = getMethodID(env, _class, "toLowerCase", "(Ljava/util/Locale;)Ljava/lang/String;");
    METHOD_toUpperCase = getMethodID(env, _class, "toUpperCase", "()Ljava/lang/String;");
    METHOD_toUpperCase_1 = getMethodID(env, _class, "toUpperCase", "(Ljava/util/Locale;)Ljava/lang/String;");
    METHOD_trim = getMethodID(env, _class, "trim", "()Ljava/lang/String;");
    METHOD_strip = getMethodID(env, _class, "strip", "()Ljava/lang/String;");
    METHOD_stripLeading = getMethodID(env, _class, "stripLeading", "()Ljava/lang/String;");
    METHOD_stripTrailing = getMethodID(env, _class, "stripTrailing", "()Ljava/lang/String;");
    METHOD_lines = getMethodID(env, _class, "lines", "()Ljava/util/stream/Stream;");
    METHOD_repeat = getMethodID(env, _class, "repeat", "(I)Ljava/lang/String;");
    METHOD_isBlank = getMethodID(env, _class, "isBlank", "()Z");
    METHOD_toCharArray = getMethodID(env, _class, "toCharArray", "()[C");
    METHOD_format_2 = getStaticMethodID(env, _class, "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_format_3 = getStaticMethodID(env, _class, "format", "(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_codePoints = getMethodID(env, _class, "codePoints", "()Ljava/util/stream/IntStream;");
    METHOD_equalsIgnoreCase = getMethodID(env, _class, "equalsIgnoreCase", "(Ljava/lang/String;)Z");
    METHOD_compareToIgnoreCase = getMethodID(env, _class, "compareToIgnoreCase", "(Ljava/lang/String;)I");
    METHOD_endsWith = getMethodID(env, _class, "endsWith", "(Ljava/lang/String;)Z");
    METHOD_subSequence = getMethodID(env, _class, "subSequence", "(II)Ljava/lang/CharSequence;");
    METHOD_concat = getMethodID(env, _class, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
    METHOD_contains = getMethodID(env, _class, "contains", "(Ljava/lang/CharSequence;)Z");
    METHOD_indent = getMethodID(env, _class, "indent", "(I)Ljava/lang/String;");
    METHOD_stripIndent = getMethodID(env, _class, "stripIndent", "()Ljava/lang/String;");
    METHOD_translateEscapes = getMethodID(env, _class, "translateEscapes", "()Ljava/lang/String;");
    METHOD_chars = getMethodID(env, _class, "chars", "()Ljava/util/stream/IntStream;");
    METHOD_transform = getMethodID(env, _class, "transform", "(Ljava/util/function/Function;)Ljava/lang/Object;");
    METHOD_formatted = getMethodID(env, _class, "formatted", "([Ljava/lang/Object;)Ljava/lang/String;");
    METHOD_copyValueOf_1 = getStaticMethodID(env, _class, "copyValueOf", "([C)Ljava/lang/String;");
    METHOD_copyValueOf_3 = getStaticMethodID(env, _class, "copyValueOf", "([CII)Ljava/lang/String;");
    METHOD_intern = getMethodID(env, _class, "intern", "()Ljava/lang/String;");
    METHOD_describeConstable = getMethodID(env, _class, "describeConstable", "()Ljava/util/Optional;");
}

typedef long numeric;

void String_class::dump() {
    printf("FIELD_CASE_INSENSITIVE_ORDER: %ld\n", (numeric) FIELD_CASE_INSENSITIVE_ORDER);
    printf("INIT_StringBuffer: %ld\n", (numeric) INIT_StringBuffer);
    printf("INIT_StringBuilder: %ld\n", (numeric) INIT_StringBuilder);
    printf("INIT_byteArray_int_int_Charset: %ld\n", (numeric) INIT_byteArray_int_int_Charset);
    printf("INIT_byteArray_String: %ld\n", (numeric) INIT_byteArray_String);
    printf("INIT_byteArray_Charset: %ld\n", (numeric) INIT_byteArray_Charset);
    printf("INIT_byteArray_int_int: %ld\n", (numeric) INIT_byteArray_int_int);
    printf("INIT_byteArray: %ld\n", (numeric) INIT_byteArray);
    printf("INIT_charArray_int_int: %ld\n", (numeric) INIT_charArray_int_int);
    printf("INIT_charArray: %ld\n", (numeric) INIT_charArray);
    printf("INIT_String: %ld\n", (numeric) INIT_String);
    printf("INIT: %ld\n", (numeric) INIT);
    printf("INIT_byteArray_int_int_String: %ld\n", (numeric) INIT_byteArray_int_int_String);
    printf("INIT_byteArray_int: %ld\n", (numeric) INIT_byteArray_int);
    printf("INIT_byteArray_int_int_int: %ld\n", (numeric) INIT_byteArray_int_int_int);
    printf("INIT_intArray_int_int: %ld\n", (numeric) INIT_intArray_int_int);
    printf("METHOD_equals: %ld\n", (numeric) METHOD_equals);
    printf("METHOD_length: %ld\n", (numeric) METHOD_length);
    printf("METHOD_toString: %ld\n", (numeric) METHOD_toString);
    printf("METHOD_hashCode: %ld\n", (numeric) METHOD_hashCode);
    printf("METHOD_getChars: %ld\n", (numeric) METHOD_getChars);
    printf("METHOD_compareTo_String: %ld\n", (numeric) METHOD_compareTo_String);
    printf("METHOD_compareTo_Object: %ld\n", (numeric) METHOD_compareTo_Object);
    printf("METHOD_indexOf_String: %ld\n", (numeric) METHOD_indexOf_String);
    printf("METHOD_indexOf_String_int: %ld\n", (numeric) METHOD_indexOf_String_int);
    printf("METHOD_indexOf_int: %ld\n", (numeric) METHOD_indexOf_int);
    printf("METHOD_indexOf_int_int: %ld\n", (numeric) METHOD_indexOf_int_int);
    printf("METHOD_valueOf_char: %ld\n", (numeric) METHOD_valueOf_char);
    printf("METHOD_valueOf_charArray_int_int: %ld\n", (numeric) METHOD_valueOf_charArray_int_int);
    printf("METHOD_valueOf_charArray: %ld\n", (numeric) METHOD_valueOf_charArray);
    printf("METHOD_valueOf_Object: %ld\n", (numeric) METHOD_valueOf_Object);
    printf("METHOD_valueOf_boolean: %ld\n", (numeric) METHOD_valueOf_boolean);
    printf("METHOD_valueOf_double: %ld\n", (numeric) METHOD_valueOf_double);
    printf("METHOD_valueOf_long: %ld\n", (numeric) METHOD_valueOf_long);
    printf("METHOD_valueOf_int: %ld\n", (numeric) METHOD_valueOf_int);
    printf("METHOD_valueOf_float: %ld\n", (numeric) METHOD_valueOf_float);
    printf("METHOD_charAt: %ld\n", (numeric) METHOD_charAt);
    printf("METHOD_codePointAt: %ld\n", (numeric) METHOD_codePointAt);
    printf("METHOD_codePointBefore: %ld\n", (numeric) METHOD_codePointBefore);
    printf("METHOD_codePointCount: %ld\n", (numeric) METHOD_codePointCount);
    printf("METHOD_offsetByCodePoints: %ld\n", (numeric) METHOD_offsetByCodePoints);
    printf("METHOD_getBytes: %ld\n", (numeric) METHOD_getBytes);
    printf("METHOD_getBytes_Charset: %ld\n", (numeric) METHOD_getBytes_Charset);
    printf("METHOD_getBytes_String: %ld\n", (numeric) METHOD_getBytes_String);
    printf("METHOD_getBytes_int_int_byteArray_int: %ld\n", (numeric) METHOD_getBytes_int_int_byteArray_int);
    printf("METHOD_contentEquals_CharSequence: %ld\n", (numeric) METHOD_contentEquals_CharSequence);
    printf("METHOD_contentEquals_StringBuffer: %ld\n", (numeric) METHOD_contentEquals_StringBuffer);
    printf("METHOD_regionMatches_5: %ld\n", (numeric) METHOD_regionMatches_5);
    printf("METHOD_regionMatches_4: %ld\n", (numeric) METHOD_regionMatches_4);
    printf("METHOD_startsWith_1: %ld\n", (numeric) METHOD_startsWith_1);
    printf("METHOD_startsWith_2: %ld\n", (numeric) METHOD_startsWith_2);
    printf("METHOD_lastIndexOf_int_int: %ld\n", (numeric) METHOD_lastIndexOf_int_int);
    printf("METHOD_lastIndexOf_int: %ld\n", (numeric) METHOD_lastIndexOf_int);
    printf("METHOD_lastIndexOf_String_int: %ld\n", (numeric) METHOD_lastIndexOf_String_int);
    printf("METHOD_lastIndexOf_String: %ld\n", (numeric) METHOD_lastIndexOf_String);
    printf("METHOD_substring_2: %ld\n", (numeric) METHOD_substring_2);
    printf("METHOD_substring_1: %ld\n", (numeric) METHOD_substring_1);
    printf("METHOD_isEmpty: %ld\n", (numeric) METHOD_isEmpty);
    printf("METHOD_replace_CharSequence_CharSequence: %ld\n", (numeric) METHOD_replace_CharSequence_CharSequence);
    printf("METHOD_replace_char_char: %ld\n", (numeric) METHOD_replace_char_char);
    printf("METHOD_matches: %ld\n", (numeric) METHOD_matches);
    printf("METHOD_replaceFirst: %ld\n", (numeric) METHOD_replaceFirst);
    printf("METHOD_replaceAll: %ld\n", (numeric) METHOD_replaceAll);
    printf("METHOD_split_2: %ld\n", (numeric) METHOD_split_2);
    printf("METHOD_split_1: %ld\n", (numeric) METHOD_split_1);
    printf("METHOD_join_CharSequence_CharSequenceArray: %ld\n", (numeric) METHOD_join_CharSequence_CharSequenceArray);
    printf("METHOD_join_CharSequence_Iterable: %ld\n", (numeric) METHOD_join_CharSequence_Iterable);
    printf("METHOD_toLowerCase: %ld\n", (numeric) METHOD_toLowerCase);
    printf("METHOD_toLowerCase_1: %ld\n", (numeric) METHOD_toLowerCase_1);
    printf("METHOD_toUpperCase: %ld\n", (numeric) METHOD_toUpperCase);
    printf("METHOD_toUpperCase_1: %ld\n", (numeric) METHOD_toUpperCase_1);
    printf("METHOD_trim: %ld\n", (numeric) METHOD_trim);
    printf("METHOD_strip: %ld\n", (numeric) METHOD_strip);
    printf("METHOD_stripLeading: %ld\n", (numeric) METHOD_stripLeading);
    printf("METHOD_stripTrailing: %ld\n", (numeric) METHOD_stripTrailing);
    printf("METHOD_lines: %ld\n", (numeric) METHOD_lines);
    printf("METHOD_repeat: %ld\n", (numeric) METHOD_repeat);
    printf("METHOD_isBlank: %ld\n", (numeric) METHOD_isBlank);
    printf("METHOD_toCharArray: %ld\n", (numeric) METHOD_toCharArray);
    printf("METHOD_format_2: %ld\n", (numeric) METHOD_format_2);
    printf("METHOD_format_3: %ld\n", (numeric) METHOD_format_3);
    printf("METHOD_codePoints: %ld\n", (numeric) METHOD_codePoints);
    printf("METHOD_equalsIgnoreCase: %ld\n", (numeric) METHOD_equalsIgnoreCase);
    printf("METHOD_compareToIgnoreCase: %ld\n", (numeric) METHOD_compareToIgnoreCase);
    printf("METHOD_endsWith: %ld\n", (numeric) METHOD_endsWith);
    printf("METHOD_subSequence: %ld\n", (numeric) METHOD_subSequence);
    printf("METHOD_concat: %ld\n", (numeric) METHOD_concat);
    printf("METHOD_contains: %ld\n", (numeric) METHOD_contains);
    printf("METHOD_indent: %ld\n", (numeric) METHOD_indent);
    printf("METHOD_stripIndent: %ld\n", (numeric) METHOD_stripIndent);
    printf("METHOD_translateEscapes: %ld\n", (numeric) METHOD_translateEscapes);
    printf("METHOD_chars: %ld\n", (numeric) METHOD_chars);
    printf("METHOD_transform: %ld\n", (numeric) METHOD_transform);
    printf("METHOD_formatted: %ld\n", (numeric) METHOD_formatted);
    printf("METHOD_copyValueOf_1: %ld\n", (numeric) METHOD_copyValueOf_1);
    printf("METHOD_copyValueOf_3: %ld\n", (numeric) METHOD_copyValueOf_3);
    printf("METHOD_intern: %ld\n", (numeric) METHOD_intern);
    printf("METHOD_describeConstable: %ld\n", (numeric) METHOD_describeConstable);
}
