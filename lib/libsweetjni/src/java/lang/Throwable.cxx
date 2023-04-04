#include "Throwable.hxx"

using namespace java::lang;

thread_local Throwable_class Throwable::CLASS;

Throwable::Throwable(JNIEnv *env)
        : Object(env) {}

Throwable::Throwable(JNIEnv *env, jobject jobj)
        : Object(env, jobj) {}

Throwable *Throwable::_wrap(jobject jobj) {
    JNIEnv *env = getEnv();
    return new Throwable(env, jobj);
}

/* ctor-create methods */
Throwable::Throwable(jthrowable arg0){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jthrowable) newObject(_env, CLASS.INIT_Throwable, arg0);
}

Throwable::Throwable(jstring arg0, jthrowable arg1){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jthrowable) newObject(_env, CLASS.INIT_String_Throwable, arg0, arg1);
}

Throwable::Throwable(jstring arg0){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jthrowable) newObject(_env, CLASS.INIT_String, arg0);
}

Throwable::Throwable(){
    jclass jclass = CLASS._class;
    this->_env = getEnv();
    this->_jobj = (jthrowable) newObject(_env, CLASS.INIT);
}

void Throwable::addSuppressed(jthrowable arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_addSuppressed.id, arg0);
}

jthrowable Throwable::fillInStackTrace(){
    jthrowable ret = (jthrowable) (_env->CallObjectMethod(_jobj, CLASS.METHOD_fillInStackTrace.id));
    return ret;
}

jthrowable Throwable::getCause(){
    jthrowable ret = (jthrowable) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getCause.id));
    return ret;
}

jstring Throwable::getLocalizedMessage(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getLocalizedMessage.id));
    return ret;
}

jstring Throwable::getMessage(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getMessage.id));
    return ret;
}

jarray Throwable::getStackTrace(){
    jarray ret = (jarray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getStackTrace.id));
    return ret;
}

jarray Throwable::getSuppressed(){
    jarray ret = (jarray) (_env->CallObjectMethod(_jobj, CLASS.METHOD_getSuppressed.id));
    return ret;
}

jthrowable Throwable::initCause(jthrowable arg0){
    jthrowable ret = (jthrowable) (_env->CallObjectMethod(_jobj, CLASS.METHOD_initCause.id, arg0));
    return ret;
}

void Throwable::printStackTrace_PrintWriter(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_printStackTrace_PrintWriter.id, arg0);
}

void Throwable::printStackTrace(){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_printStackTrace.id);
}

void Throwable::printStackTrace_PrintStream(jobject arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_printStackTrace_PrintStream.id, arg0);
}

void Throwable::setStackTrace(jarray arg0){
    _env->CallObjectMethod(_jobj, CLASS.METHOD_setStackTrace.id, arg0);
}

jstring Throwable::toString(){
    jstring ret = (jstring) (_env->CallObjectMethod(_jobj, CLASS.METHOD_toString.id));
    return ret;
}

