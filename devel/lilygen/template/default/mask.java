    file "src/main/java/$dir_db/${Base}Mask.java"

    cat <<EOT >"$file"
package $pkg_db;

import net.bodz.lily.model.base.CoObjectMask;

/**
 * @see $pkg_pojo.$Base
 */
public class ${Base}Mask
        extends CoObjectMask {

}
EOT
