package net.bodz.lapiota.ant.tasks;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.ClassOptions;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.lang.script.ScriptClass;
import net.bodz.bas.lang.script.ScriptException;
import net.bodz.bas.log.ALog;
import net.bodz.bas.types.util.Empty;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;

public class CLITask extends Task {

    protected final BasicCLI       app;
    protected final ScriptClass<?> sclass;

    public CLITask(BasicCLI cliApp) {
        this.app = cliApp;
        try {
            sclass = cliApp.getScriptClass();
        } catch (ScriptException e) {
            throw new Error(e.getMessage(), e);
        }
    }

    private List<String> moreargs;
    private String       tailargs;
    private int          logLevel = 0;

    protected void addArguments(String... args) {
        if (moreargs == null)
            moreargs = new ArrayList<String>();
        for (String arg : args)
            moreargs.add(arg);
    }

    public String getCLITail() {
        return tailargs;
    }

    public void setCLITail(String tailargs) {
        this.tailargs = tailargs;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    protected Object get(String cliFieldName) {
        try {
            return sclass.get(app, cliFieldName);
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void set(String cliFieldName, Object newValue) {
        try {
            sclass.set(app, cliFieldName, newValue);
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void maybeConfigure() throws BuildException {
        RuntimeConfigurable wrapper = getRuntimeConfigurableWrapper();
        Hashtable<?, ?> attrs = wrapper.getAttributeMap();
        try {
            ClassOptions<BasicCLI> opts = app.getOptions();
            List<String> rawArgs = null;
            for (Map.Entry<?, ?> ent : attrs.entrySet()) {
                String optnam = (String) ent.getKey();
                String optarg = (String) ent.getValue();
                if (optnam.startsWith("arg-")) {
                    int argIndex = Integer.parseInt(optnam.substring(4));
                    if (rawArgs == null)
                        rawArgs = new ArrayList<String>(argIndex + 1);
                    while (rawArgs.size() <= argIndex)
                        rawArgs.add(null);
                    rawArgs.set(argIndex, optarg);
                    continue;
                }
                try {
                    opts.findOption(optnam);
                } catch (CLIException e) {
                    continue;
                }
                if (optnam.length() == 1)
                    opts.load(app, "-" + optnam + optarg);
                else
                    opts.load(app, "--" + optnam, optarg);
                wrapper.removeAttribute(optnam);
            }
            if (rawArgs != null) {
                for (int i = rawArgs.size() - 1; i >= 0; i--)
                    if (rawArgs.get(i) == null)
                        rawArgs.remove(i);
                String[] argv = rawArgs.toArray(Empty.Strings);
                app.addArguments(argv);
            }

            // children tags
            Enumeration<RuntimeConfigurable> children = wrapper.getChildren();
            while (children.hasMoreElements()) {
                RuntimeConfigurable child = children.nextElement();
                String name = child.getElementTag();
                String value = child.getText().toString();
                if ("arg".equals(name))
                    app.addArguments(value);
                else
                    throw new BuildException("invalid child tag: " + name);
            }
        } catch (CLIException e) {
            throw new BuildException(e.getMessage(), e);
        } catch (ParseException e) {
            throw new BuildException(e.getMessage(), e);
        }

        super.maybeConfigure();
    }

    @Override
    public void execute() throws BuildException {
        try {
            if (moreargs != null && !moreargs.isEmpty()) {
                String[] moreargv = moreargs.toArray(Empty.Strings);
                app.addArguments(moreargv);
            }
            // adapting attributes
            if (logLevel != 0) {
                ALog L = (ALog) sclass.get(app, "logout");
                L.setLevel(L.getLevel() + logLevel);
            }
        } catch (CLIException e) {
            throw new BuildException(e.getMessage(), e);
        } catch (ParseException e) {
            throw new BuildException(e.getMessage(), e);
        } catch (ScriptException e) {
            throw new BuildException(e.getMessage(), e);
        }
        try {
            app.run(tailargs);
        } catch (Throwable e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private StringBuffer errbuf = new StringBuffer();

    @Override
    protected void handleErrorOutput(String output) {
        errbuf.append(output);
        if (output.endsWith("\n")) {
            super.handleErrorOutput(errbuf.toString());
            errbuf.setLength(0);
        }
    }

}
