package net.bodz.uni.echo.test;

import org.eclipse.jetty.testing.HttpTester;

public class HttpTesterException
        extends Exception {

    private static final long serialVersionUID = 1L;

    private final HttpTester tester;

    public HttpTesterException(HttpTester tester) {
        super(tester.getStatus() + " " + tester.getReason());

        this.tester = tester;
    }

    public HttpTester getTester() {
        return tester;
    }

}
