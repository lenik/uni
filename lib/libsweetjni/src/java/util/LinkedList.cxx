#include "LinkedList.hxx"

using namespace java::util;

thread_local LinkedList_class LinkedList::CLASS;

LinkedList::LinkedList(JNIEnv *env)
        : Object(env) {}

LinkedList::LinkedList(JNIEnv *env, jobject jobj)
        : Object(env, jobj) {}

LinkedList *LinkedList::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new LinkedList(env, jobj);
}

/* ctor-create methods */
LinkedList::LinkedList() {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT);
}

LinkedList::LinkedList(jobject arg0) {
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT_arg0, arg0);
}

void LinkedList::add_arg0_arg1(jint arg0, jobject arg1){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_add_arg0_arg1.id, arg0, arg1);
}

jboolean LinkedList::add_arg0(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_add_arg0.id, arg0);
    return ret;
}

jboolean LinkedList::addAll_arg0(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll_arg0.id, arg0);
    return ret;
}

jboolean LinkedList::addAll_arg0_arg1(jint arg0, jobject arg1){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll_arg0_arg1.id, arg0, arg1);
    return ret;
}

void LinkedList::addFirst(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addFirst.id, arg0);
}

void LinkedList::addLast(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addLast.id, arg0);
}

void LinkedList::clear(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_clear.id);
}

jobject LinkedList::clone(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_clone.id);
    return ret;
}

jboolean LinkedList::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contains.id, arg0);
    return ret;
}

jboolean LinkedList::containsAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_containsAll.id, arg0);
    return ret;
}

jobject LinkedList::descendingIterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_descendingIterator.id);
    return ret;
}

jobject LinkedList::element(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_element.id);
    return ret;
}

jboolean LinkedList::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

void LinkedList::forEach(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_forEach.id, arg0);
}

jobject LinkedList::get(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_get.id, arg0);
    return ret;
}

jobject LinkedList::getFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_getFirst.id);
    return ret;
}

jobject LinkedList::getLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_getLast.id);
    return ret;
}

jint LinkedList::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

jint LinkedList::indexOf(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf.id, arg0);
    return ret;
}

jboolean LinkedList::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isEmpty.id);
    return ret;
}

jobject LinkedList::iterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_iterator.id);
    return ret;
}

jint LinkedList::lastIndexOf(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf.id, arg0);
    return ret;
}

jobject LinkedList::listIterator_arg0(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_listIterator_arg0.id, arg0);
    return ret;
}

jobject LinkedList::listIterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_listIterator.id);
    return ret;
}

jboolean LinkedList::offer(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_offer.id, arg0);
    return ret;
}

jboolean LinkedList::offerFirst(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_offerFirst.id, arg0);
    return ret;
}

jboolean LinkedList::offerLast(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_offerLast.id, arg0);
    return ret;
}

jobject LinkedList::parallelStream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_parallelStream.id);
    return ret;
}

jobject LinkedList::peek(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_peek.id);
    return ret;
}

jobject LinkedList::peekFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_peekFirst.id);
    return ret;
}

jobject LinkedList::peekLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_peekLast.id);
    return ret;
}

jobject LinkedList::poll(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_poll.id);
    return ret;
}

jobject LinkedList::pollFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_pollFirst.id);
    return ret;
}

jobject LinkedList::pollLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_pollLast.id);
    return ret;
}

jobject LinkedList::pop(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_pop.id);
    return ret;
}

void LinkedList::push(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_push.id, arg0);
}

jobject LinkedList::remove_int(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_remove_int.id, arg0);
    return ret;
}

jobject LinkedList::remove(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_remove.id);
    return ret;
}

jboolean LinkedList::remove_Object(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_remove_Object.id, arg0);
    return ret;
}

jboolean LinkedList::removeAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeAll.id, arg0);
    return ret;
}

jobject LinkedList::removeFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_removeFirst.id);
    return ret;
}

jboolean LinkedList::removeFirstOccurrence(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeFirstOccurrence.id, arg0);
    return ret;
}

jboolean LinkedList::removeIf(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeIf.id, arg0);
    return ret;
}

jobject LinkedList::removeLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_removeLast.id);
    return ret;
}

jboolean LinkedList::removeLastOccurrence(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeLastOccurrence.id, arg0);
    return ret;
}

void LinkedList::replaceAll(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_replaceAll.id, arg0);
}

jboolean LinkedList::retainAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_retainAll.id, arg0);
    return ret;
}

jobject LinkedList::set(jint arg0, jobject arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_set.id, arg0, arg1);
    return ret;
}

jint LinkedList::size(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_size.id);
    return ret;
}

void LinkedList::sort(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_sort.id, arg0);
}

jobject LinkedList::spliterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_spliterator.id);
    return ret;
}

jobject LinkedList::stream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_stream.id);
    return ret;
}

jobject LinkedList::subList(jint arg0, jint arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_subList.id, arg0, arg1);
    return ret;
}

jobjectArray LinkedList::toArray_ObjectArray(jobjectArray arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_ObjectArray.id, arg0));
    return ret;
}

jobjectArray LinkedList::toArray(){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray.id));
    return ret;
}

jobjectArray LinkedList::toArray_IntFunction(jobject arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_IntFunction.id, arg0));
    return ret;
}

jstring LinkedList::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toString.id));
    return ret;
}

