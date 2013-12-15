module lenik.bas.reflect;

import std.conv : to;
import std.traits : isCallable, ReturnType, ParameterTypeTuple;
import std.variant : Variant;

class MetaElement {
    string name;
    string protection;
    Variant[] udas;
}

class Class : MetaElement {
    Class superClass;
    string simpleName;
    string moduleName;
    Field[string] fields;
    MethodGroup[string] methodGroups;
}

class Field : MetaElement {
    Class type;
}

class Method : MetaElement {
    string returnType;
    string[] parameterTypes;
}

class MethodGroup {
    Method[] methods;
}

interface IReflection {
    Class getClass();
}

template reflection_impl() {
    private Class _class;
    
    Class getClass() {
        if (_class is null) {
            Class cls = new Class;
            auto id = typeid(typeof(this)); /* TypeInfo_Class */
            cls.name = to!string(id);
            
            auto lastDot = lastIndexOf(cls.name, '.'); /* long */
            if (lastDot == -1) {
                cls.simpleName = cls.name;
                cls.moduleName = "";
            } else {
                cls.simpleName = cast(string) cls.name[lastDot + 1..$];
                cls.moduleName = cast(string) cls.name[0..lastDot];
            }

            foreach (m; __traits(derivedMembers, typeof(this))) {

                string protection;
                Variant[] udas;

                /* common */
                {
                    protection = __traits(getProtection, __traits(getMember, this, m));
                    foreach (a; __traits(getAttributes, __traits(getMember, this, m)))
                        udas ~= Variant(a);
                }
                
                if (isCallable!(__traits(getMember, this, m))) {
                    /* method members with the same name */
                    foreach (fn; __traits(getOverloads, this, m)) {
                        alias typeof(fn) fnt;
                        alias ReturnType!fnt ret_t;
                        
                        Method method = new Method;
                        method.name = m;
                        // method.protection = __traits(getProtection, fn);
                        method.protection = protection;
                        method.udas = udas;
                        
                        method.returnType = to!string(typeid(ret_t));
                        foreach (param_t; ParameterTypeTuple!fnt)
                            method.parameterTypes ~=
                                to!string(typeid(param_t));

                        MethodGroup group;
                        if (m in cls.methodGroups)
                            group = cls.methodGroups[m];
                        else
                            cls.methodGroups[m] = group = new MethodGroup;

                        group.methods ~= method;
                    }
                } else {
                    /* field member */
                    Field field = new Field;
                    field.name = m;
                    field.protection = protection;
                    field.udas = udas;
                    cls.fields[m] = field;
                }
            }
            _class = cls;
        }
        return _class;
    }
    
}
