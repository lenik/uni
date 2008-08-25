package net.bodz.lapiota.ant.tasks;

import java.util.Enumeration;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;

@SuppressWarnings("all")
public class TestTask extends Task {

    // Expected:
    public List<Object>  expected;

    // Actual:
    private StringBuffer outbuf;
    private StringBuffer errbuf;

    private int          exitCode;

    @SuppressWarnings("unchecked")
    void config() {
        RuntimeConfigurable wrapper = getRuntimeConfigurableWrapper();
        Enumeration<RuntimeConfigurable> children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            RuntimeConfigurable child = children.nextElement();

        }
    }

    @Override
    public void execute() throws BuildException {
        config();
        outbuf = new StringBuffer();
        errbuf = new StringBuffer();
    }

    @Override
    protected void handleOutput(String output) {
        super.handleOutput(output);
        outbuf.append(output);
    }

    @Override
    protected void handleErrorOutput(String output) {
        super.handleErrorOutput(output);
        errbuf.append(output);
    }

}
