    file "app/src/main/java/$dir/${Base}.java"

    cat <<EOT >"$file"
package $pkg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import $pkg_parent.R;

public class ${Base}
        extends RowLayout {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.$ba_se);
    }

}
EOT

