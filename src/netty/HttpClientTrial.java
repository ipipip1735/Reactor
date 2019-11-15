package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/11/13 23:00.
 */
public class HttpClientTrial {

    String host = "localhost";
    String ip = "192.168.0.126";
    int port = 8080;
    String uri = "http://" + ip + ":" + port + "/test/ddd";

    public static void main(String[] args) {

        HttpClientTrial httpClientTrial = new HttpClientTrial();

//        httpClientTrial.config();
//        httpClientTrial.connection();
//        httpClientTrial.response();
//        httpClientTrial.hooks();

        httpClientTrial.request();


    }

    private void request() {


        HttpClient.create()
                .post()
                .uri(uri)
                .send(Mono.just(Unpooled.wrappedBuffer("OOO".getBytes())))
                .response((httpClientResponse, byteBufFlux) -> {
                    System.out.println(httpClientResponse.status());
                    return byteBufFlux.asString();
                })
                .doOnNext(System.out::println)
                .then()
                .block();


    }

    private void connection() {

        HttpClient.create()
                .mapConnect((mono, bootstrap) ->
                        mono.map(conn -> {
                            System.out.println(conn);
                            conn.addHandlerLast(null);
                            System.out.println(bootstrap);
                            return conn;
                        })
                )
                .get()
                .uri(uri)
                .response()
                .block();

    }

    private void hooks() {

        //方式一
//        HttpClient.create()
//                .doOnRequest((httpClientRequest, connection) -> {
//                    System.out.println("~~doOnRequest~~");
//                    System.out.println(httpClientRequest);
//                    System.out.println(connection);
//                })
//                .doAfterRequest((httpClientRequest, connection) -> {
//                    System.out.println("~~doAfterRequest~~");
//                    System.out.println(httpClientRequest);
//                    System.out.println(connection);
//                })
//                .doOnRequestError((httpClientRequest, connection) -> {
//                    System.out.println("~~doOnRequestError~~");
//                    System.out.println(httpClientRequest);
//                    System.out.println(connection);
//                })
//                .doOnResponse((httpClientRequest, connection) -> {
//                    System.out.println("~~doOnResponse~~");
//                    System.out.println(httpClientRequest);
//                    System.out.println(connection);
//                })
//                .doAfterResponse((httpClientResponse, connection) -> {
//                    System.out.println("~~doAfterResponse~~");
//                    System.out.println(httpClientResponse);
//                    System.out.println(connection);
//                })
//                .doOnError((httpClientRequest, throwable) -> {
//                    System.out.println("~~doOnError~Reqest~");
//                },(httpClientResponse, throwable) -> {
//                    System.out.println("~~doOnError~Response~");
//                })
//                .doOnResponseError((HttpClientResponse, throwable) -> {
//                    System.out.println("~~doOnResponseError~~");
//                })
//                .get()
//                .uri(uri)
//                .response()
//                .doOnNext(httpClientResponse -> {
//                    System.out.println("~~doOnNext~~");
//                    System.out.println(httpClientResponse.status());
//                })
//                .then()
//                .block();


        //方式二：使用response()
        HttpClient.create()
                .doOnResponse((httpClientResponse, connection) -> {
                    System.out.println("~~doOnResponse~~");
                    System.out.println("connection is " + connection);
                    System.out.println("httpClientResponse is " + httpClientResponse);
                    System.out.println(httpClientResponse.status());
                    System.out.println(httpClientResponse.responseHeaders());

                })
                .doAfterResponse((httpClientResponse, connection) -> {
                    System.out.println("~~doAfterResponse~~");
                    System.out.println("connection is " + connection);
                    System.out.println("httpClientResponse is " + httpClientResponse);
                    System.out.println(httpClientResponse.status());
                    System.out.println(httpClientResponse.responseHeaders());

                })
                .get()
                .uri(uri)
                .response((httpClientResponse, byteBufMono) -> {
                    System.out.println("~~response~~");
                    System.out.println(httpClientResponse.status());
                    System.out.println(httpClientResponse.responseHeaders());
                    return byteBufMono.asString();
                })
                .doOnNext(System.out::println)
                .then()
                .block();

        //方式三：使用responseSingle()
//        HttpClient.create()
//                .doOnResponse((httpClientResponse, connection) -> {
//                    System.out.println("~~doOnResponse~~");
//                    System.out.println("connection is " + connection);
//                    System.out.println("httpClientResponse is " + httpClientResponse);
//                    System.out.println(httpClientResponse.status());
//                    System.out.println(httpClientResponse.responseHeaders());
//
//                })
//                .doAfterResponse((httpClientResponse, connection) -> {
//                    System.out.println("~~doAfterResponse~~");
//                    System.out.println("connection is " + connection);
//                    System.out.println("httpClientResponse is " + httpClientResponse);
//                    System.out.println(httpClientResponse.status());
//                    System.out.println(httpClientResponse.responseHeaders());
//
//                })
//                .get()
//                .uri(uri)
//                .responseSingle((httpClientResponse, byteBufMono) -> {
//                    System.out.println("~~responseSingle~~");
//                    return byteBufMono.asString()
//                            .doOnNext(s -> {
//                                System.out.println(s);
//                            })
//                            .then();
//                })
//                .block();


//        try {
//            Thread.sleep(2000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

    private void response() {

        //方式一：获取结果
        HttpClient.create()
                .get()
                .uri(uri)
                .response((httpClientResponse, byteBufMono) -> {
                    System.out.println("~~responseSingle~~");
                    System.out.println(httpClientResponse);
                    System.out.println(httpClientResponse.status());
                    System.out.println(httpClientResponse.responseHeaders());

                    return byteBufMono.asString();
                })
                .doOnNext(System.out::println)
                .then()
                .block();


        //方式二：获取单值
        HttpClient.create()
                .get()
                .uri(uri)
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    System.out.println("~~responseSingle~~");
                    System.out.println(httpClientResponse);
                    System.out.println(httpClientResponse.status());
                    System.out.println(httpClientResponse.responseHeaders());

                    return byteBufMono.asString();
                })
                .doOnNext(System.out::println)
                .then()
                .block();
    }

    private void config() {


        //方式一：分步使用
//        HttpClient httpClient = HttpClient.create()
//                .keepAlive(true)//启用长连接
//                .headers(httpHeaders -> {
//                    httpHeaders.add("xx", "y8y");
//                })
//                .compress(true);
//        HttpClient.ResponseReceiver responseReceiver = httpClient.get();
//        HttpClient.RequestSender requestSender = (HttpClient.RequestSender) responseReceiver.uri(uri);
//
//        HttpClientResponse httpClientResponse = requestSender.response()
//                .block();
//        System.out.println("status is " + httpClientResponse.status());


        //方式二：使用链式写法
        HttpClientResponse response =
                HttpClient.create()
                        .keepAlive(true)//启用长连接
                        .headers(httpHeaders -> {
                            httpHeaders.add("xx", "y8y");
                        })
                        .compress(true)
                        .get()
                        .uri(uri)
                        .response()
                        .block();

        System.out.println(response.responseHeaders());
        System.out.println(response.status());
    }
}
