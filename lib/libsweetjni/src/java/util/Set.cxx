#include "Set.hxx"

using namespace java::util;

thread_local Set_class Set::CLASS;

Set::Set(JNIEnv *env)
        : _env(env) {}

Set::Set(JNIEnv *env, jobject jobj)
        : _env(env), _jobj(jobj) {}

Set *Set::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new Set(env, jobj);
}

/* ctor-create methods */
jboolean Set::add(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_add.id, arg0);
    return ret;
}

jboolean Set::addAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_addAll.id, arg0);
    return ret;
}

void Set::clear(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_clear.id);
}

jboolean Set::contains(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_contains.id, arg0);
    return ret;
}

jboolean Set::containsAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_containsAll.id, arg0);
    return ret;
}

jobject Set::copyOf(jobject arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_copyOf.id, arg0);
    return ret;
}

jboolean Set::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

void Set::forEach(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_forEach.id, arg0);
}

jint Set::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

jboolean Set::isEmpty(){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_isEmpty.id);
    return ret;
}

jobject Set::iterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_iterator.id);
    return ret;
}

jobject Set::of_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5);
    return ret;
}

jobject Set::of_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4);
    return ret;
}

jobject Set::of_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3);
    return ret;
}

jobject Set::of(){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of.id);
    return ret;
}

jobject Set::of_Object_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8, jobject arg9){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    return ret;
}

jobject Set::of_Object_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7, jobject arg8){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    return ret;
}

jobject Set::of_Object_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6, jobject arg7){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    return ret;
}

jobject Set::of_Object_Object_Object_Object_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4, jobject arg5, jobject arg6){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object_Object_Object_Object_Object.id, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    return ret;
}

jobject Set::of_ObjectArray(jobjectArray arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_ObjectArray.id, arg0);
    return ret;
}

jobject Set::of_Object_Object_Object(jobject arg0, jobject arg1, jobject arg2){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object_Object.id, arg0, arg1, arg2);
    return ret;
}

jobject Set::of_Object(jobject arg0){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object.id, arg0);
    return ret;
}

jobject Set::of_Object_Object(jobject arg0, jobject arg1){
    JNIEnv *env = getEnv();
    jobject ret = env->CallStaticObjectMethod(CLASS._class, CLASS.METHOD_of_Object_Object.id, arg0, arg1);
    return ret;
}

jobject Set::parallelStream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_parallelStream.id);
    return ret;
}

jboolean Set::remove(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_remove.id, arg0);
    return ret;
}

jboolean Set::removeAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeAll.id, arg0);
    return ret;
}

jboolean Set::removeIf(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_removeIf.id, arg0);
    return ret;
}

jboolean Set::retainAll(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_retainAll.id, arg0);
    return ret;
}

jint Set::size(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_size.id);
    return ret;
}

jobject Set::spliterator(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_spliterator.id);
    return ret;
}

jobject Set::stream(){
    jobject ret = _env->CallObjectMethod(_jobj, CLASS.METHOD_stream.id);
    return ret;
}

jobjectArray Set::toArray(){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray.id));
    return ret;
}

jobjectArray Set::toArray_ObjectArray(jobjectArray arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_ObjectArray.id, arg0));
    return ret;
}

jobjectArray Set::toArray_IntFunction(jobject arg0){
    jobjectArray ret = (jobjectArray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toArray_IntFunction.id, arg0));
    return ret;
}

