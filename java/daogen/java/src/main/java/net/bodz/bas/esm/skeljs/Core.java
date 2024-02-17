package net.bodz.bas.esm.skeljs;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.SourcePath;

public class Core
        extends EsmModule {

    public Core(int priority) {
        super("@skeljs/core", "src", priority);
    }

    @SourcePath("logging/api")
    public class LoggingApi
            extends Source {

        public final EsmName LogLevel = type("LogLevel");
        public final EsmName Exception = _interface("Exception");
        public final EsmName StackTrackElement = _interface("StackTrackElement");
        public final EsmName LogEntry = _interface("LogEntry");
        public final EsmName levelIcon = name("levelIcon");
        public final EsmName simpleName = name("simpleName");
        public final EsmName typeLabel = name("typeLabel");
        public final EsmName causes = name("causes");
        public final EsmName parseException = name("parseException");
        public final EsmName Logger = _interface("Logger");

    }

    @SourcePath("ui/types")
    public class UiTypes
            extends Source {

        public final EsmName bool = name("bool");
        public final EsmName UiGroupItem = _interface("UiGroupItem");
        public final EsmName UiComponent = _interface("UiComponent");
        public final EsmName NameSet = _interface("NameSet");
        public final EsmName Selector = _interface("Selector");
        public final EsmName select = name("select");
        public final EsmName UiGroup = _interface("UiGroup");
        public final EsmName UiGroupMap = _interface("UiGroupMap");
        public final EsmName group = name("group");
        public final EsmName DialogAction = type("DialogAction");
        public final EsmName EventHandler = type("EventHandler");
        public final EsmName Href = type("Href");
        public final EsmName Command = _interface("Command");
        public final EsmName DialogSelectCallback = type("DialogSelectCallback");
        public final EsmName AsyncDialogSelectCallback = type("AsyncDialogSelectCallback");
        public final EsmName CommandBehavior = _interface("CommandBehavior");
        public final EsmName getDialogCmds = name("getDialogCmds");
        public final EsmName IValidateResult = _interface("IValidateResult");
        public final EsmName ValidateResult = _interface("ValidateResult");
        public final EsmName Validator = type("Validator");
        public final EsmName MessageFunc = type("MessageFunc");
        public final EsmName Status = _interface("Status");

    }

    @SourcePath("lang/VarMap")
    public class VarMap
            extends Source {

        public final EsmName INameMap = _interface("INameMap");
        public final EsmName INamedStrings = _interface("INamedStrings");
        public final EsmName VarMap = _interface("VarMap");

    }

    public final LoggingApi loggingApi = add(new LoggingApi());
    public final UiTypes uiTypes = add(new UiTypes());
    public final VarMap VarMap = add(new VarMap());

    public final EsmName Dialog = vue("ui/Dialog.vue");
    public final EsmName FieldGroup = vue("ui/FieldGroup.vue");
    public final EsmName FieldRow = vue("ui/FieldRow.vue");

}
