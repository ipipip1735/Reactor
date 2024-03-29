package netty;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.nio.file.Path;
import java.nio.file.Paths;
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
//        httpServerTrial.request();
//        httpServerTrial.response();

        httpServerTrial.handle();

//        httpServerTrial.directory();
//        httpServerTrial.file();
//        httpServerTrial.index();
    }

    private void index() {
        HttpServer.create()
                .host(ip)
                .port(port)
                .route(httpServerRoutes -> {
                    httpServerRoutes
                            .index((httpServerRequest, httpServerResponse) -> {
                                System.out.println("~~index~~");
                                System.out.println(httpServerRequest.requestHeaders());
                                return httpServerResponse.sendString(Mono.just("AAA"));
                            });
                })
                .bindNow()
                .onDispose()
                .block();


    }

    private void file() {
        HttpServer.create()
                .host(ip)
                .port(port)
                .route(httpServerRoutes -> {
                    httpServerRoutes
                            .file("/test", Paths.get("res/test"));
                })
                .bindNow()
                .onDispose()
                .block();
    }

    private void directory() {

        HttpServer.create()
                .host(ip)
                .port(port)
                .route(httpServerRoutes -> {
                    httpServerRoutes
                            .directory("/aa/bb", Paths.get("res"), httpServerResponse -> {
                                System.out.println("~~directory~~");
                                return httpServerResponse;
                            });

                })
                .bindNow()
                .onDispose()
                .block();
    }

    private void handle() {

        //方式一：最简使用
        HttpServer.create()
                .host(ip)
                .port(port)
                .handle((httpServerRequest, httpServerResponse) -> {
                    System.out.println("~~handle~~");
                    System.out.println(httpServerRequest.requestHeaders());
                    httpServerRequest.requestHeaders().add("one", "111");
                    return httpServerResponse.sendString(Mono.just("HHH"));
                })
                .bindNow()
                .onDispose()
                .block();

        //方式二：配合路由一起使用（谁先增加，谁先调用）
//        HttpServer.create()
//                .host(ip)
//                .port(port)
//                .handle((httpServerRequest, httpServerResponse) -> {//增加处理器（无条件调用）
//                    System.out.println("~~handle~~");
//                    System.out.println(httpServerRequest.requestHeaders());
//                    httpServerRequest.requestHeaders().add("one", "111");
//                    return httpServerResponse.neverComplete();
//                })
//                .route(httpServerRoutes -> //增加路由处理器（URL匹配才调用）
//                        httpServerRoutes.get("/test", (httpServerRequest, httpServerResponse) -> {
//                            System.out.println("~~get~~");
//                            System.out.println(httpServerRequest.requestHeaders());
//
//                            return httpServerResponse.sendString(Mono.just("AAA"));
//                        }))
//                .bindNow()
//                .onDispose()
//                .block();

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

        //POST请求
        HttpServer.create()
//                .host(host)
                .host(ip)
                .port(port)
                .route(routes ->
                        routes.post("/test/ddd", (request, response) -> {

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
//                                        connection.addHandlerLast(null);//增加处理器到管线
                                    });


                                    System.out.println("-------------");

                                    return request.receive()
                                            .doOnNext(byteBuf -> {
                                                System.out.println("~~doOnNext~R~");
                                                System.out.println(byteBuf);

                                            }).then(response.sendString(Mono.just("post ok")).then());

//                                  return  request.receiveObject()
//                                            .doOnNext(byteBuf -> {
//                                                System.out.println("~~doOnNext~~");
//                                                System.out.println(byteBuf);
//                                            });
                                }
                        ))
                .bindNow()
                .onDispose()
                .block();


        //GET请求
//        HttpServer.create()
////                .host(host)
//                .host(ip)
//                .port(port)
//                .route(routes ->
//                        routes.get("/test/{param}", (request, response) -> {
//
//                                    System.out.println("hostAddress is " + request.hostAddress());
//                                    System.out.println("scheme is " + request.scheme());
//                                    System.out.println("param is " + request.param("param"));
//                                    System.out.println("remoteAddress is " + request.remoteAddress());
//                                    System.out.println("requestHeaders is " + request.requestHeaders());
//                                    System.out.println(request.params());
//
//                                    System.out.println("cookies is " + request.cookies());
//                                    System.out.println("isKeepAlive is " + request.isKeepAlive());
//                                    System.out.println("isWebsocket is " + request.isWebsocket());
//                                    System.out.println("method is " + request.method());
//                                    System.out.println("path is " + request.path());
//                                    System.out.println("uri is " + request.uri());
//                                    System.out.println("version is " + request.version());
//
//                                    System.out.println("-------------");
//
//                                    request.paramsResolver(s -> {
//                                        System.out.println("paramsResolver is " + s);
//                                        return new HashMap<>();
//                                    });
//
//                                    request.receiveContent().doOnNext(httpContent -> {
//                                        System.out.println("receiveContent is " + httpContent);
//                                    });
//
//                                    request.withConnection(connection -> {
//                                        System.out.println("connection is " + connection);
//                                    });
//
//
//                                    System.out.println("-------------");
//
//                                    request.receive()
//                                            .doOnNext(byteBuf -> {
//                                                System.out.println("~~doOnNext~~");
//                                                System.out.println(byteBuf);
//                                            });
//
//                                    request.receiveObject()
//                                            .doOnNext(byteBuf -> {
//                                                System.out.println("~~doOnNext~~");
//                                                System.out.println(byteBuf);
//                                            });
//
//
//                                    return response.sendString(Mono.just("ok")).then();
//                                }
//                        ))
//                .bindNow()
//                .onDispose()
//                .block();
    }
}
