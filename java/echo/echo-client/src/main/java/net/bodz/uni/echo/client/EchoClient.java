package net.bodz.uni.echo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;

import net.bodz.bas.c.java.util.ArrayAndScalar;
import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.err.control.ControlExit;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.servlet.config.ServletContextConfig;
import net.bodz.bas.t.pojo.Pair;
import net.bodz.uni.echo.server.EchoServer;
import net.bodz.uni.echo.test.HttpTesterException;

public class EchoClient {

    static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

    EchoServer server;
    ServletContextConfig config;
    String location = "";

    Map<String, IEchoClientCommand> commandMap = new TreeMap<String, IEchoClientCommand>();
    BrowseCommand browseCommand;

    IPrintOut out = Stdio.cout;
    IPrintOut err = Stdio.cerr;

    public EchoClient(EchoServer server) {
        if (server == null)
            throw new NullPointerException("server");
        this.server = server;
        this.config = server.getConfig();

        commandMap.put("p", new PrintCommand(this, out));
        commandMap.put("b", browseCommand = new BrowseCommand(this, config));
        commandMap.put("h", new HelpCommand(this, out));
        commandMap.put("q", new QuitCommand(server));
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

    public Response httpGet(URL url)
            throws Exception {
        return httpGet(url.toString());
    }

    public Response httpGet(String urlOrPath)
            throws Exception {
        Pair<String, String> hostURI = splitHostURI(urlOrPath);

        Request request = new Request();
        request.setMethod("GET");
        request.setVersion("HTTP/1.0");

        request.setHeader("Host", hostURI.first);
        request.setURI(hostURI.second);

        ByteBuffer rawRequest = request.generate();
        ByteBuffer rawResponse = getResponse(rawRequest);
        Response response = HttpTester.parseResponse(rawResponse);

        if (response.getStatus() >= 400)
            throw new HttpTesterException(response);

        return response;
    }

    public Response httpPost(URL url, String content, Map<String, String> parameterMap)
            throws Exception {
        return httpPost(url.toString(), content, parameterMap);
    }

    public Response httpPost(String uri, String content, Map<String, String> parameterMap)
            throws Exception {
        Pair<String, String> hostURI = splitHostURI(uri);

        Request request = new Request();
        request.setMethod("POST");
        request.setVersion("HTTP/1.0");

        request.setHeader("Host", hostURI.getFirst());
        request.setURI(hostURI.getSecond());
        request.setContent(content);

        ByteBuffer rawRequest = request.generate();
        ByteBuffer rawResponse = getResponse(rawRequest);
        Response response = HttpTester.parseResponse(rawResponse);

        return response;
    }

    public ByteBuffer getResponse(ByteBuffer rawRequest)
            throws Exception {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof LocalConnector) {
                LocalConnector lc = (LocalConnector) connector;
                return lc.getResponse(rawRequest);
            }
        }
        return null;
    }

    public void dumpResponse(Response response) {
        PrintStream out = System.err;
        out.println(response.getStatus() + " " + response.getReason());

        Enumeration<String> names = response.getFieldNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            HttpField header = response.getField(name);
            out.println(header);
        }

        out.println();

        out.println(response.getContent());
    }

    public void go(String location)
            throws IOException {
        setLocation(location);

        try {
            browseCommand.execute();
        } catch (Exception e) {
            logger.error(e);
        }

        mainLoop();
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
            if (line.isEmpty())
                continue;

            String[] args = StringQuoted.split(line, ' ');
            ArrayAndScalar<String[], String> cmdArgs = Arrays.shift(args);
            String commandName = cmdArgs.scalar;
            args = cmdArgs.array;

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
