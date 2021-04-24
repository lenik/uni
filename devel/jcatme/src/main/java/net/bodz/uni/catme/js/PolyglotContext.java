package net.bodz.uni.catme.js;

import static com.oracle.truffle.js.runtime.JSContextOptions.*;

import java.io.IOException;

import javax.script.ScriptException;

import org.graalvm.polyglot.*;

import net.bodz.bas.fn.EvalException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.io.ResourceResolver;
import net.bodz.uni.catme.io.RrFileSystem;

import com.oracle.truffle.js.lang.JavaScriptLanguage;

public class PolyglotContext
        implements
            IScriptContext {

    static final Logger logger = LoggerFactory.getLogger(PolyglotContext.class);

    Context context;
    Value bindings;

    public PolyglotContext(Context context) {
        this.context = context;
        bindings = context.getBindings("js");
    }

    public Context getContext() {
        return context;
    }

    public Value getBindings() {
        return bindings;
    }

    @Override
    public Object get(String name) {
        Value member = bindings.getMember(name);
        return ValueFn.convert(member);
    }

    @Override
    public void put(String name, Object value) {
        bindings.putMember(name, value);
    }

    @Override
    public Object include(String filename)
            throws EvalException, IOException {
        logger.debug("load/include " + filename);

        Value result = (Value) eval("load(\"" + filename + "\");", filename);
        return ValueFn.convert(result);
    }

    @Override
    public Object eval(String code)
            throws EvalException, IOException {
        Source source = Source.newBuilder("js", code, "<script>") //
                .mimeType("application/javascript+module").build();
        try {
            Value result = context.eval(source);
            return ValueFn.convert(result);
        } catch (PolyglotException e) {
            throw new EvalException("error eval " + code + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Object eval(String code, String fileName)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        if (fileName == null)
            throw new NullPointerException("fileName");
        Source source = Source.newBuilder("js", code, fileName).build();
        try {
            Value result = context.eval(source);
            return ValueFn.convert(result);
        } catch (PolyglotException e) {
            throw new EvalException("error eval " + code + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args)
            throws ScriptException, NoSuchMethodException {
        Value value = Value.asValue(thiz);
        Value result = value.invokeMember(name, args);
        return ValueFn.convert(result);
    }

    @Override
    public Object invokeFunction(String name, Object... args)
            throws ScriptException, NoSuchMethodException {
        Value fn = bindings.getMember(name);
        Value result = fn.execute(args);
        return ValueFn.convert(result);
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return bindings.as(clasz);
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        Value value = Value.asValue(thiz);
        return value.as(clasz);
    }

    public static PolyglotContext createContext(ResourceResolver resolver) {
        Context context = Context.newBuilder(JavaScriptLanguage.ID) //
                .fileSystem(new RrFileSystem(resolver)) //
                .allowAllAccess(true) //
                .allowExperimentalOptions(true) //
                .option(ECMASCRIPT_VERSION_NAME, "13") // 5 to 13 or 2015 to 2022.
                .option(INTEROP_COMPLETE_PROMISES_NAME, "true") //
                .option(STRICT_NAME, "true") //
                .build();
        return new PolyglotContext(context);
    }

    public static PolyglotContext createContextVerbose(ResourceResolver resolver) {
        Context context = Context.newBuilder(JavaScriptLanguage.ID) //
                .fileSystem(new RrFileSystem(resolver)) //
                .allowAllAccess(true) //
                .allowExperimentalOptions(true) //
                .option(ECMASCRIPT_VERSION_NAME, "13") // 5 to 13 or 2015 to 2022.
                .option(INTEROP_COMPLETE_PROMISES_NAME, "true") //
                .option(STRICT_NAME, "true") //
                .allowEnvironmentAccess(EnvironmentAccess.NONE) // //
                .allowCreateProcess(true) //
                .allowCreateThread(true) //
                .allowHostAccess(HostAccess.ALL) //
                .allowHostClassLoading(true) //
                .allowHostClassLookup(s -> true) //
                .allowIO(true) //
                .allowNativeAccess(true) //
                .allowPolyglotAccess(PolyglotAccess.ALL) //
                .allowExperimentalOptions(true) //
                .option(GRAAL_BUILTIN_NAME, "true") //
                .option(REGEXP_STATIC_RESULT_NAME, "true") //
                .option(JAVA_PACKAGE_GLOBALS_NAME, "true") //
                .option(PERFORMANCE_NAME, "false") //
                .option(PRINT_NAME, "true") //
                .option(LOAD_NAME, "true") //
                .option(SCRIPTING_NAME, "true") //
                .option(SHELL_NAME, "true") //
                .option(SHEBANG_NAME, "true") //
                .option(SYNTAX_EXTENSIONS_NAME, "true") //
                .option(DISABLE_EVAL_NAME, "false") //
                .option(LOAD_FROM_URL_NAME, "true") //
                .build();
        return new PolyglotContext(context);
    }

}