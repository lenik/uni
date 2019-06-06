    file "app/src/main/java/$dir/${Base}.java"

    cat <<EOT >"$file"
package $pkg;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import $pkg_parent.R;

public class $Base
        extends LinearLayoutCompat {

    public $Base(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.$ba_se, this);

        // TextView foobar = findViewById(R.id.foobar);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.$Base);
        try {
        } finally {
            a.recycle();
        }
    }

}
EOT

    _log1 "Prepare styleable attributes for $fqcn"
    attrs="app/src/main/res/values/attrs.xml"

    tmp=`mktemp`
    
    grep -m1 -B1000000 '</resources>' "$attrs" | grep -v '</resources>' >$tmp
    cat <<EOT >>$tmp

    <declare-styleable name="$Base">
    </declare-styleable>
EOT

    grep -m1 -A1000000 '</resources>' "$attrs" >>$tmp
    
    mv $tmp "$attrs"

