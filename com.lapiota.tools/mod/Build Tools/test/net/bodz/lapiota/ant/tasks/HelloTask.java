package net.bodz.lapiota.ant.tasks;

import net.bodz.bas.cli.util.CLITask;

import org.apache.tools.ant.BuildException;

public class HelloTask extends CLITask {

    public HelloTask() {
        super(new HelloCLI());
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        log("oh, task finished!");
    }

}
