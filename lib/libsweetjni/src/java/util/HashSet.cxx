#include "HashSet.hxx"

using namespace java::util;

thread_local HashSet_class HashSet::CLASS;

HashSet::HashSet(JNIEnv *env)
        : Object(env) {}

HashSet::HashSet(JNIEnv *env, jobject jobj)
        : Object(env, jobj) {}

HashSet *HashSet::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new HashSet(env, jobj);
}

/* ctor-create methods */
HashSet::HashSet(jint arg0) {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT_int, arg0);
}

HashSet::HashSet(jint arg0, jfloat arg1) {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT_int_float, arg0, arg1);
}

HashSet::HashSet(jobject arg0) {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT_Collection, arg0);
}

HashSet::HashSet() {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT);
}

jboolean HashSet::add(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_add.id, arg0);
    return ret;
}

jboolean HashSet::addAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll.id, arg0);
    return ret;
}

void HashSet::clear(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_clear.id);
}

jobject HashSet::clone(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_clone.id);
    return ret;
}

jboolean HashSet::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contains.id, arg0);
    return ret;
}

jboolean HashSet::containsAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_containsAll.id, arg0);
    return ret;
}

jboolean HashSet::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

void HashSet::forEach(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_forEach.id, arg0);
}

jint HashSet::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

jboolean HashSet::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isEmpty.id);
    return ret;
}

jobject HashSet::iterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_iterator.id);
    return ret;
}

jobject HashSet::newHashSet(jint arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_newHashSet.id, arg0);
    return ret;
}

jobject HashSet::parallelStream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_parallelStream.id);
    return ret;
}

jboolean HashSet::remove(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_remove.id, arg0);
    return ret;
}

jboolean HashSet::removeAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeAll.id, arg0);
    return ret;
}

jboolean HashSet::removeIf(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeIf.id, arg0);
    return ret;
}

jboolean HashSet::retainAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_retainAll.id, arg0);
    return ret;
}

jint HashSet::size(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_size.id);
    return ret;
}

jobject HashSet::spliterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_spliterator.id);
    return ret;
}

jobject HashSet::stream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_stream.id);
    return ret;
}

jobjectArray HashSet::toArray_ObjectArray(jobjectArray arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_ObjectArray.id, arg0));
    return ret;
}

jobjectArray HashSet::toArray(){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray.id));
    return ret;
}

jobjectArray HashSet::toArray_IntFunction(jobject arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_IntFunction.id, arg0));
    return ret;
}

jstring HashSet::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toString.id));
    return ret;
}

