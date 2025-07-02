#include <sweetjni.hxx>

#include "HashSet_class.hxx"

using namespace java::util;

HashSet_class::HashSet_class() {
    JNIEnv *env = getEnv();
    if (env == NULL) return;
    _class = findClass(env, "java/util/HashSet");
    if (_class == NULL) return;
    INIT_int = getMethod(env, _class, "<init>", "(I)V");
    INIT_int_float = getMethod(env, _class, "<init>", "(IF)V");
    INIT_Collection = getMethod(env, _class, "<init>", "(Ljava/util/Collection;)V");
    INIT = getMethod(env, _class, "<init>", "()V");
    METHOD_add = getMethod(env, _class, "add", "(Ljava/lang/Object;)Z");
    METHOD_addAll = getMethod(env, _class, "addAll", "(Ljava/util/Collection;)Z");
    METHOD_clear = getMethod(env, _class, "clear", "()V");
    METHOD_clone = getMethod(env, _class, "clone", "()Ljava/lang/Object;");
    METHOD_contains = getMethod(env, _class, "contains", "(Ljava/lang/Object;)Z");
    METHOD_containsAll = getMethod(env, _class, "containsAll", "(Ljava/util/Collection;)Z");
    METHOD_equals = getMethod(env, _class, "equals", "(Ljava/lang/Object;)Z");
    METHOD_forEach = getMethod(env, _class, "forEach", "(Ljava/util/function/Consumer;)V");
    METHOD_hashCode = getMethod(env, _class, "hashCode", "()I");
    METHOD_isEmpty = getMethod(env, _class, "isEmpty", "()Z");
    METHOD_iterator = getMethod(env, _class, "iterator", "()Ljava/util/Iterator;");
    METHOD_newHashSet = getStaticMethod(env, _class, "newHashSet", "(I)Ljava/util/HashSet;");
    METHOD_parallelStream = getMethod(env, _class, "parallelStream", "()Ljava/util/stream/Stream;");
    METHOD_remove = getMethod(env, _class, "remove", "(Ljava/lang/Object;)Z");
    METHOD_removeAll = getMethod(env, _class, "removeAll", "(Ljava/util/Collection;)Z");
    METHOD_removeIf = getMethod(env, _class, "removeIf", "(Ljava/util/function/Predicate;)Z");
    METHOD_retainAll = getMethod(env, _class, "retainAll", "(Ljava/util/Collection;)Z");
    METHOD_size = getMethod(env, _class, "size", "()I");
    METHOD_spliterator = getMethod(env, _class, "spliterator", "()Ljava/util/Spliterator;");
    METHOD_stream = getMethod(env, _class, "stream", "()Ljava/util/stream/Stream;");
    METHOD_toArray_ObjectArray = getMethod(env, _class, "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;");
    METHOD_toArray = getMethod(env, _class, "toArray", "()[Ljava/lang/Object;");
    METHOD_toArray_IntFunction = getMethod(env, _class, "toArray", "(Ljava/util/function/IntFunction;)[Ljava/lang/Object;");
    METHOD_toString = getMethod(env, _class, "toString", "()Ljava/lang/String;");
}

void HashSet_class::dump() {
    printf("INIT_int: %p\n", INIT_int.id);
    printf("INIT_int_float: %p\n", INIT_int_float.id);
    printf("INIT_Collection: %p\n", INIT_Collection.id);
    printf("INIT: %p\n", INIT.id);
    printf("METHOD_add: %p\n", METHOD_add.id);
    printf("METHOD_addAll: %p\n", METHOD_addAll.id);
    printf("METHOD_clear: %p\n", METHOD_clear.id);
    printf("METHOD_clone: %p\n", METHOD_clone.id);
    printf("METHOD_contains: %p\n", METHOD_contains.id);
    printf("METHOD_containsAll: %p\n", METHOD_containsAll.id);
    printf("METHOD_equals: %p\n", METHOD_equals.id);
    printf("METHOD_forEach: %p\n", METHOD_forEach.id);
    printf("METHOD_hashCode: %p\n", METHOD_hashCode.id);
    printf("METHOD_isEmpty: %p\n", METHOD_isEmpty.id);
    printf("METHOD_iterator: %p\n", METHOD_iterator.id);
    printf("METHOD_newHashSet: %p\n", METHOD_newHashSet.id);
    printf("METHOD_parallelStream: %p\n", METHOD_parallelStream.id);
    printf("METHOD_remove: %p\n", METHOD_remove.id);
    printf("METHOD_removeAll: %p\n", METHOD_removeAll.id);
    printf("METHOD_removeIf: %p\n", METHOD_removeIf.id);
    printf("METHOD_retainAll: %p\n", METHOD_retainAll.id);
    printf("METHOD_size: %p\n", METHOD_size.id);
    printf("METHOD_spliterator: %p\n", METHOD_spliterator.id);
    printf("METHOD_stream: %p\n", METHOD_stream.id);
    printf("METHOD_toArray_ObjectArray: %p\n", METHOD_toArray_ObjectArray.id);
    printf("METHOD_toArray: %p\n", METHOD_toArray.id);
    printf("METHOD_toArray_IntFunction: %p\n", METHOD_toArray_IntFunction.id);
    printf("METHOD_toString: %p\n", METHOD_toString.id);
}
