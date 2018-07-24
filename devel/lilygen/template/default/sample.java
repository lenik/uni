    file "src/test/java/$dir_pojo/${Base}Samples.java"

    cat <<EOT >"$file"
package $pkg_pojo;

public class ${Base}Samples {

    public static $Base build() {
        $Base a = new $Base();
        a.setLabel("${base}-1");
        a.setDescription("A $base named ${base}-1.");
        return a;
    }

}
EOT

