package net.bodz.lily.tool.daogen;

import net.bodz.bas.db.ibatis.IMapper;
import net.bodz.lily.tool.daogen.util.CanonicalClass;

public class RuntimeSupport {

    public static Class<?> findClass(String name) {
        try {
            return CanonicalClass.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> findMapperClass(String className) {
        Class<?> clazz = findClass(className);
        if (clazz == null)
            return null;
        Class<IMapper> mapperClass = IMapper.fn.getMapperClass(clazz);
        return mapperClass;
    }

    public static String guessMapperNs(String className) {
        Class<?> mapperClass = findMapperClass(className);
        if (mapperClass != null)
            return mapperClass.getName();
        return className + "Mapper";
    }

}
