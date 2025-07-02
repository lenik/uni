#include <sweetjni.hxx>

#include "ArrayList_class.hxx"

using namespace java::util;

ArrayList_class::ArrayList_class() {
    JNIEnv *env = getEnv();
    if (env == NULL) return;
    _class = findClass(env, "java/util/ArrayList");
    if (_class == NULL) return;
    INIT_Collection = getMethod(env, _class, "<init>", "(Ljava/util/Collection;)V");
    INIT = getMethod(env, _class, "<init>", "()V");
    INIT_int = getMethod(env, _class, "<init>", "(I)V");
    METHOD_add_arg0_arg1 = getMethod(env, _class, "add", "(ILjava/lang/Object;)V");
    METHOD_add_arg0 = getMethod(env, _class, "add", "(Ljava/lang/Object;)Z");
    METHOD_addAll_arg0_arg1 = getMethod(env, _class, "addAll", "(ILjava/util/Collection;)Z");
    METHOD_addAll_arg0 = getMethod(env, _class, "addAll", "(Ljava/util/Collection;)Z");
    METHOD_addFirst = getMethod(env, _class, "addFirst", "(Ljava/lang/Object;)V");
    METHOD_addLast = getMethod(env, _class, "addLast", "(Ljava/lang/Object;)V");
    METHOD_clear = getMethod(env, _class, "clear", "()V");
    METHOD_clone = getMethod(env, _class, "clone", "()Ljava/lang/Object;");
    METHOD_contains = getMethod(env, _class, "contains", "(Ljava/lang/Object;)Z");
    METHOD_containsAll = getMethod(env, _class, "containsAll", "(Ljava/util/Collection;)Z");
    METHOD_ensureCapacity = getMethod(env, _class, "ensureCapacity", "(I)V");
    METHOD_equals = getMethod(env, _class, "equals", "(Ljava/lang/Object;)Z");
    METHOD_forEach = getMethod(env, _class, "forEach", "(Ljava/util/function/Consumer;)V");
    METHOD_get = getMethod(env, _class, "get", "(I)Ljava/lang/Object;");
    METHOD_getFirst = getMethod(env, _class, "getFirst", "()Ljava/lang/Object;");
    METHOD_getLast = getMethod(env, _class, "getLast", "()Ljava/lang/Object;");
    METHOD_hashCode = getMethod(env, _class, "hashCode", "()I");
    METHOD_indexOf = getMethod(env, _class, "indexOf", "(Ljava/lang/Object;)I");
    METHOD_isEmpty = getMethod(env, _class, "isEmpty", "()Z");
    METHOD_iterator = getMethod(env, _class, "iterator", "()Ljava/util/Iterator;");
    METHOD_lastIndexOf = getMethod(env, _class, "lastIndexOf", "(Ljava/lang/Object;)I");
    METHOD_listIterator_arg0 = getMethod(env, _class, "listIterator", "(I)Ljava/util/ListIterator;");
    METHOD_listIterator = getMethod(env, _class, "listIterator", "()Ljava/util/ListIterator;");
    METHOD_parallelStream = getMethod(env, _class, "parallelStream", "()Ljava/util/stream/Stream;");
    METHOD_remove_Object = getMethod(env, _class, "remove", "(Ljava/lang/Object;)Z");
    METHOD_remove_int = getMethod(env, _class, "remove", "(I)Ljava/lang/Object;");
    METHOD_removeAll = getMethod(env, _class, "removeAll", "(Ljava/util/Collection;)Z");
    METHOD_removeFirst = getMethod(env, _class, "removeFirst", "()Ljava/lang/Object;");
    METHOD_removeIf = getMethod(env, _class, "removeIf", "(Ljava/util/function/Predicate;)Z");
    METHOD_removeLast = getMethod(env, _class, "removeLast", "()Ljava/lang/Object;");
    METHOD_replaceAll = getMethod(env, _class, "replaceAll", "(Ljava/util/function/UnaryOperator;)V");
    METHOD_retainAll = getMethod(env, _class, "retainAll", "(Ljava/util/Collection;)Z");
    METHOD_set = getMethod(env, _class, "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
    METHOD_size = getMethod(env, _class, "size", "()I");
    METHOD_sort = getMethod(env, _class, "sort", "(Ljava/util/Comparator;)V");
    METHOD_spliterator = getMethod(env, _class, "spliterator", "()Ljava/util/Spliterator;");
    METHOD_stream = getMethod(env, _class, "stream", "()Ljava/util/stream/Stream;");
    METHOD_subList = getMethod(env, _class, "subList", "(II)Ljava/util/List;");
    METHOD_toArray = getMethod(env, _class, "toArray", "()[Ljava/lang/Object;");
    METHOD_toArray_ObjectArray = getMethod(env, _class, "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;");
    METHOD_toArray_IntFunction = getMethod(env, _class, "toArray", "(Ljava/util/function/IntFunction;)[Ljava/lang/Object;");
    METHOD_toString = getMethod(env, _class, "toString", "()Ljava/lang/String;");
    METHOD_trimToSize = getMethod(env, _class, "trimToSize", "()V");
}

void ArrayList_class::dump() {
    printf("INIT_Collection: %p\n", INIT_Collection.id);
    printf("INIT: %p\n", INIT.id);
    printf("INIT_int: %p\n", INIT_int.id);
    printf("METHOD_add_arg0_arg1: %p\n", METHOD_add_arg0_arg1.id);
    printf("METHOD_add_arg0: %p\n", METHOD_add_arg0.id);
    printf("METHOD_addAll_arg0_arg1: %p\n", METHOD_addAll_arg0_arg1.id);
    printf("METHOD_addAll_arg0: %p\n", METHOD_addAll_arg0.id);
    printf("METHOD_addFirst: %p\n", METHOD_addFirst.id);
    printf("METHOD_addLast: %p\n", METHOD_addLast.id);
    printf("METHOD_clear: %p\n", METHOD_clear.id);
    printf("METHOD_clone: %p\n", METHOD_clone.id);
    printf("METHOD_contains: %p\n", METHOD_contains.id);
    printf("METHOD_containsAll: %p\n", METHOD_containsAll.id);
    printf("METHOD_ensureCapacity: %p\n", METHOD_ensureCapacity.id);
    printf("METHOD_equals: %p\n", METHOD_equals.id);
    printf("METHOD_forEach: %p\n", METHOD_forEach.id);
    printf("METHOD_get: %p\n", METHOD_get.id);
    printf("METHOD_getFirst: %p\n", METHOD_getFirst.id);
    printf("METHOD_getLast: %p\n", METHOD_getLast.id);
    printf("METHOD_hashCode: %p\n", METHOD_hashCode.id);
    printf("METHOD_indexOf: %p\n", METHOD_indexOf.id);
    printf("METHOD_isEmpty: %p\n", METHOD_isEmpty.id);
    printf("METHOD_iterator: %p\n", METHOD_iterator.id);
    printf("METHOD_lastIndexOf: %p\n", METHOD_lastIndexOf.id);
    printf("METHOD_listIterator_arg0: %p\n", METHOD_listIterator_arg0.id);
    printf("METHOD_listIterator: %p\n", METHOD_listIterator.id);
    printf("METHOD_parallelStream: %p\n", METHOD_parallelStream.id);
    printf("METHOD_remove_Object: %p\n", METHOD_remove_Object.id);
    printf("METHOD_remove_int: %p\n", METHOD_remove_int.id);
    printf("METHOD_removeAll: %p\n", METHOD_removeAll.id);
    printf("METHOD_removeFirst: %p\n", METHOD_removeFirst.id);
    printf("METHOD_removeIf: %p\n", METHOD_removeIf.id);
    printf("METHOD_removeLast: %p\n", METHOD_removeLast.id);
    printf("METHOD_replaceAll: %p\n", METHOD_replaceAll.id);
    printf("METHOD_retainAll: %p\n", METHOD_retainAll.id);
    printf("METHOD_set: %p\n", METHOD_set.id);
    printf("METHOD_size: %p\n", METHOD_size.id);
    printf("METHOD_sort: %p\n", METHOD_sort.id);
    printf("METHOD_spliterator: %p\n", METHOD_spliterator.id);
    printf("METHOD_stream: %p\n", METHOD_stream.id);
    printf("METHOD_subList: %p\n", METHOD_subList.id);
    printf("METHOD_toArray: %p\n", METHOD_toArray.id);
    printf("METHOD_toArray_ObjectArray: %p\n", METHOD_toArray_ObjectArray.id);
    printf("METHOD_toArray_IntFunction: %p\n", METHOD_toArray_IntFunction.id);
    printf("METHOD_toString: %p\n", METHOD_toString.id);
    printf("METHOD_trimToSize: %p\n", METHOD_trimToSize.id);
}
