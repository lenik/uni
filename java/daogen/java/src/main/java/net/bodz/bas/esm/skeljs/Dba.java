package net.bodz.bas.esm.skeljs;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.SourcePath;

public class Dba
        extends EsmModule {

    public Dba(int priority) {
        super("@skeljs/dba", "src", priority);
    }

    @SourcePath("net/bodz/lily/entity")
    public class LilyEntity
            extends Source {

        public final EsmName IEntityType = _interface("IEntityType");
        public final EsmName EntityType = _class("EntityType");
        public final EsmName EntityPropertyMap = _interface("EntityPropertyMap");
        public final EsmName IEntityProperty = _interface("IEntityProperty");
        public final EsmName EntityProperty = _class("EntityProperty");

        public final EsmName primaryKey = name("primaryKey");
        public final EsmName property = name("property");

    }

    @SourcePath("ui/table/types")
    public class UiTableTypes
            extends Source {

        public final EsmName SetupDataFunc = type("SetupDataFunc");
        public final EsmName OnAppliedFunc = type("OnAppliedFunc");
        public final EsmName ConfigFn = type("ConfigFn");
        public final EsmName TextMap = _interface("TextMap");
        public final EsmName ColumnType = _interface("ColumnType");
        public final EsmName Selection = _class("Selection");

    }

    @SourcePath("ui/lily/defaults")
    public class UiLilyDefaults
            extends Source {

        public final EsmName getDefaultCommands = name("getDefaultCommands");
        public final EsmName getDefaultCommand = name("getDefaultCommand");
        public final EsmName getDefaultStatuses = name("getDefaultStatuses");
        public final EsmName getDefaultStatus = name("getDefaultStatus");
        public final EsmName getDefaultFieldRowProps = name("getDefaultFieldRowProps");

    }

    public final LilyEntity entity = add(new LilyEntity());
    public final UiTableTypes tableTypes = add(new UiTableTypes());
    public final UiLilyDefaults defaults = add(new UiLilyDefaults());

    public final EsmName DataAdmin = vue("ui/table/DataAdmin.vue");
    public final EsmName DataTable = vue("ui/table/DataTable.vue");
    public final EsmName LilyAdmin = vue("ui/lily/LilyAdmin.vue");
    public final EsmName EntityChooseDialog = vue("ui/lily/EntityChooseDialog.vue");
    public final EsmName FieldGroup = vue("ui/lily/FieldGroup.vue");
    public final EsmName RefEditor = vue("ui/input/RefEditor.vue");

}
