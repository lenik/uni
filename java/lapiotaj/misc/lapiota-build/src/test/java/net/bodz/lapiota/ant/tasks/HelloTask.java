package net.bodz.lapiota.ant.tasks;

import org.apache.tools.ant.BuildException;

import net.bodz.bas.ant.task.CLITask;

public class HelloTask
        extends CLITask {

    public HelloTask() {
        super(new HelloCLI());
    }

    @Override
    public void execute()
            throws BuildException {
        super.execute();
        log("oh, task finished!");
    }

}
