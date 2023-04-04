package net.bodz.bas.type.overloaded;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OverloadedTypeInfo {

    private OverloadedConstructors<?> constructors;
    private Map<String, OverloadedMethods> methodNameMap;
    private Map<String, OverloadedMethods> nativeMethodNameMap;
    private Map<String, Field> fieldMap;
    private Map<String, Field> staticFinalBasicFieldMap;

    public <T> void addMembers(Class<T> clazz, TypeInfoOptions options) {
        OverloadedConstructors<T> oCtors = new OverloadedConstructors<>();
        Constructor<?>[] constructors = options.declaredOnly //
                ? clazz.getDeclaredConstructors()
                : clazz.getConstructors();
        for (Constructor<?> _ctor : constructors) {
            if (options.include(_ctor)) {
                @SuppressWarnings("unchecked")
                Constructor<T> ctor = (Constructor<T>) _ctor;
                oCtors.add(ctor);
            }
        }
        this.constructors = oCtors;

        methodNameMap = options.memberOrder.newMap();
        nativeMethodNameMap = options.memberOrder.newMap();
        Method[] methods = options.declaredOnly ? //
                clazz.getDeclaredMethods() : clazz.getMethods();
        for (Method method : methods) {
            if (!options.include(method))
                continue;

            String methodName = method.getName();
            int modifiers = method.getModifiers();
            if (Modifier.isNative(modifiers)) {
                OverloadedMethods nativeMethods = nativeMethodNameMap.get(methodName);
                if (nativeMethods == null) {
                    nativeMethods = new OverloadedMethods();
                    nativeMethodNameMap.put(methodName, nativeMethods);
                }
                nativeMethods.add(method);
                if (!options.includeNativeMethodsAlways)
                    continue;
            }

            OverloadedMethods oMethods = methodNameMap.get(methodName);
            if (oMethods == null) {
                oMethods = new OverloadedMethods();
                methodNameMap.put(methodName, oMethods);
            }
            oMethods.add(method);
        }

        fieldMap = options.memberOrder.newMap();
        staticFinalBasicFieldMap = options.memberOrder.newMap();
        Field[] fields = options.declaredOnly //
                ? clazz.getDeclaredFields()
                : clazz.getFields();
        for (Field field : fields) {
            if (!options.include(field))
                continue;
            int modifiers = field.getModifiers();
            boolean isStatic = Modifier.isStatic(modifiers);
            boolean isFinal = Modifier.isFinal(modifiers);

            boolean isString = field.getType() == String.class;
            boolean isPrimitive = field.getType().isPrimitive();
            boolean basicType = isPrimitive || isString;
            if (isStatic && isFinal && basicType)
                staticFinalBasicFieldMap.put(field.getName(), field);
            else
                fieldMap.put(field.getName(), field);
        }
    }

    public ConstructorMap<?> getConstructors() {
        return constructors.distinguishablization();
    }

    public ConstructorMap<?> getConstructors(DistinguishableNaming... namings) {
        return constructors.distinguishablization(namings);
    }

    public Set<String> getMethodNames() {
        return methodNameMap.keySet();
    }

    public MethodMap getMethods(String name) {
        OverloadedMethods methods = methodNameMap.get(name);
        return methods.distinguishablization();
    }

    public MethodMap getMethods(String name, DistinguishableNaming... namings) {
        OverloadedMethods methods = methodNameMap.get(name);
        return methods.distinguishablization(namings);
    }

    public Set<String> getNativeMethodNames() {
        return nativeMethodNameMap.keySet();
    }

    public MethodMap getNativeMethods(String name) {
        OverloadedMethods methods = nativeMethodNameMap.get(name);
        return methods.distinguishablization();
    }

    public MethodMap getNativeMethods(String name, DistinguishableNaming... namings) {
        OverloadedMethods methods = nativeMethodNameMap.get(name);
        return methods.distinguishablization(namings);
    }

    public Set<String> getFieldNames() {
        return fieldMap.keySet();
    }

    public Field getField(String name) {
        return fieldMap.get(name);
    }

    public Collection<Field> getFields() {
        return fieldMap.values();
    }

    public Set<String> getConstFieldNames() {
        return staticFinalBasicFieldMap.keySet();
    }

    public Field getConstField(String name) {
        return staticFinalBasicFieldMap.get(name);
    }

    public Collection<Field> getConstFields() {
        return staticFinalBasicFieldMap.values();
    }

}
