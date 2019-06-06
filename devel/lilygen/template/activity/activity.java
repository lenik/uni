    file "app/src/main/java/$dir/${Base}.java"

    cat <<EOT >"$file"
package $pkg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import $pkg_parent.R;

public class ${Base}
        extends AppCompatActivity {

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

    _log1 "Add activity $fqcn to AndroidManifest.xml"
    manifest="app/src/main/AndroidManifest.xml"

    tmp=`mktemp`
    
    grep -m1 -B1000000 '</application>' "$manifest" | grep -v '</application>' >$tmp
    cat <<EOT >>$tmp

        <activity
            android:name=".$pkg_base.$Base"
            android:label="$label"
            android:theme="@style/FullscreenTheme">
        </activity>
EOT

    grep -m1 -A1000000 '</application>' "$manifest" >>$tmp
    
    mv $tmp "$manifest"

