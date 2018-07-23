    file "src/main/java/$dir_pojo/$Base.java"

    cat <<EOT >"$file"
package $pkg_pojo;

import net.bodz.lily.entity.IdType;
import net.bodz.lily.model.base.CoEntity;

@IdType(Integer.class)
public class $Base
        extends CoEntity<Integer> {

    private static final long serialVersionUID = 1L;

    public $Base() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(super.toString());
        return sb.toString();
    }

}
EOT
