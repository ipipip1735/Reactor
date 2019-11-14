package netty;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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

//        httpClientTrial.baseUrl();
//        httpClientTrial.conn();
//        httpClientTrial.addressSupplier();
        httpClientTrial.get();
//        httpClientTrial.httpClientResponse();

    }

    private void httpClientResponse() {

        //方式一：分步使用
//        HttpClient httpClient = HttpClient.create()
//                .keepAlive(true)
//                .headers(httpHeaders -> {
//                    httpHeaders.add("xx", "y8y");
//                })
//                .baseUrl(uri)
//                .compress(true);
//        HttpClient.ResponseReceiver responseReceiver = httpClient.get();
//        HttpClient.RequestSender requestSender = (HttpClient.RequestSender) responseReceiver;
//        HttpClientResponse httpClientResponse = requestSender
//                .response()
//                .block();
//        System.out.println("status is " + httpClientResponse.status());
//        System.out.println("responseHeaders is " + httpClientResponse.responseHeaders());


        //方式二：使用链式写法
//        HttpClientResponse httpClientResponse =
//                HttpClient.create()
//                        .get()
//                        .uri(uri)
//                        .response()
//                        .block();
//        System.out.println("status is " + httpClientResponse.status());
//        System.out.println("responseHeaders is " + httpClientResponse.responseHeaders());


        //方式三
        HttpClient.create()
                .get()
                .uri(uri)
                .response((httpClientResponse, byteBufFlux) -> {
                    System.out.println("~~response~~");
                    System.out.println("status is " + httpClientResponse.status());
                    System.out.println("responseHeaders is " + httpClientResponse.responseHeaders());
                    System.out.println("currentContext is " + httpClientResponse.currentContext());
                    System.out.println("cookies is " + httpClientResponse.cookies());


                    byteBufFlux.asString()
                            .subscribeOn(Schedulers.elastic())
                            .doOnNext(byteBuf -> {
                        System.out.println("~~doOnNext~~");
                        System.out.println(byteBufFlux);
                    }).then().subscribe();


                    return Flux.just("oo");
                })
                .blockLast();

    }

    private void addressSupplier() {

        HttpClient.create()
//                .baseUrl("http://192.168.0.126:8080")
                .addressSupplier(() -> {
                    System.out.println("~~addressSupplier~~");
                    return new InetSocketAddress(ip, 8080);
                })
                .protocol(HttpProtocol.HTTP11)
                .get()
//                .uri("/test/ddd")
                .response()
                .block();

    }

    private void get() {

        HttpClient.create()
                .get()
                .uri(uri)
                .responseContent()
                .asString()
                .doOnNext(s -> {
                    System.out.println("~~doOnNext~~");
                    System.out.println(s);
                })
                .then()
                .block();


    }

    private void baseUrl() {
//        HttpClient.create()
//                .baseUrl("http://192.168.0.126:8080")
//                .get()
//                .uri("/test/ddd")
//                .response()
//                .block();


//        HttpClient.create()
//                .baseUrl("http://192.168.0.126:8080/test")
//                .get()
//                .uri("/ddd")
//                .response()
//                .block();


    }

    private void conn() {


    }
}
