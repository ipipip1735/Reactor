package netty;

import reactor.netty.http.server.HttpServer;

/**
 * Created by Administrator on 2019/11/11 4:41.
 */
public class HttpServerTrial {

    String ip = "192.168.0.127";
    int port = 8080;

    public static void main(String[] args) {
        HttpServerTrial httpServerTrial = new HttpServerTrial();
        httpServerTrial.request();
    }

    private void request() {

        HttpServer.create()   // Prepares an HTTP server ready for configuration
                .host(ip)
                .port(port)    // Configures the port number as zero, this will let the system pick up
                // an ephemeral port when binding the server
                .route(routes ->
                        // The server will respond only on POST requests
                        // where the path starts with /test and then there is path parameter
                        routes.get("/test/{param}", (request, response) ->
                                response.sendString(request.receive()
                                        .asString()
                                        .map(s -> s + ' ' + request.param("param") + '!')
                                        .log("http-server"))))
                .bindNow()
                .onDispose()
                .block(); // Starts the server in a blocking fashion, and waits for it to finish its initialization


    }
}
