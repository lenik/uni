#include "List.hxx"

using namespace java::util;

thread_local List_class List::CLASS;

List::List(JNIEnv *env)
        : _env(env) {}

List::List(JNIEnv *env, jobject jobj)
        : _env(env), _jobj(jobj) {}

List *List::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new List(env, jobj);
}

/* ctor-create methods */
jboolean List::add_arg0(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_add_arg0.id, arg0);
    return ret;
}

void List::add_arg0_arg1(jint arg0, jobject arg1){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_add_arg0_arg1.id, arg0, arg1);
}

jboolean List::addAll_arg0(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll_arg0.id, arg0);
    return ret;
}

jboolean List::addAll_arg0_arg1(jint arg0, jobject arg1){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll_arg0_arg1.id, arg0, arg1);
    return ret;
}

void List::addFirst(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addFirst.id, arg0);
}

void List::addLast(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addLast.id, arg0);
}

void List::clear(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_clear.id);
}

jboolean List::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contains.id, arg0);
    return ret;
}

jboolean List::containsAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_containsAll.id, arg0);
    return ret;
}

jobject List::copyOf(jobject arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_copyOf.id, arg0);
    return ret;
}

jboolean List::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

void List::forEach(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_forEach.id, arg0);
}

jobject List::get(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_get.id, arg0);
    return ret;
}

jobject List::getFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_getFirst.id);
    return ret;
}

jobject List::getLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_getLast.id);
    return ret;
}

jint List::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

jint List::indexOf(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_indexOf.id, arg0);
    return ret;
}

jboolean List::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isEmpty.id);
    return ret;
}

jobject List::iterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_iterator.id);
    return ret;
}

jint List::lastIndexOf(jobject arg0){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_lastIndexOf.id, arg0);
    return ret;
}

jobject List::listIterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_listIterator.id);
    return ret;
}

jobject List::listIterator_arg0(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_listIterator_arg0.id, arg0);
    return ret;
}

jobject List::of_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object.id, arg0, arg1, arg2);
    return ret;
}

jobject List::of_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3);
    return ret;
}

jobject List::of_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4);
    return ret;
}

jobject List::of_Object_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8, jobject arg9){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    return ret;
}

jobject List::of_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    return ret;
}

jobject List::of(){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of.id);
    return ret;
}

jobject List::of_Object(jobject arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object.id, arg0);
    return ret;
}

jobject List::of_Object_Object(jobject arg0, jobject arg1){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object.id, arg0, arg1);
    return ret;
}

jobject List::of_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    return ret;
}

jobject List::of_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    return ret;
}

jobject List::of_ObjectArray(jobjectArray arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_ObjectArray.id, arg0);
    return ret;
}

jobject List::of_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5);
    return ret;
}

jobject List::parallelStream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_parallelStream.id);
    return ret;
}

jboolean List::remove_Object(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_remove_Object.id, arg0);
    return ret;
}

jobject List::remove_int(jint arg0){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_remove_int.id, arg0);
    return ret;
}

jboolean List::removeAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeAll.id, arg0);
    return ret;
}

jobject List::removeFirst(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_removeFirst.id);
    return ret;
}

jboolean List::removeIf(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeIf.id, arg0);
    return ret;
}

jobject List::removeLast(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_removeLast.id);
    return ret;
}

void List::replaceAll(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_replaceAll.id, arg0);
}

jboolean List::retainAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_retainAll.id, arg0);
    return ret;
}

jobject List::set(jint arg0, jobject arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_set.id, arg0, arg1);
    return ret;
}

jint List::size(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_size.id);
    return ret;
}

void List::sort(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_sort.id, arg0);
}

jobject List::spliterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_spliterator.id);
    return ret;
}

jobject List::stream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_stream.id);
    return ret;
}

jobject List::subList(jint arg0, jint arg1){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_subList.id, arg0, arg1);
    return ret;
}

jobjectArray List::toArray(){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray.id));
    return ret;
}

jobjectArray List::toArray_ObjectArray(jobjectArray arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_ObjectArray.id, arg0));
    return ret;
}

jobjectArray List::toArray_IntFunction(jobject arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_IntFunction.id, arg0));
    return ret;
}

