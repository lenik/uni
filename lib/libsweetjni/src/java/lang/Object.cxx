#include "Object.hxx"

using namespace java::lang;

thread_local Object_class Object::CLASS;

Object::Object(JNIEnv *env) {
    this->_env = env;
}

Object::Object(JNIEnv *env, jobject jobj) {
    this->_env = env;
    this->_jobj = jobj;
}

Object *Object::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new Object(env, jobj);
}

/* ctor-create methods */
Object::Object(){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = newObject(_env, CLASS.INIT);
}

jboolean Object::equals(jobject arg0){
    jboolean ret = _env->CallBooleanMethod(_jobj, CLASS.METHOD_equals.id, arg0);
    return ret;
}

jclass Object::getClass(){
    jclass ret = (jclass) _env->CallObjectMethod(_jobj, CLASS.METHOD_getClass.id);
    return ret;
}

jint Object::hashCode(){
    jint ret = _env->CallIntMethod(_jobj, CLASS.METHOD_hashCode.id);
    return ret;
}

void Object::notify(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_notify.id);
}

void Object::notifyAll(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_notifyAll.id);
}

jstring Object::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toString.id));
    return ret;
}

void Object::wait_1(jlong arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_wait_1.id, arg0);
}

void Object::wait_2(jlong arg0, jint arg1){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_wait_2.id, arg0, arg1);
}

void Object::wait(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_wait.id);
}
