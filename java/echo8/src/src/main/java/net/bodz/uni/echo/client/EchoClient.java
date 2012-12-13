package net.bodz.uni.echo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.testing.HttpTester;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.err.control.ControlExit;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.t.pojo.Pair;
import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.server.EchoServer;
import net.bodz.uni.echo.test.HttpTesterException;

public class EchoClient {

    static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

    EchoServer server;
    EchoServerConfig config;
    String location = "";
    Map<String, IEchoClientCommand> commandMap = new TreeMap<String, IEchoClientCommand>();

    IPrintOut out;
    IPrintOut err;

    public EchoClient(EchoServer server) {
        if (server == null)
            throw new NullPointerException("server");
        this.server = server;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    Pair<String, String> splitHostURI(String urlOrPath) {
        String hostName;
        String uri;

        int pos = urlOrPath.indexOf("://");
        if (pos != -1) {
            String hostAndURI = urlOrPath.substring(pos + 3);
            int at = hostAndURI.indexOf('@');
            if (at != -1)
                hostAndURI = hostAndURI.substring(at + 1);

            int slash = hostAndURI.indexOf('/');
            if (slash == -1) {
                hostName = hostAndURI;
                uri = "/";
            } else {
                hostName = hostAndURI.substring(0, slash);
                uri = hostAndURI.substring(slash);
            }
        } else {
            hostName = config.getHostName("localhost");
            uri = urlOrPath;
        }
        return Pair.of(hostName, uri);
    }

    public HttpTester httpGet(URL url)
            throws Exception {
        return httpGet(url.toString());
    }

    public HttpTester httpGet(String urlOrPath)
            throws Exception {
        HttpTester request = new HttpTester();
        request.setMethod("GET");
        request.setVersion("HTTP/1.0");

        Pair<String, String> hostURI = splitHostURI(urlOrPath);

        request.setHeader("Host", hostURI.getFirst());
        request.setURI(hostURI.getSecond());

        String rawRequest = request.generate();

        String rawResponse = getResponses(rawRequest);
        HttpTester response = new HttpTester();
        response.parse(rawResponse);

        if (response.getStatus() >= 400)
            throw new HttpTesterException(response);

        return response;
    }

    public HttpTester httpPost(URL url, String content, Map<String, String> parameterMap)
            throws Exception {
        return httpPost(url.toString(), content, parameterMap);
    }

    public HttpTester httpPost(String uri, String content, Map<String, String> parameterMap)
            throws Exception {
        HttpTester request = new HttpTester();
        request.setMethod("POST");
        request.setVersion("HTTP/1.0");

        Pair<String, String> hostURI = splitHostURI(uri);

        request.setHeader("Host", hostURI.getFirst());
        request.setURI(hostURI.getSecond());
        request.setContent(content);

        String rawResponse = getResponses(request.generate());
        HttpTester response = new HttpTester();
        response.parse(rawResponse);
        return response;
    }

    public String getResponses(String rawRequests)
            throws Exception {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof LocalConnector) {
                LocalConnector lc = (LocalConnector) connector;
                String responses = lc.getResponses(rawRequests);
                if (responses != null)
                    return responses;
            }
        }
        return null;
    }

    public void dumpResponse(HttpTester http) {
        PrintStream out = System.err;
        out.println(http.getStatus() + " " + http.getReason());

        Enumeration<String> names = http.getHeaderNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String header = http.getHeader(name);
            out.println(name + ": " + header);
        }

        out.println();

        out.println(http.getContent());
    }

    public void mainLoop()
            throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            out.print("$ ");
            String line = stdin.readLine();
            if (line == null)
                break;

            line = line.trim();

            String[] args = StringQuoted.split(line, ' ');
            Pair<String, String[]> cmdArgs = Arrays.shift(args);
            String commandName = cmdArgs.getFirst();
            args = cmdArgs.getSecond();

            IEchoClientCommand command = commandMap.get(commandName);
            if (command == null) {
                logger.error("No such command: " + commandName);
                continue;
            }

            try {
                command.execute(args);
            } catch (ControlExit c) {
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
