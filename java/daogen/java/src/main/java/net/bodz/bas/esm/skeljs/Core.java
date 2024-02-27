package net.bodz.bas.esm.skeljs;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.SourcePath;

public class Core
        extends EsmModule {

    public Core(int priority) {
        super("@skeljs/core", "src", priority);
    }

    @SourcePath("lang/basetype")
    public class BaseType
            extends Source {
        public final EsmName _byte = type("byte");
        public final EsmName _short = type("short");
        public final EsmName integer = type("integer");
        public final EsmName _long = type("long");
        public final EsmName _float = type("float");
        public final EsmName _double = type("double");
        public final EsmName _char = type("char");
        // public final EsmName _boolean = type("boolean");
//        public final EsmName string = type("string");
        public final EsmName BigInteger = type("BigInteger");
        public final EsmName BigDecimal = type("BigDecimal");
    }

    @SourcePath("lang/typespec")
    public class TypeSpec
            extends Source {
        public final EsmName BYTE = value("BYTE");
        public final EsmName SHORT = value("SHORT");
        public final EsmName INT = value("INT");
        public final EsmName LONG = value("LONG");
        public final EsmName FLOAT = value("FLOAT");
        public final EsmName DOUBLE = value("DOUBLE");
        public final EsmName BIG_INTEGER = value("BIG_INTEGER");
        public final EsmName BIG_DECIMAL = value("BIG_DECIMAL");
        public final EsmName BOOLEAN = value("BOOLEAN");
        public final EsmName CHAR = value("CHAR");
        public final EsmName STRING = value("STRING");
        public final EsmName DATE = value("DATE");
    }

    public class LangTime {
        public final EsmName Instant = defaultClass("lang/time/Instant");
        public final EsmName LocalDate = defaultClass("lang/time/LocalDate");
        public final EsmName LocalTime = defaultClass("lang/time/LocalTime");
        public final EsmName LocalDateTime = defaultClass("lang/time/LocalDateTime");
        public final EsmName OffsetTime = defaultClass("lang/time/OffsetTime");
        public final EsmName OffsetDateTime = defaultClass("lang/time/OffsetDateTime");
        public final EsmName ZonedDateTime = defaultClass("lang/time/ZonedDateTime");
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

    public final BaseType baseType = add(new BaseType());
    public final TypeSpec typeSpec = add(new TypeSpec());
    public final LangTime time = new LangTime();

    public final LoggingApi loggingApi = add(new LoggingApi());
    public final UiTypes uiTypes = add(new UiTypes());
    public final VarMap VarMap = add(new VarMap());

    public final EsmName Dialog = vue("ui/Dialog.vue");
    public final EsmName FieldRow = vue("ui/FieldRow.vue");

}
