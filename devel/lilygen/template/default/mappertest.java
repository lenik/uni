    file "src/test/java/$dir_db/${Base}MapperTest.java"

    cat <<EOT >"$file"
package $pkg_db;

import net.bodz.bas.db.ctx.DataContext;
import net.bodz.lily.model.test.AbstractMapperTest;

import MapperTestSupport;
import $pkg_pojo.$Base;
import $pkg_pojo.${Base}Samples;

public class ${Base}MapperTest
        extends AbstractMapperTest<$Base, ${Base}Mask, ${Base}Mapper> {

    @Override
    public DataContext getContext() {
        return MapperTestSupport.getDefaultContext();
    }

    @Override
    public $Base buildSample() {
        return ${Base}Samples.build();
    }

}
EOT

