    file "src/main/java/$dir_web/${Base}_htm.java"

    cat <<EOT >"$file"
package $pkg_web;

import java.io.IOException;

import $fqcn;

public class ${Base}_htm
        extends SlimForm_htm<$Base> {

    public ${Base}_htm() {
        super($Base.class);
    }

}
EOT

    css="src/main/resources/$dir_web/${Base}_htm.css"
    js="src/main/resources/$dir_web/${Base}_htm.js"
    touch $css
    touch $js

    add_inf \
        "src/main/resources/META-INF/services/net.bodz.bas.http.viz.IHttpViewBuilder" \
        "$pkg_web.${Base}_htm"
