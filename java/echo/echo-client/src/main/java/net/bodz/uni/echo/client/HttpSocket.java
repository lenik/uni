package net.bodz.uni.echo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.http.HttpVersion;

import net.bodz.bas.c.string.StringPred;

public class HttpSocket {

    static final int PORT_HTTP = 80;
    // static final int PORT_HTTPS = 443;

    Socket socket;
    String host;
    int port;

    public HttpSocket(String hostPort)
            throws UnknownHostException, IOException {
        int colon = hostPort.lastIndexOf(':');
        if (colon != -1) {
            String portStr = hostPort.substring(colon + 1);
            if (StringPred.isDecimal(portStr)) {
                host = hostPort.substring(0, colon);
                port = Integer.parseInt(portStr);
            } else {
                host = hostPort;
                port = PORT_HTTP;
            }
        } else {
            host = hostPort;
            port = PORT_HTTP;
        }
        this.socket = new Socket(host, port);
    }

    public HttpSocket(String host, int port)
            throws UnknownHostException, IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
    }

    public HttpSocket(Socket socket) {
        this.socket = socket;
        SocketAddress remote = socket.getRemoteSocketAddress();
        if (remote instanceof InetSocketAddress) {
            InetSocketAddress inet = (InetSocketAddress) remote;
            host = inet.getHostString();
            port = inet.getPort();
        }
    }

    public HttpTester.Request newRequest(String uri) {
        return newRequest(HttpVersion.HTTP_1_1, uri);
    }

    public HttpTester.Request newRequest(HttpVersion httpVersion, String uri) {
        HttpTester.Request request = HttpTester.newRequest();
        request.put(HttpHeader.HOST, host + ":" + port);
        request.setVersion(httpVersion);
        request.setURI(uri);
        return request;
    }

    public HttpTester.Response call(HttpTester.Request request)
            throws IOException {
        ByteBuffer output = request.generate();

        socket.getOutputStream().write(output.array(), output.arrayOffset() + output.position(), output.remaining());

        HttpTester.Input input = HttpTester.from(socket.getInputStream());
        HttpTester.Response response = HttpTester.parseResponse(input);
        return response;
    }

}
