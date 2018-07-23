    file "src/main/java/$dir_web/${Base}Index.java"

    cat <<EOT >"$file"
package $pkg_web;

import net.bodz.bas.meta.decl.ObjectType;
import net.bodz.lily.model.base.CoIndex;

import $pkg_pojo.$Base;
import $pkg_db.${Base}Mask;

@ObjectType($Base.class)
public class ${Base}Index
        extends CoIndex<$Base, ${Base}Mask> {

}
EOT

    add_inf \
        "src/main/resources/META-INF/services/net.bodz.lily.model.base.CoIndex" \
        "$pkg_web.${Base}Index"
