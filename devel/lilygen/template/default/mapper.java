    file "src/main/java/$dir_db/${Base}Mapper.java"

    cat <<EOT >"$file"
package $pkg_db;

import net.bodz.bas.db.ibatis.IMapperTemplate;

import $pkg_pojo.$Base;
import $pkg_db.${Base}Mask;

/**
 * @mapper.xml ${Base}Mapper.xml
 */
public interface ${Base}Mapper
        extends IMapperTemplate<$Base, ${Base}Mask> {

}
EOT

    add_inf \
        "src/main/resources/META-INF/services/net.bodz.bas.db.ibatis.IMapper" \
        "$pkg_db.${Base}Mapper"
