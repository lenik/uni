#include "String.hxx"

using namespace java::lang;

thread_local String_class String::CLASS;

String::String(JNIEnv *env, jstring jobj) {
    this->_env = env;
    this->_jobj = (jstring) jobj;
}

String *String::_wrap(jstring jobj) {
    JNIEnv *env = getEnv();
    return new String(env, jobj);
}

/* ctor-create methods */
String::String(jbyteArray arg0, jint arg1, jint arg2, jobject arg3){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_int_int_Charset, arg0, arg1, arg2, arg3);
}

String::String(jbyteArray arg0, jstring arg1){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_String, arg0, arg1);
}

String::String(jbyteArray arg0, jobject arg1){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_Charset, arg0, arg1);
}

String::String(jbyteArray arg0, jint arg1, jint arg2){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_int_int, arg0, arg1, arg2);
}

String::String(jbyteArray arg0){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray, arg0);
}

String::String(jcharArray arg0, jint arg1, jint arg2){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_charArray_int_int, arg0, arg1, arg2);
}

String::String(jcharArray arg0){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_charArray, arg0);
}

String::String(jstring arg0){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_String, arg0);
}

String::String(){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jstring arg3){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_int_int_String, arg0, arg1, arg2, arg3);
}

String::String(jbyteArray arg0, jint arg1){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_int, arg0, arg1);
}

String::String(jbyteArray arg0, jint arg1, jint arg2, jint arg3){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_byteArray_int_int_int, arg0, arg1, arg2, arg3);
}

String::String(jintArray arg0, jint arg1, jint arg2){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jstring) newObject(_env, CLASS.INIT_intArray_int_int, arg0, arg1, arg2);
}

jchar String::charAt(jint arg0){
    jchar ret = _env->CallCharMethod(_jobj, CLASS.METHOD_charAt.id, arg0);
    return ret;
}

jobject String::chars(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_chars.id);
    return ret;
}

jint String::codePointAt(jint arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_codePointAt.id, arg0);
    return ret;
}

jint String::codePointBefore(jint arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_codePointBefore.id, arg0);
    return ret;
}

jint String::codePointCount(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_codePointCount.id, arg0, arg1);
    return ret;
}

jobject String::codePoints(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_codePoints.id);
    return ret;
}

jint String::compareTo_String(jstring arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_compareTo_String.id, arg0);
    return ret;
}

jint String::compareTo_Object(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_compareTo_Object.id, arg0);
    return ret;
}

jint String::compareToIgnoreCase(jstring arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_compareToIgnoreCase.id, arg0);
    return ret;
}

jstring String::concat(jstring arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_concat.id, arg0));
    return ret;
}

jboolean String::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contains.id, arg0);
    return ret;
}

jboolean String::contentEquals_CharSequence(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contentEquals_CharSequence.id, arg0);
    return ret;
}

jboolean String::contentEquals_StringBuffer(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contentEquals_StringBuffer.id, arg0);
    return ret;
}

jstring String::copyValueOf_1(jcharArray arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_copyValueOf_1.id, arg0));
    return ret;
}

jstring String::copyValueOf_3(jcharArray arg0, jint arg1, jint arg2){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_copyValueOf_3.id, arg0, arg1, arg2));
    return ret;
}

jobject String::describeConstable(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_describeConstable.id);
    return ret;
}

jboolean String::endsWith(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_endsWith.id, arg0);
    return ret;
}

jboolean String::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

jboolean String::equalsIgnoreCase(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equalsIgnoreCase.id, arg0);
    return ret;
}

jstring String::format_2(jstring arg0, jobjectArray arg1){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_format_2.id, arg0, arg1));
    return ret;
}

jstring String::format_3(jobject arg0, jstring arg1, jobjectArray arg2){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_format_3.id, arg0, arg1, arg2));
    return ret;
}

jstring String::formatted(jobjectArray arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_formatted.id, arg0));
    return ret;
}

jbyteArray String::getBytes(){
    jbyteArray ret = (jbyteArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getBytes.id));
    return ret;
}

jbyteArray String::getBytes_Charset(jobject arg0){
    jbyteArray ret = (jbyteArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getBytes_Charset.id, arg0));
    return ret;
}

jbyteArray String::getBytes_String(jstring arg0){
    jbyteArray ret = (jbyteArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getBytes_String.id, arg0));
    return ret;
}

void String::getBytes_int_int_byteArray_int(jint arg0, jint arg1, jbyteArray arg2, jint arg3){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_getBytes_int_int_byteArray_int.id, arg0, arg1, arg2, arg3);
}

void String::getChars(jint arg0, jint arg1, jcharArray arg2, jint arg3){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_getChars.id, arg0, arg1, arg2, arg3);
}

jint String::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

jstring String::indent(jint arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_indent.id, arg0));
    return ret;
}

jint String::indexOf_String(jstring arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf_String.id, arg0);
    return ret;
}

jint String::indexOf_String_int(jstring arg0, jint arg1){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf_String_int.id, arg0, arg1);
    return ret;
}

jint String::indexOf_int(jint arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf_int.id, arg0);
    return ret;
}

jint String::indexOf_int_int(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf_int_int.id, arg0, arg1);
    return ret;
}

jstring String::intern(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_intern.id));
    return ret;
}

jboolean String::isBlank(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isBlank.id);
    return ret;
}

jboolean String::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isEmpty.id);
    return ret;
}

jstring String::join_CharSequence_CharSequenceArray(jobject arg0, jarray arg1){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_join_CharSequence_CharSequenceArray.id, arg0, arg1));
    return ret;
}

jstring String::join_CharSequence_Iterable(jobject arg0, jobject arg1){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_join_CharSequence_Iterable.id, arg0, arg1));
    return ret;
}

jint String::lastIndexOf_int_int(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf_int_int.id, arg0, arg1);
    return ret;
}

jint String::lastIndexOf_int(jint arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf_int.id, arg0);
    return ret;
}

jint String::lastIndexOf_String_int(jstring arg0, jint arg1){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf_String_int.id, arg0, arg1);
    return ret;
}

jint String::lastIndexOf_String(jstring arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf_String.id, arg0);
    return ret;
}

jint String::length(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_length.id);
    return ret;
}

jobject String::lines(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_lines.id);
    return ret;
}

jboolean String::matches(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_matches.id, arg0);
    return ret;
}

jint String::offsetByCodePoints(jint arg0, jint arg1){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_offsetByCodePoints.id, arg0, arg1);
    return ret;
}

jboolean String::regionMatches_5(jboolean arg0, jint arg1, jstring arg2, jint arg3, jint arg4){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_regionMatches_5.id, arg0, arg1, arg2, arg3, arg4);
    return ret;
}

jboolean String::regionMatches_4(jint arg0, jstring arg1, jint arg2, jint arg3){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_regionMatches_4.id, arg0, arg1, arg2, arg3);
    return ret;
}

jstring String::repeat(jint arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_repeat.id, arg0));
    return ret;
}

jstring String::replace_CharSequence_CharSequence(jobject arg0, jobject arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_replace_CharSequence_CharSequence.id, arg0, arg1));
    return ret;
}

jstring String::replace_char_char(jchar arg0, jchar arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_replace_char_char.id, arg0, arg1));
    return ret;
}

jstring String::replaceAll(jstring arg0, jstring arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_replaceAll.id, arg0, arg1));
    return ret;
}

jstring String::replaceFirst(jstring arg0, jstring arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_replaceFirst.id, arg0, arg1));
    return ret;
}

jarray String::split_2(jstring arg0, jint arg1){
    jarray ret = (jarray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_split_2.id, arg0, arg1));
    return ret;
}

jarray String::split_1(jstring arg0){
    jarray ret = (jarray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_split_1.id, arg0));
    return ret;
}

jboolean String::startsWith_1(jstring arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_startsWith_1.id, arg0);
    return ret;
}

jboolean String::startsWith_2(jstring arg0, jint arg1){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_startsWith_2.id, arg0, arg1);
    return ret;
}

jstring String::strip(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_strip.id));
    return ret;
}

jstring String::stripIndent(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_stripIndent.id));
    return ret;
}

jstring String::stripLeading(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_stripLeading.id));
    return ret;
}

jstring String::stripTrailing(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_stripTrailing.id));
    return ret;
}

jobject String::subSequence(jint arg0, jint arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_subSequence.id, arg0, arg1);
    return ret;
}

jstring String::substring_2(jint arg0, jint arg1){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_substring_2.id, arg0, arg1));
    return ret;
}

jstring String::substring_1(jint arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_substring_1.id, arg0));
    return ret;
}

jcharArray String::toCharArray(){
    jcharArray ret = (jcharArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toCharArray.id));
    return ret;
}

jstring String::toLowerCase(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toLowerCase.id));
    return ret;
}

jstring String::toLowerCase_1(jobject arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toLowerCase_1.id, arg0));
    return ret;
}

jstring String::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toString.id));
    return ret;
}

jstring String::toUpperCase(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toUpperCase.id));
    return ret;
}

jstring String::toUpperCase_1(jobject arg0){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toUpperCase_1.id, arg0));
    return ret;
}

jobject String::transform(jobject arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_transform.id, arg0);
    return ret;
}

jstring String::translateEscapes(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_translateEscapes.id));
    return ret;
}

jstring String::trim(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_trim.id));
    return ret;
}

jstring String::valueOf_char(jchar arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_char.id, arg0));
    return ret;
}

jstring String::valueOf_charArray_int_int(jcharArray arg0, jint arg1, jint arg2){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_charArray_int_int.id, arg0, arg1, arg2));
    return ret;
}

jstring String::valueOf_charArray(jcharArray arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_charArray.id, arg0));
    return ret;
}

jstring String::valueOf_Object(jobject arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_Object.id, arg0));
    return ret;
}

jstring String::valueOf_boolean(jboolean arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_boolean.id, arg0));
    return ret;
}

jstring String::valueOf_double(jdouble arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_double.id, arg0));
    return ret;
}

jstring String::valueOf_long(jlong arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_long.id, arg0));
    return ret;
}

jstring String::valueOf_int(jint arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_int.id, arg0));
    return ret;
}

jstring String::valueOf_float(jfloat arg0){
    JNIEnv *env = getEnv();
    jstring ret = (jstring) (env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_valueOf_float.id, arg0));
    return ret;
}

