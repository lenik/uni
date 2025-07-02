#include "ArrayList.hxx"

using namespace java::util;

thread_local ArrayList_class ArrayList::CLASS;

ArrayList::ArrayList(JNIEnv *env)
        : Object(env) {}

ArrayList::ArrayList(JNIEnv *env, jobject jobj)
        : Object(env, jobj) {}

ArrayList *ArrayList::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new ArrayList(env, jobj);
}

/* ctor-create methods */
ArrayList::ArrayList(jobject arg0) {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT_Collection, arg0);
}

ArrayList::ArrayList() {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT);
}

ArrayList::ArrayList(jint arg0) {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT_int, arg0);
}

void ArrayList::add_arg0_arg1(jint arg0, jobject arg1){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_add_arg0_arg1.id, arg0, arg1);
}

jboolean ArrayList::add_arg0(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_add_arg0.id, arg0);
    return ret;
}

jboolean ArrayList::addAll_arg0_arg1(jint arg0, jobject arg1){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll_arg0_arg1.id, arg0, arg1);
    return ret;
}

jboolean ArrayList::addAll_arg0(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll_arg0.id, arg0);
    return ret;
}

void ArrayList::addFirst(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addFirst.id, arg0);
}

void ArrayList::addLast(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addLast.id, arg0);
}

void ArrayList::clear(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_clear.id);
}

jobject ArrayList::clone(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_clone.id);
    return ret;
}

jboolean ArrayList::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contains.id, arg0);
    return ret;
}

jboolean ArrayList::containsAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_containsAll.id, arg0);
    return ret;
}

void ArrayList::ensureCapacity(jint arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_ensureCapacity.id, arg0);
}

jboolean ArrayList::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

void ArrayList::forEach(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_forEach.id, arg0);
}

jobject ArrayList::get(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_get.id, arg0);
    return ret;
}

jobject ArrayList::getFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_getFirst.id);
    return ret;
}

jobject ArrayList::getLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_getLast.id);
    return ret;
}

jint ArrayList::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

jint ArrayList::indexOf(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf.id, arg0);
    return ret;
}

jboolean ArrayList::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isEmpty.id);
    return ret;
}

jobject ArrayList::iterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_iterator.id);
    return ret;
}

jint ArrayList::lastIndexOf(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf.id, arg0);
    return ret;
}

jobject ArrayList::listIterator_arg0(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_listIterator_arg0.id, arg0);
    return ret;
}

jobject ArrayList::listIterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_listIterator.id);
    return ret;
}

jobject ArrayList::parallelStream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_parallelStream.id);
    return ret;
}

jboolean ArrayList::remove_Object(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_remove_Object.id, arg0);
    return ret;
}

jobject ArrayList::remove_int(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_remove_int.id, arg0);
    return ret;
}

jboolean ArrayList::removeAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeAll.id, arg0);
    return ret;
}

jobject ArrayList::removeFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_removeFirst.id);
    return ret;
}

jboolean ArrayList::removeIf(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeIf.id, arg0);
    return ret;
}

jobject ArrayList::removeLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_removeLast.id);
    return ret;
}

void ArrayList::replaceAll(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_replaceAll.id, arg0);
}

jboolean ArrayList::retainAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_retainAll.id, arg0);
    return ret;
}

jobject ArrayList::set(jint arg0, jobject arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_set.id, arg0, arg1);
    return ret;
}

jint ArrayList::size(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_size.id);
    return ret;
}

void ArrayList::sort(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_sort.id, arg0);
}

jobject ArrayList::spliterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_spliterator.id);
    return ret;
}

jobject ArrayList::stream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_stream.id);
    return ret;
}

jobject ArrayList::subList(jint arg0, jint arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_subList.id, arg0, arg1);
    return ret;
}

jobjectArray ArrayList::toArray(){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray.id));
    return ret;
}

jobjectArray ArrayList::toArray_ObjectArray(jobjectArray arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_ObjectArray.id, arg0));
    return ret;
}

jobjectArray ArrayList::toArray_IntFunction(jobject arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_IntFunction.id, arg0));
    return ret;
}

jstring ArrayList::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toString.id));
    return ret;
}

void ArrayList::trimToSize(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_trimToSize.id);
}

