package netty;

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

        httpClientTrial.conn();

    }

    private void conn() {


        //方式一：分步使用
        HttpClient httpClient = HttpClient.create()
                .keepAlive(true)
                .headers(httpHeaders -> {
                    httpHeaders.add("xx", "y8y");
                })
                .compress(true);
        HttpClient.ResponseReceiver responseReceiver = httpClient.get();
        HttpClient.RequestSender requestSender = (HttpClient.RequestSender) responseReceiver.uri(uri);

        HttpClientResponse httpClientResponse = requestSender.response()
                .block();
        System.out.println("status is " + httpClientResponse.status());


//        httpClient.baseUrl();
//        HttpClient.RequestSender requestSender = (HttpClient.RequestSender) httpClient.get();


        //方式二：使用链式写法
//        HttpClientResponse response =
//                HttpClient.create()
//                        .get()
//                        .uri(uri)
//                        .response()
//                        .block();


        //方式三：使用链式写法
//        HttpClient.create()
//                .get()
//                .uri(uri)
//                .response((httpClientResponse, byteBufFlux) -> {
//                    System.out.println("~~response~~");
//                    System.out.println("status is " + httpClientResponse.status());
//                    System.out.println("responseHeaders is " + httpClientResponse.responseHeaders());
//                    System.out.println("currentContext is " + httpClientResponse.currentContext());
//                    System.out.println("cookies is " + httpClientResponse.cookies());
//
//
////                    byteBufFlux.asString()
////                            .doOnNext(byteBuf -> {
////                        System.out.println("~~doOnNext~~");
////                        System.out.println(byteBufFlux);
////                    }).subscribe();
//
//
//
//                    return Flux.just("oo");
//                })
//                .blockLast();


        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
