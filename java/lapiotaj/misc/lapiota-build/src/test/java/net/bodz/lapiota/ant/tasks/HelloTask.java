package net.bodz.lapiota.ant.tasks;

public class HelloTask
        extends CLITask {

    public HelloTask() {
        super(new HelloCLI());
    }

    @Override
    public void execute()
            throws BuildException {
        super.execute();
        log("oh, task finished!"); //$NON-NLS-1$
    }

}
