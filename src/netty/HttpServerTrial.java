package netty;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/11/11 4:41.
 */
public class HttpServerTrial {

    String host = "localhost";
    String ip = "192.168.0.126";
    int port = 8080;

    public static void main(String[] args) {
        HttpServerTrial httpServerTrial = new HttpServerTrial();
        httpServerTrial.request();
//        httpServerTrial.response();
    }

    private void response() {

        HttpServer.create()
                .host(ip)
                .port(port)
                .route(httpServerRoutes ->
                    httpServerRoutes.get("/test/{p}", (request, response) -> {

                        return response.addCookie(new DefaultCookie("xx", "yy"))
                        .addHeader("xx", "yy")
                        .chunkedTransfer(true)
                        .compression(true)
                        .keepAlive(true)
                        .sendString(Mono.just("tt")).then();
                    })
                )
                .bindNow()
                .onDispose()
                .block();


    }

    private void request() {

        HttpServer.create()
//                .host(host)
                .host(ip)
                .port(port)
                .route(routes ->
                        routes.get("/test/{param}", (request, response) -> {

                                    System.out.println("hostAddress is " + request.hostAddress());
                                    System.out.println("scheme is " + request.scheme());
                                    System.out.println("param is " + request.param("param"));
                                    System.out.println("remoteAddress is " + request.remoteAddress());
                                    System.out.println("requestHeaders is " + request.requestHeaders());
                                    System.out.println(request.params());

                                    System.out.println("cookies is " + request.cookies());
                                    System.out.println("isKeepAlive is " + request.isKeepAlive());
                                    System.out.println("isWebsocket is " + request.isWebsocket());
                                    System.out.println("method is " + request.method());
                                    System.out.println("path is " + request.path());
                                    System.out.println("uri is " + request.uri());
                                    System.out.println("version is " + request.version());

                                    System.out.println("-------------");

                                    request.paramsResolver(s -> {
                                        System.out.println("paramsResolver is " + s);
                                        return new HashMap<>();
                                    });

                                    request.receiveContent().doOnNext(httpContent -> {
                                        System.out.println("receiveContent is " + httpContent);
                                    });

                                    request.withConnection(connection -> {
                                        System.out.println("connection is " + connection);
                                    });


                                    System.out.println("-------------");

                                    request.receive()
                                            .doOnNext(byteBuf -> {
                                                System.out.println("~~doOnNext~~");
                                                System.out.println(byteBuf);
                                            });

//                                    request.receiveObject()
//                                            .doOnNext(byteBuf -> {
//                                                System.out.println("~~doOnNext~~");
//                                                System.out.println(byteBuf);
//                                            });



                                    return response.sendString(Mono.just("ok")).then();
                                }
                        ))
                .bindNow()
                .onDispose()
                .block();
    }
}
