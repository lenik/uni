package net.bodz.uni.echo.test;

import org.eclipse.jetty.http.HttpTester.Response;

public class HttpTesterException
        extends Exception {

    private static final long serialVersionUID = 1L;

    private final Response response;

    public HttpTesterException(Response resp) {
        super(resp.getStatus() + " " + resp.getReason());

        this.response = resp;
    }

    public Response getResponse() {
        return response;
    }

}
